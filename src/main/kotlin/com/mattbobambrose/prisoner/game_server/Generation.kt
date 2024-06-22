package com.mattbobambrose.prisoner.game_server

import com.github.michaelbull.itertools.pairCombinations
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

class Generation(
    val parentGame: Game,
    private val strategyInfoList: List<StrategyInfo>,
    private val rules: Rules,
) {
    val scoreboard = Scoreboard(strategyInfoList)
    val matchList = mutableListOf<Match>()
    var isFinished = false

    fun playMatches(client: ClientContext) {
        strategyInfoList
            .pairCombinations()
            .map { (info1, info2) ->
                Match(this@Generation, info1, info2, scoreboard, rules)
                    .also { match -> matchList.add(match) }
            }.forEach {
                runBlocking {
                    logger.info { "Launching match: $it" }
                    it.runMatch(client)
                    logger.info { "Match finished: $it" }
                }
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
        private const val CONCURRENT_MATCHES = 5
    }
}