package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.player_server.StrategyGroup

class AlwaysCoop : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return COOPERATE
    }

    companion object {
        fun StrategyGroup.alwaysCoop() = addStrategy(AlwaysCoop())
    }
}