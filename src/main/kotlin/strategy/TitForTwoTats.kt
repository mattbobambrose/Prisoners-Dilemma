package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class TitForTwoTats : GameStrategy() {
    var oneDefect = false
    override fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ) =
        when {
            roundNumber == 0 -> {
                COOPERATE
            }

            oneDefect && opponentMoves[roundNumber - 1] == DEFECT -> {
                oneDefect = false
                DEFECT
            }

            else -> {
                oneDefect = opponentMoves[roundNumber - 1] == DEFECT
                COOPERATE
            }
        }
}