package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class TitForTwoTats(
    override val forgiveness: Int,
    override val sneaky: Int
) : GameStrategy() {
    var oneDefect = false
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision {
        if (roundNumber == 0) {
            return COOPERATE
        }
        if (oneDefect && opponentMoves[roundNumber - 1] == DEFECT) {
            oneDefect = false
            return DEFECT
        } else {
            oneDefect = opponentMoves[roundNumber - 1] == DEFECT
            return COOPERATE
        }
    }
}