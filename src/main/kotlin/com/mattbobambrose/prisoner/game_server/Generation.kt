package com.mattbobambrose.prisoner.game_server

import com.github.michaelbull.itertools.pairCombinations
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.ParallelForEach.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Generation(
    private val parentGame: Game,
    private val infoList: List<StrategyInfo>,
    private val rules: Rules,
) {
    val scoreboard = Scoreboard(infoList)
    val matchList = mutableListOf<Match>()
    var isFinished = false

    fun playMatches(client: ClientContext) {
        runBlocking {
            launch {
                infoList
                    .pairCombinations()
                    .map { (info1, info2) ->
                        Match(this@Generation, info1, info2, scoreboard, rules)
                            .also { match ->
                                matchList.add(match)
                            }
                    }.forEach(CONCURRENT_MATCHES) {
                        println()
                        println("Running match: $it")
                        it.runMatch(client)
                        println("$it")
                    }
            }
        }
        if (matchList.all { it.isFinished }) {
            isFinished = true
            println("Generation finished")
        }
    }

    fun sortedScores(): List<Scorecard> {
        return scoreboard.sortedScores()
    }

    companion object {
        private const val CONCURRENT_MATCHES = 5
    }
}