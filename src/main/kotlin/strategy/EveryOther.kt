package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class EveryOther(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision =
        if (roundNumber % 2 == 0) {
            COOPERATE
        } else {
            DEFECT
        }
}