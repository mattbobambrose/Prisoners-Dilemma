class Scorecard {
    var totalPoints: Int = 0
    private var winCount: Int = 0
    private val winList = mutableListOf<String>()
    private var lossCount: Int = 0
    private val lossList = mutableListOf<String>()
    private var tieCount: Int = 0
    private val tieList = mutableListOf<String>()
    fun updateScorecard(score1: Int, score2: Int, opponentId: String) {
        totalPoints += score1
        when {
            score1 > score2 -> {
                winCount++
                winList.add(opponentId)
            }

            score1 < score2 -> {
                lossCount++
                lossList.add(opponentId)
            }

            else -> {
                tieCount++
                tieList.add(opponentId)
            }
        }
    }
}