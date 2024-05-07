package strategy

import Decision
import Decision.COOPERATE

class AlwaysCoop : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return COOPERATE
    }
}