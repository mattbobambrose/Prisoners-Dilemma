package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.Port
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.participantMap
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.strategyMap
import com.mattbobambrose.prisoner.strategy.GameStrategy
import kotlin.collections.set

class StrategyGroup(val competition: Competition, val username: Username, val port: Port) {
    val strategyList = mutableListOf<GameStrategy>()
    val portNumber get() = port.portNumber

    fun addStrategy(strategy: GameStrategy) {
        strategyList.add(strategy)
        with(competition) {
            participantMap[competitionId]?.add(
                GameParticipant(
                    username,
                    competitionId,
                    strategy.fqn
                )
            )
            strategyMap[strategy.fqn] = strategy
        }
    }
}