import com.mattbobambrose.prisoner.common.EndpointNames.PARTICIPANTS
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
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
    val tournamentRequests = Channel<GameParticipant>()
    val pendingRequests = ConcurrentHashMap<GameId, MutableList<GameParticipant>>()

    @JvmStatic
    fun main(args: Array<String>) {
        thread {
            runBlocking {
                for (participant in tournamentRequests) {
                    with(pendingRequests) {
                        putIfAbsent(participant.gameId, mutableListOf())
                        get(participant.gameId)?.add(participant)
                            ?: error("Error adding participant")
                    }
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
            val participants = pendingRequests.remove(GameId(gameId))
                ?: error("No participants for game $gameId")
            val infoList =
                participants.map { participant ->
                    val participantURL = participant.url
                    println("Received: $participantURL")
                    client.get("${participantURL}/$PARTICIPANTS")
                        .body<List<StrategyFqn>>()
                        .map {
                            StrategyInfo(
                                participantURL,
                                Username(participant.username.name),
                                StrategyFqn(it.fqn)
                            )
                        }
                }.flatten()
            with(Tournament(infoList, 1)) {
                runSimulation(participants.first().rules)
                reportScores()
            }
        }
    }
}