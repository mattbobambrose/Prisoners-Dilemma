package strategy

import Decision
import Decision.*

class Joss(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        return if (player.firstMove()) {
            COOPERATE
        } else if (sneakAttack()) {
            DEFECT
        } else {
            player.getOpponentMoveXMovesAgo(1)
        }
    }

}