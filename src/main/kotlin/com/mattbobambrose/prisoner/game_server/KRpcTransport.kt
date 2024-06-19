package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.KRpcService
import kotlinx.rpc.RPCClient
import kotlinx.rpc.client.withService
import kotlinx.rpc.internal.streamScoped

class KRpcTransport(val client: RPCClient, val match: Match) : CallTransport {
    override suspend fun requestDecision(
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int
    ): Decision {
        return streamScoped {
            client
                .withService<KRpcService>()
                .requestDecision(
                    info,
                    opponentInfo,
                    round,
                    match.makeHistory(info),
                    match.makeHistory(opponentInfo)
                )
        }
    }
}