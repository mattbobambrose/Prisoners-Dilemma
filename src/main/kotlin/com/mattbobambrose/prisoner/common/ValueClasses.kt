package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StrategyFqn(val name: String) {
    init {
        require(name.isNotEmpty()) { "FQN must not be empty" }
    }

    override fun toString(): String = name
}

@JvmInline
@Serializable
value class CompetitionId(val id: String) {
    init {
        require(id.isNotEmpty()) { "ID must not be empty" }
    }

    override fun toString(): String = id

    companion object {
        val unknownCompetitionId = CompetitionId("Unknown")
    }
}

@JvmInline
@Serializable
value class Username(val name: String) {
    init {
        require(name.isNotEmpty()) { "Name must not be empty" }
    }

    override fun toString(): String = name

    companion object {
        val unknownUsername = Username("Unknown")
    }
}

@JvmInline
@Serializable
value class MatchId(val id: String) {
    init {
        require(id.isNotEmpty()) { "ID must not be empty" }
    }

    override fun toString(): String = id

    companion object {
        val unknownMatchId = MatchId("Unknown")
    }
}

@JvmInline
@Serializable
value class PortNumber(val number: Int) {
    init {
        require(number in 1..65535) { "Port number must be between 1 and 65535" }
    }

    override fun toString(): String = number.toString()
}