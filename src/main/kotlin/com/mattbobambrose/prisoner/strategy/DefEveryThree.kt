package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.StrategyGroup

class DefEveryThree : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber % 3 == 2 -> DEFECT
            else -> COOPERATE
        }

    companion object {
        fun StrategyGroup.defEveryThree(strategyCount: Int = 1) {
            repeat(strategyCount) {
                addStrategy(DefEveryThree())
            }
        }
    }
}