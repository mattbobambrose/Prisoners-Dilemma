import Decision.COOPERATE
import Decision.DEFECT
import strategy.GameStrategy

class Player(gameStrategy: GameStrategy) {
    var points = 0
    val myMoves = mutableListOf<Decision>()
    val opponentMoves = mutableListOf<Decision>()
    val strategy = gameStrategy
    var roundNumber = 0

    fun addPoints(newPoints: Int) {
        points += newPoints
        roundNumber++
    }

    fun makeChoice(): Decision {
        return if (strategy.forgive()) {
            COOPERATE
        } else if (strategy.sneakAttack()) {
            DEFECT
        } else {
            strategy.chooseOption(roundNumber, strategy.fqn, myMoves, opponentMoves)
        }
    }

    fun getOpponentMoveXMovesAgo(turnsAgo: Int) =
        opponentMoves[opponentMoves.size - turnsAgo]

    fun firstMove() = roundNumber == 0

    fun displayHistory() {
        println("Player's moves: ")
        for (move in myMoves) {
            print("$move ")
        }
        println()
    }

    fun getResult(opponent: Player): MatchResultOptions {
        if (points > opponent.points) {
            return MatchResultOptions.WIN
        } else if (points < opponent.points) {
            return MatchResultOptions.LOSS
        }
        return MatchResultOptions.TIE
    }
}