import HttpObjects.StrategyArgs
import HttpObjects.StrategyResponse
import Server.strategyList
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import strategy.*
import java.time.Duration

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
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

@Serializable
data class Simple(val name: String)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/participants") {
            call.respond(Json.encodeToString(strategyList.map { it.fqn }))
        }

        post("/strategy/{fqn}/play") {
            val fqn = call.parameters["fqn"] ?: error("invalid strategy")
            val strategy = Server.strategyMap[fqn] ?: error("invalid fqn: $fqn")
            val body = call.receiveText()
            val args = Json.decodeFromString<StrategyArgs>(body)
            val decision = with(args) {
                strategy.chooseOption(roundNumber, opponentFqn, myMoves, opponentMoves)
            }
            call.respond(Json.encodeToString(StrategyResponse(decision)))
        }
    }
    routing {
        post("/json/kotlinx-serialization") {
            val body = call.receiveText()
            val agent = call.request.headers[HttpHeaders.UserAgent]
            val secret = call.request.headers["my_secret"]
            val list = Json.decodeFromString<List<Int>>(body)
            val s = Json.encodeToString(Simple("hello: $list $agent $secret"))
            call.respondText(s)
        }
    }

    routing {
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
        status(HttpStatusCode.NotFound) { call, status ->
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


    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World1!")
        }
        get("matt.html") {
            call.respondHtml { // HTML
                head {
                    title { +"HTML Application" }
                }
                body {
                    h1 { +"Sample HTML Page" }
                    p {
                        +"This is a sample HTML page"
                    }
                }
            }
        }
        get("hello") {
            val uri = call.parameters.getAll("name")?.joinToString(", ") ?: "no uri"
            call.respondHtml { // HTML
                head {
                    title { +"HTML Application" }
                }
                body {
                    h1 { +"Sample HTML Page" }
                    p {
                        +"Hello $uri"
                    }
                }
            }
        }

        get("strategy/{baseid}") {
            val strategy = call.parameters["baseid"] ?: "no strategy"
//            val roundNumber = call.request.queryParameters["roundNumber"] ?: "no roundNumber"
            val roundNumber = call.parameters["roundNumber"] ?: "no roundNumber"
            call.respondText("Strategy: $strategy, roundNumber: $roundNumber")
        }

        for (n in 1..10) {
            get("/html-$n") {
                call.respondHtml {
                    body {
                        h1 { +"HTML $n" }
                        ul {
                            for (i in 1..n) {
                                li { +"$i" }
                            }
                        }
                    }
                }
            }
        }
    }
}