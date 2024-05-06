import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.h3
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

object GameEngine {
    @JvmStatic
    fun main(args: Array<String>) {
        thread {
            runBlocking {
                for (fqns in tournamentRequests) {
                    println("Received: $fqns")
                    delay(3.seconds)
                    Tournament(fqns, 1).runSimulation()

                }
            }
        }
        embeddedServer(CIO, port = 8081, host = "0.0.0.0", module = Application::gameEngineModule)
            .start(wait = true)
    }

    val tournamentRequests = Channel<List<String>>()
}

fun Application.gameEngineModule() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
//            call.respondText(text = "404: I'm sorry that page is not present", status = status)
            call.respondHtml(status = status) {
                body {
                    h3 { +"404: I'm sorry that page is not present" }
                }
            }
        }
    }

    install(Resources)
    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/participants") {
            println("I am here")
            val fqns = call.receive<List<String>>()
            GameEngine.tournamentRequests.send(fqns)
            call.respondText("Thank you!")
        }
        get("/") {
            call.respondText("Hello World1!")
        }
    }
}