package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.StrategyGroup

class TitForTat : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber == 0 -> COOPERATE
            sneakAttack() -> DEFECT
            else -> opponentMoves[roundNumber - 1]
        }

    companion object {
        fun StrategyGroup.titForTat() = addStrategy(TitForTat())
    }
}