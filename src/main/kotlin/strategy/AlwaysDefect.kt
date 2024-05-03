package strategy

import Decision
import Decision.DEFECT

class AlwaysDefect(override val forgiveness: Int = 0, override val sneaky: Int = 0) :
    GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return DEFECT
    }
}