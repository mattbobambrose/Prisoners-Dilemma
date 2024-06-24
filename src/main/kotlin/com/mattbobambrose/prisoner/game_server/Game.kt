package com.mattbobambrose.prisoner.game_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import io.github.oshai.kotlinlogging.KotlinLogging

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
        for (i in 0..<generationCount) {
            Generation(this@Game, currentInfo, rules).also { generation ->
                generationList.add(generation)
                generation.playMatches()
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