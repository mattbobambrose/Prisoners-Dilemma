package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyArgs
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyResponse
import com.mattbobambrose.prisoner.common.MatchId
import com.mattbobambrose.prisoner.common.Utils.randomId
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Match(
    val parentGeneration: Generation,
    private val info1: StrategyInfo,
    private val info2: StrategyInfo,
    private val scoreboard: Scoreboard,
    private val rules: Rules,
    val matchId: MatchId = MatchId(randomId())
) {
    val moves = mutableListOf<Moves>()
    private var score1 = 0
    private var score2 = 0

    suspend fun runMatch(client: HttpClient) {
        for (i in 0 until rules.rounds) {
            val response1: HttpResponse =
                client.post("${info1.url}/$STRATEGY/${info1.fqn.name}") {
                    setJsonBody(StrategyArgs(i, info2, makeHistory(info1), makeHistory(info2)))
                }

            val response2: HttpResponse =
                client.post("${info2.url}/$STRATEGY/${info2.fqn.name}") {
                    setJsonBody(StrategyArgs(i, info1, makeHistory(info2), makeHistory(info1)))
                }
            if (i % 10 == 0) {
                delay(500.milliseconds)
            }
            val d1 = response1.body<StrategyResponse>().decision
            val d2 = response2.body<StrategyResponse>().decision

            updateMatchScore(d1, d2)
            moves.add(Moves(info1, info2, d1, d2, score1, score2))
        }
        scoreboard.updateScores(info1, info2, score1, score2)
    }

    fun getFqnStrings(): List<String> {
        return listOf(info1.fqn.name, info2.fqn.name)
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

    private fun makeHistory(fqn: StrategyInfo) =
        moves.map { if (fqn == info1) it.p1Choice else it.p2Choice }

    override fun toString() =
        "Match(strategy1=$info1, strategy2=$info2, score1=$score1, score2=$score2)"

    fun getOpponentFqn(fqn: String) =
        when (fqn) {
            info1.fqn.name -> {
                info2.fqn.name
            }

            else -> {
                info1.fqn.name
            }
        }

    fun getScore(fqn: String) =
        when (fqn) {
            info1.fqn.name -> {
                score1
            }

            else -> {
                score2
            }
        }

    fun getOutcome(fqn: String) =
        when (fqn) {
            info1.fqn.name -> {
                if (score1 > score2) {
                    "Win"
                } else if (score1 < score2) {
                    "Loss"
                } else {
                    "Draw"
                }
            }

            else -> {
                if (score2 > score1) {
                    "Win"
                } else if (score2 < score1) {
                    "Loss"
                } else {
                    "Draw"
                }
            }
        }
}