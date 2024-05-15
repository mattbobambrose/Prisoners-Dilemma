package com.mattbobambrose.prisoner.game_server

import GameServer
import GameServer.pendingGames
import GameServer.playChannel
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.EndpointNames.MOREDETAILS
import com.mattbobambrose.prisoner.common.EndpointNames.PLAY
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.EndpointNames.SCOREBOARD
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYHISTORY
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.channels.Channel
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
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.thead
import kotlinx.html.tr

fun Application.gameServerRouting() {
    routing {
//        val debug = this@gameServerRouting.environment.config
//            .propertyOrNull("ktor.deployment.debug")?.getString()?.toBoolean() ?: false
        get("/") {
            call.respondHtml { // HTML
                body {
                    h1 { +"Prisoner's Dilemma" }
                    a { href = "/$PLAY"; +"Start Game" }
                }
            }
        }

        get("/$GO") {
            println("Go")
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            println("GameId: $gameId")
            with(Channel<Boolean>()) {
                playChannel.send(GameId(gameId) to this)
                receive()
                close()
            }
            call.respondRedirect("/$SCOREBOARD?gameId=$gameId")
        }

        get("/$SCOREBOARD") {
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = "/style.css" }
                }
                body {
                    div {
                        h1 { +"Scoreboard for game $gameId" }
                        table(classes = "scores") {
                            thead {
                                tr {
                                    td { +"Rank" }
                                    td { +"Username" }
                                    td { +"Strategy type" }
                                    td { +"Score" }
                                }
                            }
                            tbody {
                                val game =
                                    GameServer.getGame(GameId(gameId)) ?: error("Game not found")
                                game.generationList.forEachIndexed { genIndex, generation ->
                                    generation
                                        .sortedScores()
                                        .forEachIndexed { index, (info, scorecard) ->
                                            tr {
                                                td { +"${index + 1}" }
                                                td { +info.username.name }
                                                td { +info.fqn.name }
                                                td { +"${scorecard.totalPoints}" }
                                                td {
                                                    a {
                                                        href =
                                                            "/$MOREDETAILS?gameId=$gameId&fqn=${info.fqn}"
                                                        +"More details"
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }

        get("/$MOREDETAILS") {
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            val fqn = call.request.queryParameters["fqn"] ?: error("Missing info")
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = "/style.css" }
                }
                body {
                    div {
                        h1 { +"More details for fqn $fqn" }
                        table(classes = "scores") {
                            thead {
                                tr {
                                    td { +"Generation number" }
                                    td { +"Opponent strategy" }
                                    td { +"Points won" }
                                    td { +"Outcome" }
                                    td { +"Decision history" }
                                }
                            }
                            tbody {
                                val game =
                                    GameServer.getGame(GameId(gameId)) ?: error("Game not found")
                                game.generationList.forEachIndexed { index, generation ->
                                    generation.matchList.filter {
                                        it.getFqnStrings().contains(fqn)
                                    }.forEach {
                                        tr {
                                            td { +"${index + 1}" }
                                            td { +it.getOpponentFqn(fqn) }
                                            td { +"${it.getScore(fqn)}" }
                                            td { +it.getOutcome(fqn) }
                                            td {
                                                a {
                                                    href =
                                                        "/$STRATEGYHISTORY?gameId=$gameId&fqn=$fqn&matchId=${it.matchId}"
                                                    +"Decision history"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        get("/$STRATEGYHISTORY") {
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            val fqn = call.request.queryParameters["fqn"] ?: error("Missing fqn")
            val matchId = call.request.queryParameters["matchId"] ?: error("Missing matchId")
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = "/style.css" }
                }
                body {
                    div {
                        h1 { +"Strategy history for fqn $fqn" }
                        table(classes = "scores") {
                            val game =
                                GameServer.getGame(GameId(gameId)) ?: error("Game not found")
                            val match = game.getMatch(matchId) ?: error("Match not found")
                            thead {
                                tr {
                                    td { +"Round number" }
                                    td { +"Your decision: " }
                                    td { +"Opponent decision" }
                                    td { +"Your points" }
                                    td { +"Opponent points" }
                                }
                            }
                            tbody {
                                match.moves.forEachIndexed { index, move ->
                                    val playerOpponentChoice = if (move.p1Info.fqn.name == fqn) {
                                        move.p1Choice to move.p2Choice
                                    } else {
                                        move.p2Choice to move.p1Choice
                                    }
                                    val playerOpponentScore = if (move.p1Info.fqn.name == fqn) {
                                        move.p1Score to move.p2Score
                                    } else {
                                        move.p2Score to move.p1Score
                                    }
                                    tr {
                                        td { +"${index + 1}" }
                                        td { +playerOpponentChoice.first.name }
                                        td { +playerOpponentChoice.second.name }
                                        td { +"${playerOpponentScore.first}" }
                                        td { +"${playerOpponentScore.second}" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        post("/$REGISTER") {
            val participant = call.receive<GameRequest>()
            println("Registered: $participant")
            GameServer.gameRequests.send(participant)
            call.respondText("Registered")
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
                            pendingGames().forEach { gameId ->
                                tr {
                                    td {
                                        button(classes = "playButton") {
                                            onClick =
                                                "window.location.href='/$GO?gameId=${gameId.id}'"
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