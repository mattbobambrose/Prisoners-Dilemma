package strategy

import Decision

class Random(override val forgiveness: Int = 0, override val sneaky: Int = 0) : GameStrategy() {
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        return if (Math.random() < 0.5) {
            Decision.COOPERATE
        } else {
            Decision.DEFECT
        }
    }
}