package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Constants.PLAYER_SERVER_PORT
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils

class Competition() {
    val gameId: GameId = GameId(Utils.getRandomString(10))
    val strategies = mutableListOf<StrategyGroup>()
    var rules: HttpObjects.Rules = HttpObjects.Rules()

    init {
        PlayerServer.competitionMap.putIfAbsent(gameId, mutableListOf())
    }

    fun rules(block: HttpObjects.Rules.() -> Unit) {
        rules = HttpObjects.Rules().apply(block)
    }

    fun player(
        username: String,
        url: String = "http://localhost:$PLAYER_SERVER_PORT",
        block: StrategyGroup.() -> Unit
    ) {
        strategies += StrategyGroup(this, Username(username), url).apply(block)
    }

    fun start() {
        strategies.forEach {
            PlayerServer.register(it.username, gameId, it.url, rules)
//            it.go()
        }
    }
}