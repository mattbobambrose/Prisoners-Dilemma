package strategy

import Decision
import Moves
import kotlin.random.Random

abstract class GameStrategy {
    open val id: String = this::class.simpleName ?: "Unknown"
    abstract val forgiveness: Int
    abstract val sneaky: Int
    abstract fun chooseOption(roundNumber: Int, strategyId: String, moves: List<Moves>): Decision
    fun forgive() = Random.nextInt(1, 100) in 1..forgiveness
    fun sneakAttack() = Random.nextInt(1, 100) in 1..sneaky
}