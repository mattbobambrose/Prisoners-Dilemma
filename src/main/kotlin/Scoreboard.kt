class Scoreboard(fqnList: List<String>) {
    val scores = fqnList.associateWith { Scorecard() }

    fun getScore(name: String): Int {
        return scores.getValue(name).totalPoints
    }
}