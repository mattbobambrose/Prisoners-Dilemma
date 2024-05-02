import strategy.GameStrategy

class Team(val strategy: GameStrategy) {
    val name = strategy.toString().split("@")[0].split(".").last()
    val players = mutableListOf<Player>()
    var totalPoints = 0
    var winCount = 0
    val tiedWith = mutableListOf<Team>()

    fun getPlayer() = players.removeAt(0)
    fun printResults(matchHistories: MutableList<Triple<Team, Int, MatchResultOptions>>) {
        if (tiedWith.size == 0) {
            println("Team $name won $totalPoints points, won $winCount games and tied with 0 teams.")
        } else if (tiedWith.size == 1) {
            println(
                "Team $name won $totalPoints points, won $winCount games and tied with 1 team, which was ${tiedWith[0].name}."
            )
        } else {
            println(
                "Team $name won $totalPoints points, won $winCount games and tied with " +
                        "${tiedWith.size} teams, which were ${
                            tiedWith.map { it.name }.subList(0, tiedWith.lastIndex)
                                .joinToString(", ")
                        } and ${tiedWith[tiedWith.lastIndex].name}."
            )
        }
        println("Match history for $name:")
        matchHistories.forEach {
            println("Team $name played against ${it.first.name}, scored ${it.second} points, and ${it.third.result}.")
        }
    }
}