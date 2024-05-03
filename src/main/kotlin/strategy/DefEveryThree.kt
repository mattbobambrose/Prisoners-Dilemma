package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class DefEveryThree(override val forgiveness: Int = 0, override val sneaky: Int = 0) :
    GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return if (roundNumber % 3 == 2) {
            DEFECT
        } else {
            COOPERATE
        }
    }
}