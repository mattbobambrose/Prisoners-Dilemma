package com.mattbobambrose.prisoner.game_server

import GameServer
import GameServer.Companion.logger
import GameServer.Companion.pendingGames
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Constants.FQN
import com.mattbobambrose.prisoner.common.Constants.MATCH_ID
import com.mattbobambrose.prisoner.common.EndpointNames.CSS_SOURCE
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.EndpointNames.MOREDETAILS
import com.mattbobambrose.prisoner.common.EndpointNames.PLAY
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.EndpointNames.SCOREBOARD
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYHISTORY
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.Utils.encode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
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
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.thead
import kotlinx.html.tr

fun Application.gameServerRouting(gameServer: GameServer) {
    routing {
        staticResources("/static", "static")
        staticResources("/css", "css")
        get("/") {
            call.respondHtml { // HTML
                head {
                    link { rel = "stylesheet"; href = CSS_SOURCE }
                }
                body {
                    h1 { +"Prisoner's Dilemma" }
                    a { href = "/$PLAY"; +"Start Game" }
                }
            }
        }

        get("/$PLAY") {
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = CSS_SOURCE }
                }
                body {
                    div(classes = "playButtons") {
//                        style = "margin-left: 40px;"
                        h1 { +"Prisoner's Dilemma" }
                        p { +"Click the button to start the game" }
                        table(classes = "playButtons") {
                            pendingGames().forEach { competitionId ->
                                tr {
                                    td {
                                        button(classes = "playButton") {
                                            onClick =
                                                "window.location.href='/$GO?$COMPETITION_ID=${competitionId.id.encode()}'"
                                            +"Play Game ${competitionId.id}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        get("/$GO") {
            val competitionId =
                call.request.queryParameters[COMPETITION_ID] ?: error("Missing $COMPETITION_ID")
            val gameLatch = SuspendingCountDownLatch()
            gameServer.pendingCompetitionChannel.send(CompetitionId(competitionId) to gameLatch)
            logger.info { "Competition $competitionId requested to start" }
            gameLatch.await()
//            call.respondRedirect("/$SCOREBOARD?$COMPETITION_ID=${competitionId.encode()}")
            call.respondText("Registered")
        }

        post("/$REGISTER") {
            val participant = call.receive<GameRequest>()
            logger.info { "Registered: $participant" }
            gameServer.gameRequestChannel.send(participant)
            call.respondText("Registered")
        }

        get("/$SCOREBOARD") {
            val competitionId =
                call.request.queryParameters[COMPETITION_ID] ?: error("Missing $COMPETITION_ID")
            val game =
                GameServer.findGame(CompetitionId(competitionId)) ?: error("Game not found")
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = CSS_SOURCE }
                    if (!game.isFinished) {
                        script { type = "text/javascript"; src = "static/refreshPage.js" }
                    }
                }
                body {
                    div {
                        h1 { +"Scoreboard for game $competitionId" }
                        h2 {
                            +if (game.isFinished)
                                "Game ${game.competitionId.id} is finished"
                            else
                                "Game in progress: ${game.competitionId.id}"
                        }
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
                                game.generationList.forEachIndexed { genIndex, generation ->
                                    tr { +"Generation ${genIndex + 1}" }
                                    generation
                                        .sortedScores()
                                        .forEachIndexed { index, scorecard ->
                                            tr {
                                                td { +"${index + 1}" }
                                                td { +scorecard.strategyInfo.username.name }
                                                td { +scorecard.strategyInfo.fqn.name }
                                                td { +"${scorecard.totalPoints}" }
                                                td {
                                                    a {
                                                        href =
                                                            "/$MOREDETAILS?$COMPETITION_ID=$competitionId&genIndex=$genIndex&fqn=${scorecard.strategyInfo.fqn}"
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
            val competitionId =
                call.request.queryParameters[COMPETITION_ID] ?: error("Missing $COMPETITION_ID")
            val genIndex = call.request.queryParameters["genIndex"] ?: error("Missing genIndex")
            val fqn = call.request.queryParameters[FQN] ?: error("Missing info")
            val game =
                GameServer.findGame(CompetitionId(competitionId)) ?: error("Game not found")
            val matchList: List<Match> =
                buildList {
                    game.generationList.forEach { generation ->
                        add(generation.matchList.filter {
                            it.getFqnStrings().contains(fqn)
                        })
                    }
                }.flatten()

            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = CSS_SOURCE }
                    if (!matchList.all { it.isFinished }) {
                        script { type = "text/javascript"; src = "static/refreshPage.js" }
                    }
                }
                body {
                    div {
                        h1 { +"More details for fqn $fqn" }
                        if (matchList.all { it.isFinished }) {
                            h2 { +"All matches are finished for strategy $fqn" }
                        } else {
                            h2 { +"Some matches are still in progress for strategy $fqn" }
                        }
                        a {
                            href = "/$SCOREBOARD?$COMPETITION_ID=$competitionId"
                            +"Back to scoreboard"
                        }
                        table(classes = "scores") {
                            thead {
                                tr {
                                    td { +"Generation number" }
                                    td { +"Opponent strategy" }
                                    td { +"Round count" }
                                    td { +"Current state" }
                                    td { +"Points won" }
                                    td { +"Outcome" }
                                    td { +"Decision history" }
                                }
                            }
                            tbody {
                                matchList.forEach {
                                    tr {
                                        td { +genIndex }
                                        td { +it.getOpponentFqn(fqn) }
                                        td { +"${it.moves.size}" }
                                        td { +it.getFinishString() }
                                        td { +"${it.getScore(fqn)}" }
                                        td { +it.getOutcome(it.isFinished, fqn) }
                                        td {
                                            a {
                                                href =
                                                    "/$STRATEGYHISTORY?$COMPETITION_ID=$competitionId&fqn=$fqn&matchId=${it.matchId}"
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

        get("/$STRATEGYHISTORY") {
            val competitionId =
                call.request.queryParameters[COMPETITION_ID] ?: error("Missing $COMPETITION_ID")
            val game = GameServer.findGame(CompetitionId(competitionId)) ?: error("Game not found")
            val fqn = call.request.queryParameters[FQN] ?: error("Missing fqn")
            val matchId = call.request.queryParameters[MATCH_ID] ?: error("Missing matchId")
            val match = game.getMatch(matchId) ?: error("Match not found")
            val opponentFqn = match.getOpponentFqn(fqn)
            call.respondHtml {
                head {
                    link { rel = "stylesheet"; href = CSS_SOURCE }
                    if (!match.isFinished) {
                        script { type = "text/javascript"; src = "static/refreshPage.js" }
                    }
                }
                body {
                    div {
                        h1 { +"Strategy history for fqn $fqn" }
                        if (match.isFinished) {
                            h2 { +"Match ${match.matchId.id} against $opponentFqn is finished" }
                        } else {
                            h2 { +"Match ${match.matchId.id} against $opponentFqn is in progress" }
                        }
                        a {
                            href = "/$MOREDETAILS?$COMPETITION_ID=$competitionId&fqn=$fqn"
                            +"Back to strategy details"
                        }
                        a {
                            href = "/$SCOREBOARD?$COMPETITION_ID=$competitionId"
                            +"Back to scoreboard"
                        }
                        table(classes = "scores") {
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
                                    val playerOpponentChoice =
                                        if (move.p1Info.fqn.name == fqn) {
                                            move.p1Choice.s to move.p2Choice.s
                                        } else {
                                            move.p2Choice.s to move.p1Choice.s
                                        }
                                    val playerOpponentScore = if (move.p1Info.fqn.name == fqn) {
                                        move.p1Score to move.p2Score
                                    } else {
                                        move.p2Score to move.p1Score
                                    }
                                    tr {
                                        td { +"${index + 1}" }
                                        td { +playerOpponentChoice.first }
                                        td { +playerOpponentChoice.second }
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
    }
}