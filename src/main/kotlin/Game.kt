import EndpointNames.REGISTER
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import strategy.GameStrategy

class Game(vararg strategies: GameStrategy) {
    init {
        strategyList.addAll(strategies)
    }

    fun play() {
        register()
        strategyList.forEach { strategy ->
            strategyMap[strategy.fqn] = strategy
        }
        embeddedServer(CIO, port = 8082, host = "0.0.0.0", module = Application::playerModule)
            .start(wait = true)
    }

    companion object {
        val strategyList: MutableList<GameStrategy> = mutableListOf()
        val strategyMap: MutableMap<String, GameStrategy> = mutableMapOf()

        fun register() {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:8081/$REGISTER") {
                        contentType(ContentType.Application.Json)
                        setBody(HttpObjects.GameParticipant("http://localhost:8082", 10))
                    }
                }
            }
        }
    }
}