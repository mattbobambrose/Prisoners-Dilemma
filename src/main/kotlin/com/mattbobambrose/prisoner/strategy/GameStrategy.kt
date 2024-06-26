package com.mattbobambrose.prisoner.strategy

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.StrategyFqn
import kotlin.random.Random

abstract class GameStrategy {
    protected val baseName: String = this::class.simpleName ?: "Unknown"
    var forgiveness: Int = 0
    var sneaky: Int = 0
    val fqn = StrategyFqn("$baseName-${nextId(this)}")

    abstract fun chooseOption(
        roundNumber: Int,
        opponentId: String,
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