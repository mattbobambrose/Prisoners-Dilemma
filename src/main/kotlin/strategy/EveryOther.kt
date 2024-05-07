package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

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
}