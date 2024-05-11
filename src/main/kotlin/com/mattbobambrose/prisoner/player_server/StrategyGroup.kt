package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.TournamentRequest
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.setJsonBody
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class StrategyGroup(vararg strategies: GameStrategy) {
    val strategyGroupList = strategies.toList()

    init {
        strategies.forEach {
            strategyMap[it.fqn] = it
        }
    }

    fun registerGroup(gameId: String, username: String, rules: Rules = Rules()) {
        participantMap.putIfAbsent(GameId(gameId), mutableListOf())
        strategyGroupList.forEach {
            participantMap[GameId(gameId)]?.add(
                GameParticipant(
                    Username(username),
                    GameId(gameId),
                    it.fqn
                )
            )
        }
        register(Username(username), GameId(gameId), rules)
    }

    fun go(gameId: String) {
        HttpClient(io.ktor.client.engine.cio.CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }.use { client ->
            runBlocking {
                client.get("http://localhost:8081/$GO?gameId=$gameId")
            }
        }
    }

    companion object {
        val participantMap: MutableMap<GameId, MutableList<GameParticipant>> = mutableMapOf()
        val strategyMap: MutableMap<StrategyFqn, GameStrategy> = mutableMapOf()

        init {
            thread {
                embeddedServer(
                    CIO,
                    port = 8082,
                    host = "0.0.0.0",
                    module = Application::playerModule
                )
                    .start(wait = true)
            }
        }

        fun register(username: Username, gameId: GameId, rules: Rules) {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:8081/$REGISTER") {
                        setJsonBody(
                            TournamentRequest(
                                username,
                                gameId,
                                "http://localhost:8082",
                                rules
                            )
                        )
                    }
                }
            }
        }
    }
}