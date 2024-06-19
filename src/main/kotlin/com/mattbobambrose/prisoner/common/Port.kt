package com.mattbobambrose.prisoner.common

import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import java.util.concurrent.atomic.AtomicBoolean

class Port(val portNumber: Int) {
    private var available = AtomicBoolean(true)

    fun setAvailable() {
        available.set(true)
    }

    private fun setUnavailable() {
        available.set(false)
    }

    companion object {
        private val ports = mutableListOf<Port>()
        private var nextPortId = GAME_SERVER_PORT + 1

        @Synchronized
        fun nextAvailablePort() =
            ports.firstOrNull { it.available.get() }?.also { it.setUnavailable() }
                ?: Port(nextPortId++).also {
                    it.setUnavailable()
                    ports.add(it)
                }
    }
}