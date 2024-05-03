package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class Friedman(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    private var betrayed = false
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        if (roundNumber == 0) {
            return COOPERATE
        }
        if (opponentMoves[roundNumber - 1] == DEFECT) {
            betrayed = true
        }
        if (forgive()) {
            betrayed = false
        }
        return if (betrayed) {
            DEFECT
        } else {
            COOPERATE
        }
    }
}