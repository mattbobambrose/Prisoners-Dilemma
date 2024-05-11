package com.mattbobambrose.prisoner.game_server

import GameServer
import GameServer.currentGameIds
import GameServer.playChannel
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.EndpointNames.PLAY
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.TournamentRequest
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr

fun Application.gameServerRouting() {
    routing {
//        val debug = this@gameServerRouting.environment.config
//            .propertyOrNull("ktor.deployment.debug")?.getString()?.toBoolean() ?: false

        get("/$GO") {
            println("Go")
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            println("GameId: $gameId")
            playChannel.send(GameId(gameId))
            call.respondText("Game $gameId started")
        }

        post("/$REGISTER") {
            val participant = call.receive<TournamentRequest>()
            println("Registered: $participant")
            GameServer.tournamentRequests.send(participant)
            call.respondText("Registered")
        }

        get("/") {
            call.respondHtml { // HTML
                body {
                    h1 { +"Prisoner's Dilemma" }
                    a {
                        href = "/$PLAY"
                        +"Start Game"
                    }
                }
            }
        }

        get("/$PLAY") {
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = "/style.css" }
                }
                body {
                    div(classes = "playButtons") {
//                        style = "margin-left: 40px;"
                        h1 { +"Prisoner's Dilemma" }
                        p { +"Click the button to start the game" }
                        table(classes = "playButtons") {
                            currentGameIds().forEach { gameId ->
                                tr {
                                    td {
                                        button(classes = "playButton") {
                                            onClick =
                                                "window.location.href='/go?gameId=${gameId.id}'"
                                            +"Play Game ${gameId.id}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        get("/style.css") {
            call.respondText(
                """
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f0f0f0;
                }
                h1 {
                    color: #ff0000;
                }
                .playButtons {
                    margin-left: 20px;
                }
                .playButton {
                    width: 150px;
                    font-size: 20px;
                    border-radius: 12px;
                    background-color: #93c5fd;
                }
                """,
                contentType = ContentType.Text.CSS
            )
        }
    }
}