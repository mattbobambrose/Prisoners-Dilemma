package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StrategyFqn(val name: String) {
    init {
        require(name.isNotEmpty()) { "FQN must not be empty" }
    }
}

@JvmInline
@Serializable
value class GameId(val id: String) {
    init {
        require(id.isNotEmpty()) { "ID must not be empty" }
    }

    companion object {
        val unknownGameId = GameId("Unknown")
    }
}

@JvmInline
@Serializable
value class Username(val name: String) {
    init {
        require(name.isNotEmpty()) { "Name must not be empty" }
    }

    companion object {
        val unknownUsername = Username("Unknown")
    }
}