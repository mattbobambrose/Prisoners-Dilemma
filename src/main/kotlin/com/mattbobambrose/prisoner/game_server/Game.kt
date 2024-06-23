package com.mattbobambrose.prisoner.game_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.KRPC_DECISION
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.Utils.createRpcClient
import io.github.oshai.kotlinlogging.KotlinLogging
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
    val gameServer: GameServer,
    val competitionId: CompetitionId,
    private val infoList: List<StrategyInfo>,
    private val generationCount: Int
) {
    val generationList = mutableListOf<Generation>()
    var isFinished = false
    var currentInfo = infoList

    fun runGame(rules: Rules) {
        ClientContext().use { client ->
            for (i in 0..<generationCount) {
                Generation(this@Game, currentInfo, rules).also { generation ->
                    generationList.add(generation)
                    generation.playMatches(client)
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
            logger.info { "Generation ${index + 1}" }
            generation.scoreboard.reportScores()
        }
    }

    fun getMatch(id: String): Match? {
        return generationList
            .flatMap { it.matchList }
            .find { it.matchId.id == id }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}