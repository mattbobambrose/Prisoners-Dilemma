package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

class HttpObjects {
    @Serializable
    data class StrategyArgs(
        val roundNumber: Int,
        val opponentInfo: StrategyInfo,
        val myMoves: List<Decision>,
        val opponentMoves: List<Decision>
    )

    @Serializable
    data class StrategyResponse(val decision: Decision)

    @Serializable
    data class GameParticipant(
        val username: Username, val gameId: GameId, val url: String, val rules: Rules = Rules()
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

    @Serializable
    data class StrategyInfo(
        val url: String,
        val username: Username,
        val fqn: StrategyFqn
    )
}