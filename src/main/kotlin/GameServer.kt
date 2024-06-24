import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.CONCURRENT_MATCHES
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.Constants.GENERATION_COUNT
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.game_server.Game
import com.mattbobambrose.prisoner.game_server.SuspendingCountDownLatch
import com.mattbobambrose.prisoner.game_server.TransportType
import com.mattbobambrose.prisoner.game_server.TransportType.REST
import com.mattbobambrose.prisoner.game_server.gameServerModule
import com.mattbobambrose.prisoner.player_server.Competition
import com.mattbobambrose.prisoner.player_server.PlayerDSL.GameServerContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class GameServer(val transportType: TransportType = REST) {
    var concurrentMatches = CONCURRENT_MATCHES
    val competitionMap = ConcurrentHashMap<CompetitionId, Competition>()
    val gameRequestChannel = Channel<GameRequest>()
    val pendingCompetitionChannel = Channel<Pair<CompetitionId, SuspendingCountDownLatch>>()
    val gameServerContext = GameServerContext(this)
    val threadCompleteLatch = CountDownLatch(2)
    private lateinit var httpServer: NettyApplicationEngine

    fun startChannelThreads() {
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
            threadCompleteLatch.countDown()
        }

        thread {
            runBlocking {
                for ((competitionId, gameLatch) in pendingCompetitionChannel) {
                    logger.info { "Starting game ${competitionId.id}" }
                    launch { playGame(competitionId, gameLatch) }
                }
            }
            threadCompleteLatch.countDown()
        }
    }

    fun startGameServer() {
        if (transportType.requiresHttp) {
            httpServer = embeddedServer(
                io.ktor.server.netty.Netty,
                port = GAME_SERVER_PORT,
                host = "0.0.0.0",
                module = {
                    gameServerModule(this@GameServer, competitionMap)
                })
            httpServer.start(wait = false)
        }
        startChannelThreads()
    }

    fun stopGameServer() {
        gameRequestChannel.close()
        pendingCompetitionChannel.close()
        threadCompleteLatch.await()
        if (transportType.requiresHttp) {
            httpServer.stop(1000, 1000)
        }
    }

    suspend fun playGame(
        competitionId: CompetitionId,
        gameLatch: SuspendingCountDownLatch
    ) {
        val requests = pendingGameRequestsMap.remove(competitionId)
            ?: error("No game requests for ${competitionId.id}")
        val competition = competitionMap[competitionId] ?: error("Competition not found")
        logger.info { "Playing game for $competitionId" }
        val strategyInfoList =
            requests.map { request ->
                val player = competition.lookUpPlayer(request.portNumber)
                val strategyFqnList =
                    player.callTransport.getStrategyFqnList(competitionId, request)
                strategyFqnList.map { strategyFqn ->
                    StrategyInfo(request.url, request.portNumber, request.username, strategyFqn)
                }
            }.flatten()

        with(Game(this, competitionId, strategyInfoList, GENERATION_COUNT)) {
            gameLatch.countDown()
            gameList.add(this)
            runGame(requests.first().rules)
            reportScores()
            competitionMap[competitionId]?.onCompletion()
        }
    }

    fun getCompetition(competitionId: CompetitionId): Competition =
        competitionMap[competitionId] ?: error("Competition not found: $competitionId")

    companion object {
        val logger = KotlinLogging.logger {}
        private val pendingGameRequestsMap =
            ConcurrentHashMap<CompetitionId, MutableList<GameRequest>>()

        // TODO Not being purged
        val gameList = mutableListOf<Game>()

        fun findGame(competitionId: CompetitionId): Game? =
            gameList.find { it.competitionId == competitionId }

        fun pendingGames(): List<CompetitionId> = pendingGameRequestsMap.keys.toList()
    }
}