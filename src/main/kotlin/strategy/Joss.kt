package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class Joss(override val forgiveness: Int = 0, override val sneaky: Int = 0) : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return if (sneakAttack()) {
            DEFECT
        } else if (roundNumber == 0) {
            COOPERATE
        } else {
            opponentMoves[roundNumber - 1]
        }
    }

}