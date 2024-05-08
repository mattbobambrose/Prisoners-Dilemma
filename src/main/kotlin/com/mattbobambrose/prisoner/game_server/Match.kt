package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyArgs
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyResponse
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.setJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Match(
    private val participantURL: String,
    private val fqn1: StrategyFqn,
    private val fqn2: StrategyFqn,
    private val scoreboard: Scoreboard,
    private val rules: Rules
) {
    private val moves = mutableListOf<Moves>()
    private var score1 = 0
    private var score2 = 0

    suspend fun runMatch(client: HttpClient) {
        for (i in 0 until rules.rounds) {
            val response1: HttpResponse =
                client.post("$participantURL/$STRATEGY/${fqn1.fqn}") {
                    setJsonBody(StrategyArgs(i, fqn2, makeHistory(fqn1), makeHistory(fqn2)))
                }

            val response2: HttpResponse =
                client.post("$participantURL/$STRATEGY/${fqn2.fqn}") {
                    setJsonBody(StrategyArgs(i, fqn1, makeHistory(fqn2), makeHistory(fqn1)))
                }
            if (i % 10 == 0) {
                delay(500.milliseconds)
            }
            val d1 = response1.body<StrategyResponse>().decision
            val d2 = response2.body<StrategyResponse>().decision

            moves.add(Moves(d1, d2))
            updateMatchScore(d1, d2)
        }
        scoreboard.updateScores(fqn1, fqn2, score1, score2)
    }

    private fun updateMatchScore(d1: Decision, d2: Decision) {
        with(rules) {
            if (d1 == COOPERATE) {
                if (d2 == COOPERATE) {
                    score1 += bothWinPoints
                    score2 += bothWinPoints
                } else {
                    score1 += lossPoints
                    score2 += winPoints
                }
            } else {
                if (d2 == COOPERATE) {
                    score1 += winPoints
                    score2 += lossPoints
                } else {
                    score1 += bothLosePoints
                    score2 += bothLosePoints
                }
            }
        }
    }

    private fun makeHistory(fqn: StrategyFqn) =
        moves.map { if (fqn == fqn1) it.p1Choice else it.p2Choice }

    override fun toString() =
        "Match(strategy1=$fqn1, strategy2=$fqn2, score1=$score1, score2=$score2)"
}