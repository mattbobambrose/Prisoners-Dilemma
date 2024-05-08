package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.setJsonBody
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking

class Game(vararg strategies: GameStrategy) {
    init {
        strategyList.addAll(strategies)
    }

    fun play(rules: Rules = Rules()) {
        register(rules)
        strategyList.forEach { strategy: GameStrategy ->
            strategyMap[strategy.fqn] = strategy
        }
        embeddedServer(CIO, port = 8082, host = "0.0.0.0", module = Application::playerModule)
            .start(wait = true)
    }

    companion object {
        val strategyList: MutableList<GameStrategy> = mutableListOf()
        val strategyMap: MutableMap<StrategyFqn, GameStrategy> = mutableMapOf()

        fun register(rules: Rules) {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:8081/$REGISTER") {
                        setJsonBody(HttpObjects.GameParticipant("http://localhost:8082", rules))
                    }
                }
            }
        }
    }
}