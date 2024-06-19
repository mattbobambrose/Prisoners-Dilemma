import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.Constants.GENERATION_COUNT
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.game_server.Game
import com.mattbobambrose.prisoner.game_server.SuspendingCountDownLatch
import com.mattbobambrose.prisoner.game_server.gameServerModule
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
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class GameServer {
    val completionCountDownLatchMap = mutableMapOf<GameId, CountDownLatch>()
    val httpServer = embeddedServer(
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
                        putIfAbsent(request.gameId, mutableListOf())
                        get(request.gameId)?.add(request)
                            ?: error("Error adding participant")
                    }
                }
            }
        }
        thread {
            runBlocking {
                for ((gameId, latch) in playChannel) {
                    println("Playing game ${gameId.id}")
                    playGame(gameId, latch)
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

    suspend fun playGame(gameId: GameId, latch: SuspendingCountDownLatch) {
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
            val requests = pendingGameRequestsMap.remove(gameId)
                ?: error("No participants for game ${gameId.id}")
            println("Participants size: ${requests.size}")
            val infoList =
                requests.map { request ->
                    val requestURL = request.url
                    println("Received: $requestURL")
                    client.get("${requestURL}/$STRATEGYFQNS?gameId=${gameId.id.encode()}&username=${request.username.name.encode()}")
                        .body<List<StrategyFqn>>()
                        .map { StrategyInfo(requestURL, request.username, it) }
                }.flatten()
            println("Playing game with $infoList")
            println("infoList size: ${infoList.size}")
            with(Game(gameId, infoList, GENERATION_COUNT)) {
                latch.countDown()
                gameList.add(this)
                runSimulation(requests.first().rules)
                reportScores()
                completionCountDownLatchMap[gameId]?.countDown()
            }
        }
    }

    companion object {
        val gameRequests = Channel<GameRequest>()
        val playChannel = Channel<Pair<GameId, SuspendingCountDownLatch>>()
        private val pendingGameRequestsMap = ConcurrentHashMap<GameId, MutableList<GameRequest>>()

        // TODO Not being purged
        val gameList = mutableListOf<Game>()

        @JvmStatic
        fun main(args: Array<String>) {
            GameServer().startServer()
        }

        fun findGame(gameId: GameId): Game? = gameList.find { it.gameId == gameId }

        fun pendingGames(): List<GameId> = pendingGameRequestsMap.keys.toList()
    }
}