package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.StrategyGroup

class AlwaysDefect : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return DEFECT
    }

    companion object {
        fun StrategyGroup.alwaysDefect() = addStrategy(AlwaysDefect())
    }
}