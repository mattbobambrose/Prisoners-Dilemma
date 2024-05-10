package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo

class Scoreboard(fqnList: List<StrategyInfo>) {
    private val scoresMap: Map<StrategyInfo, Scorecard> = fqnList.associateWith { Scorecard() }

    fun reportScores() {
        scoresMap.toList()
            .sortedByDescending { it.second.totalPoints }
            .forEach { (name, scorecard) ->
                println("$name: ${scorecard.totalPoints}")
            }
    }

    fun updateScores(fqn1: StrategyInfo, fqn2: StrategyInfo, score1: Int, score2: Int) {
        with(scoresMap) {
            getValue(fqn1).updateScorecard(score1, score2, fqn2)
            getValue(fqn2).updateScorecard(score2, score1, fqn1)
        }
    }
}