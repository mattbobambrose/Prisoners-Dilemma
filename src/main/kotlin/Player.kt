import EndpointNames.PARTICIPANTS
import EndpointNames.STRATEGY
import HttpObjects.StrategyArgs
import HttpObjects.StrategyResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul
import strategy.AlwaysCoop
import strategy.AlwaysDefect
import strategy.GameStrategy
import strategy.Joss
import strategy.TitForTat
import java.time.Duration

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        HttpClient(io.ktor.client.engine.cio.CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }.use { client ->
            runBlocking {
                client.post("http://localhost:8081/$PARTICIPANTS") {
                    contentType(ContentType.Application.Json)
                    setBody(strategyList.map { it.fqn })
                }
            }
        }


        embeddedServer(CIO, port = 8082, host = "0.0.0.0", module = Application::playerModule)
            .start(wait = true)
    }

    val strategyList = mutableListOf<GameStrategy>()
    val strategyMap: Map<String, GameStrategy>

    init {
        with(strategyList) {
            add(AlwaysCoop())
            add(AlwaysDefect())
            add(TitForTat())
            add(Joss())
        }
        strategyMap = strategyList.associateBy { it.fqn }
    }
}

fun Application.playerModule() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/$STRATEGY/{fqn}") {
            val fqn = call.parameters["fqn"] ?: error("invalid strategy")
            val strategy = Player.strategyMap[fqn] ?: error("invalid fqn: $fqn")
            val args = call.receive<StrategyArgs>()
            val decision = with(args) {
                strategy.chooseOption(roundNumber, opponentFqn, myMoves, opponentMoves)
            }
            call.respond(StrategyResponse(decision))
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }
    }

    install(StatusPages) {
        status(io.ktor.http.HttpStatusCode.NotFound) { call, status ->
//            call.respondText(text = "404: I'm sorry that page is not present", status = status)
            call.respondHtml(status = status) {
                body {
                    h3 { +"404: I'm sorry that page is not present" }
                }
            }
//            call.respondRedirect("https://kotlinlang.org/docs/sequences.html#from-chunks", false)
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/ws") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }

    routing {
        get("matt.html") {
            call.respondHtml { // HTML
                body {
                    h1 { +"Sample HTML Page" }
                    p {
                        +"This is a sample HTML page"
                    }
                }
            }
        }
    }
}