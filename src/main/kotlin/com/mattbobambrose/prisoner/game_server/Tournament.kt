package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class Tournament(
    private val infoList: List<StrategyInfo>,
    private val generationCount: Int
) {
    private val generations = mutableListOf<Generation>()

    fun runSimulation(rules: Rules) {
        runBlocking {
            HttpClient(CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                for (i in 0..<generationCount) {
                    Generation(infoList, rules).also {
                        it.playMatches(client)
                        generations.add(it)
                    }
                }
            }
        }
    }

    fun reportScores() {
        generations.forEachIndexed { index, generation ->
            println("Generation ${index + 1}")
            generation.scoreboard.reportScores()
            println()
        }
    }
}