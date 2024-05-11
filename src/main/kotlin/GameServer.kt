import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.HttpObjects.TournamentRequest
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.game_server.Tournament
import com.mattbobambrose.prisoner.game_server.gameServerModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

object GameServer {
    val tournamentRequests = Channel<TournamentRequest>()
    val pendingGameRequestsMap = ConcurrentHashMap<GameId, MutableList<TournamentRequest>>()
    val playChannel = Channel<GameId>()

    @JvmStatic
    fun main(args: Array<String>) {
        thread {
            runBlocking {
                for (request in tournamentRequests) {
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
                for (gameId in playChannel) {
                    println("Playing game $gameId")
                    playGame(gameId.id)
                }
            }
        }
        embeddedServer(CIO, port = 8081, host = "0.0.0.0", module = Application::gameServerModule)
            .start(wait = true)
    }

    suspend fun playGame(gameId: String) {
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
            val requests = pendingGameRequestsMap.remove(GameId(gameId))
                ?: error("No participants for game $gameId")
            println("Participants size: ${requests.size}")
            val infoList =
                requests.map { request ->
                    val requestURL = request.url
                    println("Received: $requestURL")
                    client.get("${requestURL}/$STRATEGYFQNS?gameId=$gameId&username=${request.username.name}")
                        .body<List<StrategyFqn>>()
                        .map { StrategyInfo(requestURL, request.username, it) }
                }.flatten()
            println("Playing game with $infoList")
            println("infoList size: ${infoList.size}")
            with(Tournament(infoList, 1)) {
                runSimulation(requests.first().rules)
                reportScores()
            }
        }
    }

    fun currentGameIds(): List<GameId> = pendingGameRequestsMap.keys().toList()
}