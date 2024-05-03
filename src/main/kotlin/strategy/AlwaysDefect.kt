package strategy

import Decision
import Decision.DEFECT

class AlwaysDefect(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return DEFECT
    }
}