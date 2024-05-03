package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class TitForTat(override val forgiveness: Int = 0, override val sneaky: Int = 0) : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        if (roundNumber == 0) {
            return COOPERATE
        }
        if (sneakAttack()) {
            return DEFECT
        }
        return opponentMoves[roundNumber - 1]
    }
}