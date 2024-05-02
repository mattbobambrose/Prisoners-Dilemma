package strategy

import Decision
import Decision.*

class Friedman(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    var betrayed = false
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        if (player.firstMove()) {
            return COOPERATE
        }
        if (player.getOpponentMoveXMovesAgo(1) == DEFECT) {
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