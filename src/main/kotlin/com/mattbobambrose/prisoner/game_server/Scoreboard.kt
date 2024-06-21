package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import io.github.oshai.kotlinlogging.KotlinLogging

class Scoreboard(fqnList: List<StrategyInfo>) {
    private val scoresList: List<Scorecard> = buildList { fqnList.forEach { add(Scorecard(it)) } }

    fun reportScores() {
        scoresList
            .sortedByDescending { it.totalPoints }
            .forEach { scorecard ->
                logger.info { "${scorecard.strategyInfo.fqn.name}: ${scorecard.totalPoints}" }
            }
    }

    fun sortedScores(): List<Scorecard> {
        return scoresList
            .sortedByDescending { it.totalPoints }
    }

    fun updateScores(
        info1: StrategyInfo,
        info2: StrategyInfo,
        score1: Int,
        score2: Int,
        increase1: Int,
        increase2: Int
    ) {
        scoresList.forEach {
            if (it.strategyInfo.fqn.name == info1.fqn.name) {
                it.updateScorecard(score1, score2, increase1, info2)
            }
            if (it.strategyInfo.fqn.name == info2.fqn.name) {
                it.updateScorecard(score2, score1, increase2, info1)
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}