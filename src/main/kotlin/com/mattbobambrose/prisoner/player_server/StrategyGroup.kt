package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.competitionMap
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.strategyMap
import com.mattbobambrose.prisoner.strategy.GameStrategy
import kotlin.collections.mutableListOf
import kotlin.collections.set

class StrategyGroup(val competition: Competition, val username: Username, val portNumber: Int) {
    val strategyList = mutableListOf<GameStrategy>()

    fun addStrategy(strategy: GameStrategy) {
        strategyList.add(strategy)
        with(competition) {
            competitionMap[gameId]?.add(GameParticipant(username, gameId, strategy.fqn))
            strategyMap[strategy.fqn] = strategy
        }
    }
}