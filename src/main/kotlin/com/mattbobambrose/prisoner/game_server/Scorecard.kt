package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo

class Scorecard(val strategyInfo: StrategyInfo) {
    var totalPoints: Int = 0
    private var winCount: Int = 0
    private val winList = mutableListOf<StrategyInfo>()
    private var lossCount: Int = 0
    private val lossList = mutableListOf<StrategyInfo>()
    private var tieCount: Int = 0
    private val tieList = mutableListOf<StrategyInfo>()
    fun updateScorecard(
        score1: Int,
        score2: Int,
        increase: Int,
        opponentInfo: StrategyInfo
    ) {
        totalPoints += increase
        when {
            score1 > score2 -> {
                winCount++
                winList.add(opponentInfo)
            }

            score1 < score2 -> {
                lossCount++
                lossList.add(opponentInfo)
            }

            else -> {
                tieCount++
                tieList.add(opponentInfo)
            }
        }
    }
}