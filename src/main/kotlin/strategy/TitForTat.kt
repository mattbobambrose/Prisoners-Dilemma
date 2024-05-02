package strategy

import Decision
import Decision.*

class TitForTat(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        if (player.firstMove()) {
            return COOPERATE
        }
        if (sneakAttack()) {
            return DEFECT
        }
        return player.getOpponentMoveXMovesAgo(1)
    }
}