package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

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
            HttpClient(CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
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