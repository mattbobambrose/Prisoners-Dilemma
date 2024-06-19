package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects

interface CallTransport {
    suspend fun requestDecision(
        info: HttpObjects.StrategyInfo,
        opponentInfo: HttpObjects.StrategyInfo,
        round: Int
    ): Decision
}