package strategy

import Decision
import Decision.DEFECT

class AlwaysDefect : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return DEFECT
    }
}