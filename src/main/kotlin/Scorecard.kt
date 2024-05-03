class Scorecard {
    var totalPoints: Int = 0
    var winCount: Int = 0
    val winList = mutableListOf<String>()
    var lossCount: Int = 0
    val lossList = mutableListOf<String>()
    var tieCount: Int = 0
    val tieList = mutableListOf<String>()
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