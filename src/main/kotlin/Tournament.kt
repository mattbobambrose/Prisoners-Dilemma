import strategy.GameStrategy

class Tournament(val strategies: List<GameStrategy>, val generationCount: Int) {
    val generations = mutableListOf<Generation>()
    val matchHistories = mutableMapOf<Team, MutableList<Triple<Team, Int, MatchResultOptions>>>()

    fun runSimulation() {
        val teams = strategies.map {
            createTeam(it, strategies.size - 1)
        }
        for (i in 1..generationCount) {
            generations.add(Generation(strategies, Rules()))
            generations[i - 1].playMatches()
        }
        teams.sortedByDescending { it.totalPoints }
            .filter { matchHistories.containsKey(it) }
            .forEach {
                it.printResults(matchHistories[it]!!)
            }
    }

    fun createTeam(strategy: GameStrategy, players: Int): Team {
        val result = Team(strategy)
        for (i in 0 until players) {
            result.players.add(Player(strategy))
        }
        return result
    }
}