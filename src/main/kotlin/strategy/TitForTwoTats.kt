package strategy

import Decision
import Decision.*

class TitForTwoTats(
    override val forgiveness: Int,
    override val sneaky: Int
) : GameStrategy() {
    var oneDefect = false
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        if (player.firstMove()) {
            return COOPERATE
        }
        if (oneDefect && player.getOpponentMoveXMovesAgo(1) == DEFECT) {
            oneDefect = false
            return DEFECT
        } else {
            oneDefect = player.getOpponentMoveXMovesAgo(1) == DEFECT
            return COOPERATE
        }
    }
}