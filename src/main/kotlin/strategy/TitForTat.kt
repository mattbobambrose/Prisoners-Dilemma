package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class TitForTat : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber == 0 -> COOPERATE
            sneakAttack() -> DEFECT
            else -> opponentMoves[roundNumber - 1]
        }
}