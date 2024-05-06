import Decision.COOPERATE
import HttpObjects.StrategyArgs
import HttpObjects.StrategyResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import strategy.GameStrategy

class Match(
    private val fqn1: String,
    private val fqn2: String,
    private val scoreboard: Scoreboard,
    private val rules: Rules
) {
    private val moves = mutableListOf<Moves>()
    private var score1 = 0
    private var score2 = 0

    suspend fun runMatch(client: HttpClient) {
        for (i in 0 until rules.rounds) {
            val response1: HttpResponse =
                client.post("http://localhost:8080/strategy/$fqn1/play") {
                    setBody(
                        Json.encodeToString(
                            StrategyArgs(i, fqn2, makeHistory(fqn1), makeHistory(fqn2))
                        )
                    )
                }
            val str1 = response1.body<String>()
//            println("response 1: ${Json.decodeFromString<StrategyResponse>(str1)}")

            val response2: HttpResponse =
                client.post("http://localhost:8080/strategy/$fqn2/play") {
                    setBody(
                        Json.encodeToString(
                            StrategyArgs(i, fqn1, makeHistory(fqn2), makeHistory(fqn1))
                        )
                    )
                }
            val str2 = response2.body<String>()
//            println("response 2: ${Json.decodeFromString<StrategyResponse>(str2)}")

            val d1 = Json.decodeFromString<StrategyResponse>(str1).decision
            val d2 = Json.decodeFromString<StrategyResponse>(str2).decision

            moves.add(Moves(d1, d2))
            updateMatchScore(d1, d2)
        }
        with(scoreboard.scores) {
            getValue(fqn1).updateScorecard(score1, score2, fqn2)
            getValue(fqn2).updateScorecard(score2, score1, fqn1)
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

    private fun makeHistory(fqn: String): List<Decision> {
        return moves.map {
            if (fqn == fqn1) {
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
        return "Match(strategy1=$fqn1, strategy2=$fqn2, score1=$score1, score2=$score2)"
    }
}