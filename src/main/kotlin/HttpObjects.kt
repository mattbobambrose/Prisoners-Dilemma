import kotlinx.serialization.Serializable

class HttpObjects {
    @Serializable
    data class StrategyArgs(
        val roundNumber: Int,
        val opponentFqn: String,
        val myMoves: List<Decision>,
        val opponentMoves: List<Decision>
    )

    @Serializable
    data class StrategyResponse(val decision: Decision)

    @Serializable
    data class GameParticipant(val url: String, val rounds: Int)
}