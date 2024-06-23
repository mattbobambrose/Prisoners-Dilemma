package com.mattbobambrose.prisoner.game_server

import com.github.michaelbull.itertools.pairCombinations
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.ParallelForEach.forEach
import io.github.oshai.kotlinlogging.KotlinLogging

class Generation(
    val game: Game,
    private val strategyInfoList: List<StrategyInfo>,
    private val rules: Rules,
) {
    val scoreboard = Scoreboard(strategyInfoList)
    val matchList = mutableListOf<Match>()
    var isFinished = false

    fun playMatches(callTransport: CallTransport) {
        val concurrentMatches = game.gameServer.concurrentMatches
        strategyInfoList
            .pairCombinations()
            .map { (info1, info2) ->
                Match(this@Generation, info1, info2, scoreboard, rules)
                    .also { match -> matchList.add(match) }
            }.forEach(concurrentMatches) {
                logger.info { "Launching match: $it" }
                it.runMatch(callTransport)
                logger.info { "Match finished: $it" }
            }

        if (matchList.all { it.isFinished }) {
            isFinished = true
            logger.info { "Generation finished" }
        }
    }

    fun sortedScores(): List<Scorecard> {
        return scoreboard.sortedScores()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}