package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.KRPC_DECISION
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.Utils.createRpcClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RPCClient

class ClientContext : Closeable {
    private lateinit var _httpClient: HttpClient
    val httpClient: HttpClient
        get() {
            synchronized(this) {
                if (!::_httpClient.isInitialized) {
                    _httpClient = HttpClient(CIO) {
                        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                            json()
                        }
                    }
                }
            }
            return _httpClient
        }

    private lateinit var _krpcClient: RPCClient
    val krpcClient: RPCClient
        get() {
            synchronized(this) {
                if (!::_krpcClient.isInitialized) {
                    _krpcClient =
                        runBlocking { createRpcClient("ws://localhost:$GAME_SERVER_PORT/$KRPC_DECISION") }
                }
            }
            return _krpcClient
        }

    override fun close() {
        if (::_httpClient.isInitialized) {
            _httpClient.close()
        }
    }
}

class Game(
    val gameId: GameId,
    private val infoList: List<StrategyInfo>,
    private val generationCount: Int
) {
    val generationList = mutableListOf<Generation>()
    var isFinished = false
    var currentInfo = infoList

    fun runSimulation(rules: Rules) {
        runBlocking {
            ClientContext().use { client ->
                for (i in 0..<generationCount) {
                    Generation(this@Game, currentInfo, rules).also { generation ->
                        generationList.add(generation)
                        generation.playMatches(client)
//                        currentInfo = generation.createNewInfoList()
                    }
                }
            }
        }
        assert(generationList.all { it.isFinished })
        isFinished = true
    }

    private fun Generation.createNewInfoList(): List<StrategyInfo> {
        return currentInfo
    }

    fun reportScores() {
        generationList.forEachIndexed { index, generation ->
            println("Generation ${index + 1}")
            generation.scoreboard.reportScores()
            println()
        }
    }

    fun getMatch(id: String): Match? {
        return generationList
            .flatMap { it.matchList }
            .find { it.matchId.id == id }
    }
}