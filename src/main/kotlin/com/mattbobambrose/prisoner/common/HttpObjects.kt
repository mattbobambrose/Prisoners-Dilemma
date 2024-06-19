package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

class HttpObjects {
    @Serializable
    data class GameParticipant(
        val username: Username,
        val competitionId: CompetitionId,
        val fqn: StrategyFqn
    )

    @Serializable
    data class Rules(
        var winPoints: Int = 5,
        var bothWinPoints: Int = 3,
        var bothLosePoints: Int = 1,
        var lossPoints: Int = 0,
        var rounds: Int = 10,
        var isRandomSize: Boolean = false
    )

    @Serializable
    data class StrategyArgs(
        val roundNumber: Int,
        val opponentInfo: StrategyInfo,
        val myMoves: List<Decision>,
        val opponentMoves: List<Decision>
    )

    @Serializable
    data class StrategyInfo(
        val url: String,
        val username: Username,
        val fqn: StrategyFqn
    )

    @Serializable
    data class StrategyResponse(val decision: Decision)

    @Serializable
    data class GameRequest(
        val competitionId: CompetitionId,
        val username: Username,
        val url: String,
        val rules: Rules = Rules()
    )
}