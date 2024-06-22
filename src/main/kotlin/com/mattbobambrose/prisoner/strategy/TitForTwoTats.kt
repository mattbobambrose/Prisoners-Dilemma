package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.Decision.COOPERATE
import com.mattbobambrose.prisoner.common.Decision.DEFECT
import com.mattbobambrose.prisoner.player_server.Player

class TitForTwoTats : GameStrategy() {
    var oneDefect = false
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber == 0 -> {
                COOPERATE
            }

            oneDefect && opponentMoves[roundNumber - 1] == DEFECT -> {
                oneDefect = false
                DEFECT
            }

            else -> {
                oneDefect = opponentMoves[roundNumber - 1] == DEFECT
                COOPERATE
            }
        }

    companion object {
        fun Player.titForTwoTats() = addStrategy(TitForTwoTats())
    }
}