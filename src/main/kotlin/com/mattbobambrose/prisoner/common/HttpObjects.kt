package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

class HttpObjects {
    @Serializable
    data class StrategyArgs(
        val roundNumber: Int,
        val opponentFqn: StrategyFqn,
        val myMoves: List<Decision>,
        val opponentMoves: List<Decision>
    )

    @Serializable
    data class StrategyResponse(val decision: Decision)

    @Serializable
    data class GameParticipant(
        val url: String, val rules: Rules = Rules()
    )

    @Serializable
    data class Rules(
        val winPoints: Int = 5,
        val bothWinPoints: Int = 3,
        val bothLosePoints: Int = 1,
        val lossPoints: Int = 0,
        val rounds: Int = 10,
        val isRandomSize: Boolean = false
    )
}