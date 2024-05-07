package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.StrategyFqn

class Scorecard {
    var totalPoints: Int = 0
    private var winCount: Int = 0
    private val winList = mutableListOf<StrategyFqn>()
    private var lossCount: Int = 0
    private val lossList = mutableListOf<StrategyFqn>()
    private var tieCount: Int = 0
    private val tieList = mutableListOf<StrategyFqn>()
    fun updateScorecard(score1: Int, score2: Int, opponentId: StrategyFqn) {
        totalPoints += score1
        when {
            score1 > score2 -> {
                winCount++
                winList.add(opponentId)
            }

            score1 < score2 -> {
                lossCount++
                lossList.add(opponentId)
            }

            else -> {
                tieCount++
                tieList.add(opponentId)
            }
        }
    }
}