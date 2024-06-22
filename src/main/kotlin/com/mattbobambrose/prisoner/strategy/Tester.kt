package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.Player

class Tester : GameStrategy() {
    private var opponentSecondMove: Decision = DEFECT
    private var chosenStrategy: GameStrategy = this
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        when (roundNumber) {
            0 -> return DEFECT
            1 -> return COOPERATE
            2 -> {
                opponentSecondMove = opponentMoves[1]
                chosenStrategy = if (opponentSecondMove == COOPERATE) {
                    EveryOther().also {
                        it.sneaky = this.sneaky
                        it.forgiveness = this.forgiveness
                    }
                } else {
                    TitForTat().also {
                        it.sneaky = this.sneaky
                        it.forgiveness = this.forgiveness
                    }
                }
            }
        }
        return chosenStrategy.chooseOption(roundNumber, strategyId, myMoves, opponentMoves)
    }

    companion object {
        fun Player.tester() = addStrategy(Tester())
    }
}