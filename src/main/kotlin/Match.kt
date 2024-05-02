import Decision.COOPERATE
import strategy.GameStrategy

class Match(
    private val generation: Generation,
    private val strategy1: GameStrategy,
    private val strategy2: GameStrategy,
    private val rules: Rules
) {
    private val moves = mutableListOf<Moves>()
    private var score1 = 0
    private var score2 = 0

    fun runMatch() {
        for (i in 0 until rules.rounds) {
            val d1 = strategy1.chooseOption(i, strategy2.id, moves)
            val d2 = strategy2.chooseOption(i, strategy1.id, moves)
            updateMatchScore(d1, d2)
        }
        with(generation.scoreboard) {
            getValue(strategy1).updateScorecard(score1, score2, strategy2.id)
            getValue(strategy2).updateScorecard(score2, score1, strategy1.id)
        }
    }

    private fun updateMatchScore(d1: Decision, d2: Decision) {
        if (d1 == COOPERATE) {
            if (d2 == COOPERATE) {
                score1 += rules.bothWinPoints
                score2 += rules.bothWinPoints
            } else {
                score1 += rules.lossPoints
                score2 += rules.winPoints
            }
        } else {
            if (d2 == COOPERATE) {
                score1 += rules.winPoints
                score2 += rules.lossPoints
            } else {
                score1 += rules.bothLosePoints
                score2 += rules.bothLosePoints
            }
        }
    }
}