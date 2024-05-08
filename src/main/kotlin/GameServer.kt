import com.mattbobambrose.prisoner.common.EndpointNames.PARTICIPANTS
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

object GameServer {
    val tournamentRequests = Channel<GameParticipant>()

    @JvmStatic
    fun main(args: Array<String>) {
        thread {
            runBlocking {
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
                    for (participant in tournamentRequests) {
                        val participantURL = participant.url
                        println("Received: $participantURL")
                        delay(1.seconds)
                        val response = client.get("${participantURL}/$PARTICIPANTS")
                        val fqns = response.body<List<StrategyFqn>>()
                        with(Tournament(participantURL, fqns, 1)) {
                            runSimulation(participant.rules)
                            reportScores()
                        }
                    }
                }
            }
        }
        embeddedServer(
            CIO,
            port = 8081,
            host = "0.0.0.0",
            module = Application::gameServerModule
        ).start(wait = true)
    }
}