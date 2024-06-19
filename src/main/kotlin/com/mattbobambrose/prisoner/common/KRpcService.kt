package com.mattbobambrose.prisoner.common

import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import kotlinx.rpc.RPC

interface KRpcService : RPC {
    //    suspend fun getNews(city: String): Flow<String>
//    suspend fun sendCities(city: Flow<String>): String
//    suspend fun sendAndGetCities(city: Flow<String>): Flow<String>
    suspend fun requestDecision(
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int,
        myHistory: List<Decision>,
        opponentHistory: List<Decision>
    ): Decision
}