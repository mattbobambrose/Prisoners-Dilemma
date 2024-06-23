package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.MatchId
import com.mattbobambrose.prisoner.common.Utils.randomId
import com.mattbobambrose.prisoner.game_server.TransportType.GRPC
import com.mattbobambrose.prisoner.game_server.TransportType.KRPC
import com.mattbobambrose.prisoner.game_server.TransportType.REST
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Match(
    val generation: Generation,
    private val info1: StrategyInfo,
    private val info2: StrategyInfo,
    private val scoreboard: Scoreboard,
    private val rules: Rules,
    val matchId: MatchId = MatchId(randomId())
) {
    val moves = mutableListOf<Moves>()
    private var increase1 = 0
    private var increase2 = 0
    private var score1 = 0
    private var score2 = 0
    var isRunning = false
    var isFinished = false
    val competitionId get() = generation.game.competitionId

    suspend fun runMatch(client: ClientContext) {
        isRunning = true
        val serverImpl: CallTransport =
            when (generation.game.gameServer.transportType) {
                REST -> RestTransport(client.httpClient, this)
                GRPC -> throw NotImplementedError("gRPC not supported")
                KRPC -> KRpcTransport(client.krpcClient, this)
            }
        for (i in 0 until rules.rounds) {
            val d1 = serverImpl.requestDecision(info1, info2, i)
            val d2 = serverImpl.requestDecision(info2, info1, i)

            updateIncreases(d1, d2)
            updateScore()
            moves.add(Moves(info1, info2, d1, d2, increase1, increase2, score1, score2))

            if (i % 10 == 0) {
                delay(500.milliseconds)
            }
        }
        isFinished = true
        logger.info { "Match ${matchId.id} finished" }
    }

    private fun updateScore() {
        score1 += increase1
        score2 += increase2
        scoreboard.updateScores(info1, info2, score1, score2, increase1, increase2)
    }

    fun getFqnStrings(): List<String> {
        return listOf(info1.fqn.name, info2.fqn.name)
    }

    private fun updateIncreases(d1: Decision, d2: Decision) {
        with(rules) {
            if (d1 == COOPERATE) {
                if (d2 == COOPERATE) {
                    increase1 = bothWinPoints
                    increase2 = bothWinPoints
                } else {
                    increase1 = lossPoints
                    increase2 = winPoints
                }
            } else {
                if (d2 == COOPERATE) {
                    increase1 = winPoints
                    increase2 = lossPoints
                } else {
                    increase1 = bothLosePoints
                    increase2 = bothLosePoints
                }
            }
        }
    }

    internal fun makeHistory(fqn: StrategyInfo) =
        moves.map { if (fqn == info1) it.p1Choice else it.p2Choice }

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

    fun getOutcome(isFinished: Boolean, fqn: String) =
        when (fqn) {
            info1.fqn.name -> makeStateString(isFinished, score1, score2)
            else -> makeStateString(isFinished, score2, score1)
        }

    fun makeStateString(isFinished: Boolean, score1: Int, score2: Int): String {
        return if (score1 > score2) {
            if (isFinished) {
                "Win"
            } else {
                "Winning"
            }
        } else if (score1 < score2) {
            if (isFinished) {
                "Loss"
            } else {
                "Losing"
            }
        } else {
            if (isFinished) {
                "Draw"
            } else {
                "Tied"
            }
        }
    }

    fun getFinishString() =
        if (isFinished) {
            "Finished"
        } else if (isRunning) {
            "Running"
        } else {
            "Not Started"
        }

    override fun toString() =
        "Match(strategy1=${info1.username} with ${info1.fqn}, strategy2=${info2.username} with ${info2.fqn}, score1=$score1, score2=$score2)"

    companion object {
        val logger = KotlinLogging.logger {}
    }
}