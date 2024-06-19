package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

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
        portNumber: Int,
        block: StrategyGroup.() -> Unit
    ) {
        strategies += StrategyGroup(this, Username(username), portNumber).apply(block)
    }

    fun start() {
        strategies.forEach {
            val playerServer = PlayerServer(it.portNumber)
            playerServer.startServer()
            val url = "http://localhost:${it.portNumber}"
            PlayerServer.register(it.username, gameId, url, rules)
        }
        go()
    }

    private fun go() {
        HttpClient(io.ktor.client.engine.cio.CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }.use { client ->
            runBlocking {
                client.get("http://localhost:$GAME_SERVER_PORT/$GO?gameId=${gameId.id}")
            }
        }
    }
}