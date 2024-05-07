package com.mattbobambrose.prisoner.common

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StrategyFqn(val fqn: String) {
    init {
        require(fqn.isNotEmpty()) { "FQN must not be empty" }
    }
}