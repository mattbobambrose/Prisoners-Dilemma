package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.Player

class EveryOther : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision =
        when {
            roundNumber % 2 == 0 -> COOPERATE
            else -> DEFECT
        }

    companion object {
        fun Player.everyOther(strategyCount: Int = 1) {
            repeat(strategyCount) {
                addStrategy(EveryOther())
            }
        }
    }
}