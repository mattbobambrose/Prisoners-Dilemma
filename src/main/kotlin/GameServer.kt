import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.CONCURRENT_MATCHES
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.Constants.GENERATION_COUNT
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.game_server.CallTransport
import com.mattbobambrose.prisoner.game_server.ClientContext
import com.mattbobambrose.prisoner.game_server.Game
import com.mattbobambrose.prisoner.game_server.KRpcTransport
import com.mattbobambrose.prisoner.game_server.LocalTransport
import com.mattbobambrose.prisoner.game_server.RestTransport
import com.mattbobambrose.prisoner.game_server.SuspendingCountDownLatch
import com.mattbobambrose.prisoner.game_server.TransportType
import com.mattbobambrose.prisoner.game_server.TransportType.GRPC
import com.mattbobambrose.prisoner.game_server.TransportType.KRPC
import com.mattbobambrose.prisoner.game_server.TransportType.LOCAL
import com.mattbobambrose.prisoner.game_server.TransportType.REST
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
            threadCompleteLatch.countDown()
        }

        thread {
            runBlocking {
                ClientContext().use { clientContext ->
                    val callTransport: CallTransport =
                        when (transportType) {
                            LOCAL -> LocalTransport(this@GameServer)
                            REST -> RestTransport(clientContext.httpClient)
                            GRPC -> throw NotImplementedError("gRPC not supported")
                            KRPC -> KRpcTransport(clientContext.krpcClient)
                        }
                    for ((competitionId, gameLatch) in pendingCompetitionChannel) {
                        logger.info { "Starting game ${competitionId.id}" }
                        launch { playGame(callTransport, competitionId, gameLatch) }
                    }
                }
            }
            threadCompleteLatch.countDown()
        }
    }

    fun startServer() {
        if (transportType.requiresHttp) {
            httpServer = embeddedServer(
                io.ktor.server.netty.Netty,
                port = GAME_SERVER_PORT,
                host = "0.0.0.0",
                module = {
//                    gameServerModule(this@GameServer, competitionMap)
                })
            httpServer.start(wait = false)
        }
    }

    fun stopServer() {
        gameRequestChannel.close()
        pendingCompetitionChannel.close()
        threadCompleteLatch.await()
        if (transportType.requiresHttp) {
            httpServer.stop(1000, 1000)
        }
    }

    suspend fun playGame(
        callTransport: CallTransport,
        competitionId: CompetitionId,
        gameLatch: SuspendingCountDownLatch
    ) {
        val requests = pendingGameRequestsMap.remove(competitionId)
            ?: error("No participants for game ${competitionId.id}")
        val strategyInfoList =
            requests.map { request ->
                callTransport.getStrategyFqnList(competitionId, request)
                    .map { strategyFqn ->
                        StrategyInfo(request.url, request.username, strategyFqn)
                    }
            }.flatten()

        with(Game(this, competitionId, strategyInfoList, GENERATION_COUNT)) {
            gameLatch.countDown()
            gameList.add(this)
            runGame(callTransport, requests.first().rules)
            reportScores()
            competitionMap[competitionId]?.onCompletion()
        }
    }

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