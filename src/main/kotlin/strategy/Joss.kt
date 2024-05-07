package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class Joss : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            sneakAttack() -> DEFECT
            roundNumber == 0 -> COOPERATE
            else -> opponentMoves[roundNumber - 1]
        }
}