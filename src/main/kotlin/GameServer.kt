import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.Constants.GENERATION_COUNT
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.game_server.Game
import com.mattbobambrose.prisoner.game_server.SuspendingCountDownLatch
import com.mattbobambrose.prisoner.game_server.gameServerModule
import com.mattbobambrose.prisoner.player_server.Competition
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.channels.Channel
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
                for (request in gameRequests) {
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
                for ((gameId, gameLatch) in playChannel) {
                    println("Playing game ${gameId.id}")
                    playGame(gameId, gameLatch)
                }
            }
        }
    }

    fun startServer() {
        httpServer.start(wait = false)
    }

    fun stopServer() {
        httpServer.stop(1000, 1000)
    }

    suspend fun playGame(competitionId: CompetitionId, gameLatch: SuspendingCountDownLatch) {
        HttpClient(io.ktor.client.engine.cio.CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                println("Configuring ContentNegotiation...")
                json()
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
        }.use { client ->
            val requests = pendingGameRequestsMap.remove(competitionId)
                ?: error("No participants for game ${competitionId.id}")
            val infoList =
                requests.map { request ->
                    val requestURL = request.url
                    client.get("${requestURL}/$STRATEGYFQNS?gameId=${competitionId.id.encode()}&username=${request.username.name.encode()}")
                        .body<List<StrategyFqn>>()
                        .map { StrategyInfo(requestURL, request.username, it) }
                }.flatten()

            with(Game(competitionId, infoList, GENERATION_COUNT)) {
                gameLatch.countDown()
                gameList.add(this)
                runSimulation(requests.first().rules)
                reportScores()
                competitionMap[competitionId]?.onCompletion()
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        val gameRequests = Channel<GameRequest>()
        val playChannel = Channel<Pair<CompetitionId, SuspendingCountDownLatch>>()
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