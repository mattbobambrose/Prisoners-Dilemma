import Decision.COOPERATE
import strategy.GameStrategy

class Match(
    private val strategy1: GameStrategy,
    private val strategy2: GameStrategy,
    val scoreboard: Scoreboard,
    private val rules: Rules
) {
    private val moves = mutableListOf<Moves>()
    private var score1 = 0
    private var score2 = 0

    fun runMatch() {
        for (i in 0 until rules.rounds) {
            val d1 = strategy1.chooseOption(
                i,
                strategy2.fqn,
                makeHistory(strategy1),
                makeHistory(strategy2)
            )
            val d2 = strategy2.chooseOption(
                i,
                strategy1.fqn,
                makeHistory(strategy2),
                makeHistory(strategy1)
            )
            moves.add(Moves(d1, d2))
            updateMatchScore(d1, d2)
        }
        with(scoreboard.scores) {
            getValue(strategy1.fqn).updateScorecard(score1, score2, strategy2.fqn)
            getValue(strategy2.fqn).updateScorecard(score2, score1, strategy1.fqn)
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

    private fun makeHistory(strategy: GameStrategy): List<Decision> {
        return moves.map {
            if (strategy.fqn == strategy1.fqn) {
                it.p1Choice
            } else {
                it.p2Choice
            }
        }
    }

    fun getScore(strategy: GameStrategy): Int {
        return scoreboard.getScore(strategy.fqn)
    }

    override fun toString(): String {
        return "Match(strategy1=$strategy1, strategy2=$strategy2, score1=$score1, score2=$score2)"
    }
}