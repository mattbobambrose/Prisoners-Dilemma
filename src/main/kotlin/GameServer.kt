import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.Constants.GENERATION_COUNT
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Utils.createHttpClient
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.game_server.Game
import com.mattbobambrose.prisoner.game_server.SuspendingCountDownLatch
import com.mattbobambrose.prisoner.game_server.gameServerModule
import com.mattbobambrose.prisoner.player_server.Competition
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class GameServer {
    val competitionMap = ConcurrentHashMap<CompetitionId, Competition>()
    private val httpServer = embeddedServer(
        Netty,
        port = GAME_SERVER_PORT,
        host = "0.0.0.0",
        module = Application::gameServerModule
    )

    init {
        thread {
            runBlocking {
                for (request in gameRequestChannel) {
                    with(pendingGameRequestsMap) {
                        putIfAbsent(request.competitionId, mutableListOf())
                        get(request.competitionId)?.add(request)
                            ?: error("Error adding participant")
                    }
                }
            }
        }

        thread {
            runBlocking {
                for ((competitionId, gameLatch) in pendingCompetitionChannel) {
                    logger.info { "Starting game ${competitionId.id}" }
                    launch { playGame(competitionId, gameLatch) }
                }
            }
        }
    }

    fun startServer() {
        httpServer.start(wait = false)
    }

    fun stopServer() {
        gameRequestChannel.close()
        pendingCompetitionChannel.close()
        httpServer.stop(1000, 1000)
    }

    suspend fun playGame(competitionId: CompetitionId, gameLatch: SuspendingCountDownLatch) {
        createHttpClient().use { client ->
            val requests = pendingGameRequestsMap.remove(competitionId)
                ?: error("No participants for game ${competitionId.id}")
            val strategyInfoList =
                requests.map { request ->
                    val requestURL = request.url
                    client.get("${requestURL}/$STRATEGYFQNS?$COMPETITION_ID=${competitionId.id.encode()}&username=${request.username.name.encode()}")
                        .body<List<StrategyFqn>>()
                        .map { strategyFqn ->
                            StrategyInfo(
                                requestURL,
                                request.username,
                                strategyFqn
                            )
                        }
                }.flatten()

            with(Game(competitionId, strategyInfoList, GENERATION_COUNT)) {
                gameLatch.countDown()
                gameList.add(this)
                runGame(requests.first().rules)
                reportScores()
                competitionMap[competitionId]?.onCompletion()
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
        val gameRequestChannel = Channel<GameRequest>()
        val pendingCompetitionChannel = Channel<Pair<CompetitionId, SuspendingCountDownLatch>>()
        private val pendingGameRequestsMap =
            ConcurrentHashMap<CompetitionId, MutableList<GameRequest>>()

        // TODO Not being purged
        val gameList = mutableListOf<Game>()

        @JvmStatic
        fun main(args: Array<String>) {
            GameServer().startServer()
        }

        fun findGame(competitionId: CompetitionId): Game? =
            gameList.find { it.competitionId == competitionId }

        fun pendingGames(): List<CompetitionId> = pendingGameRequestsMap.keys.toList()
    }
}