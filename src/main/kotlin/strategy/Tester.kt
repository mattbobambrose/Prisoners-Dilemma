package strategy

import Decision
import Decision.*

class Tester(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    var opponentSecondMove: Decision = DEFECT
    var chosenStrategy: GameStrategy = this
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        if (player.firstMove()) {
            return DEFECT
        }
        if (player.roundNumber == 1) {
            opponentSecondMove = player.getOpponentMoveXMovesAgo(1)
            chosenStrategy = if (opponentSecondMove == COOPERATE) {
                EveryOther(forgiveness, sneaky)
            } else {
                TitForTat(forgiveness, sneaky)
            }
        }
        return chosenStrategy.chooseOption()
    }
}