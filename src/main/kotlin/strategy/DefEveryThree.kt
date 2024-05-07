package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class DefEveryThree : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber % 3 == 2 -> DEFECT
            else -> COOPERATE
        }
}