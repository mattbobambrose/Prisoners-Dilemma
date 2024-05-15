package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo

data class Moves(
    val p1Info: StrategyInfo,
    val p2Info: StrategyInfo,
    val p1Choice: Decision,
    val p2Choice: Decision,
    val p1Score: Int,
    val p2Score: Int
)