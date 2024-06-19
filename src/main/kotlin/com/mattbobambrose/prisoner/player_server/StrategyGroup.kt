package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.competitionMap
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.strategyMap
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class StrategyGroup(val competition: Competition, val username: Username, val url: String) {
    val strategyList = mutableListOf<GameStrategy>()

    fun addStrategy(strategy: GameStrategy) {
        strategyList.add(strategy)
        with(competition) {
            competitionMap[gameId]?.add(GameParticipant(username, gameId, strategy.fqn))
            strategyMap[strategy.fqn] = strategy
        }
    }

    fun go() {
        HttpClient(io.ktor.client.engine.cio.CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }.use { client ->
            runBlocking {
                client.get("http://localhost:$GAME_SERVER_PORT/$GO?gameId=${competition.gameId.id}")
            }
        }
    }
}