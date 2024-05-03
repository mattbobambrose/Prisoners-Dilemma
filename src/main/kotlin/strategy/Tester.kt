package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class Tester(override val forgiveness: Int = 0, override val sneaky: Int = 0) : GameStrategy() {
    private var opponentSecondMove: Decision = DEFECT
    private var chosenStrategy: GameStrategy = this
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        if (roundNumber == 0) {
            return DEFECT
        }
        if (roundNumber == 1) {
            opponentSecondMove = opponentMoves[1]
            chosenStrategy = if (opponentSecondMove == COOPERATE) {
                EveryOther(forgiveness, sneaky)
            } else {
                TitForTat(forgiveness, sneaky)
            }
        }
        if (roundNumber == 2 && chosenStrategy is TitForTat) {
            return COOPERATE
        }
        return chosenStrategy.chooseOption(roundNumber, strategyId, myMoves, opponentMoves)
    }
}