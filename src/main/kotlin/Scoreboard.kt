import strategy.GameStrategy

class Scoreboard(strategies: List<GameStrategy>) {
    val scores = strategies.map { it.fqn }.associateWith { Scorecard() }

    fun getScore(name: String): Int {
        return scores.getValue(name).totalPoints
    }
}