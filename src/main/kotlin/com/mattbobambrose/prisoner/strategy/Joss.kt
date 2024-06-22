package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.Player

class Joss : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            sneakAttack() -> DEFECT
            roundNumber == 0 -> COOPERATE
            else -> opponentMoves[roundNumber - 1]
        }

    companion object {
        fun Player.joss() = addStrategy(Joss())
    }
}