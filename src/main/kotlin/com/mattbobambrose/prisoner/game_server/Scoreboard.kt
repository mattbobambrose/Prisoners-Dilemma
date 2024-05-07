package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.StrategyFqn

class Scoreboard(fqnList: List<StrategyFqn>) {
    val scores = fqnList.associateWith { Scorecard() }

    fun reportScores() {
        scores.toList()
            .sortedByDescending { it.second.totalPoints }
            .forEach { (name, scorecard) ->
                println("$name: ${scorecard.totalPoints}")
            }
    }
}