package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class Friedman : GameStrategy() {
    private var betrayed = false
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        when {
            roundNumber == 0 -> return COOPERATE
            opponentMoves[roundNumber - 1] == DEFECT -> betrayed = true
            forgive() -> betrayed = false
        }
        return if (betrayed) {
            DEFECT
        } else {
            COOPERATE
        }
    }
}