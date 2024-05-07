package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision

class Random : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) = cd.random()

    companion object {
        private val cd = listOf(Decision.COOPERATE, Decision.DEFECT)
    }
}