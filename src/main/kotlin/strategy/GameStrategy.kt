package strategy

import Decision
import kotlin.random.Random

abstract class GameStrategy {
    protected val baseName: String = this::class.simpleName ?: "Unknown"
    abstract val forgiveness: Int
    abstract val sneaky: Int
    val fqn = "$baseName-${nextId(this)}"

    abstract fun chooseOption(
        roundNumber: Int,
        strategyId: String,
        myMoves: List<Decision>,
        opponentMoves: List<Decision>
    ): Decision

    fun forgive() = Random.nextInt(1, 100) in 1..forgiveness
    fun sneakAttack() = Random.nextInt(1, 100) in 1..sneaky
    override fun toString() = "Strategy: $fqn"

    companion object {
        private val idCounter = mutableMapOf<String, Int>()
        fun nextId(strategy: GameStrategy): Int {
            val id = strategy.baseName
            return (idCounter.computeIfAbsent(id) { 0 } + 1).also { idCounter[id] = it }
        }
    }
}