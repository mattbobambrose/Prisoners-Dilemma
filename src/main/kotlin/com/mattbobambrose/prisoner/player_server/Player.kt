package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Port
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.strategy.GameStrategy

class Player(val competition: Competition, val username: Username, val port: Port) {
    val portNumber get() = port.portNumber

    fun addStrategy(strategy: GameStrategy) = competition.addStrategy(username, strategy)
}