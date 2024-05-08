package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.StrategyFqn
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class Tournament(
    private val participantURL: String,
    private val fqns: List<StrategyFqn>,
    private val generationCount: Int
) {
    private val generations = mutableListOf<Generation>()

    fun runSimulation(rules: Rules) {
        runBlocking {
            HttpClient(CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    println("Configuring ContentNegotiation...")
                    json()
                }
            }.use { client ->
                for (i in 0..<generationCount) {
                    Generation(fqns, rules).also {
                        it.playMatches(participantURL, client)
                        generations.add(it)
                    }
                }
            }
        }
    }

    fun reportScores() {
        generations.forEachIndexed { index, generation ->
            println("com.mattbobambrose.prisoner.game_server.Generation ${index + 1}")
            generation.scoreboard.reportScores()
            println()
        }
    }
}