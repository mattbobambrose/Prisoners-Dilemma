package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.KRpcService
import kotlinx.rpc.client.withService
import kotlinx.rpc.internal.streamScoped

class KRpcTransport(val clientContext: ClientContext) : CallTransport {
    override suspend fun requestDecision(
        match: Match,
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int
    ) =
        streamScoped {
            clientContext.krpcClient
                .withService<KRpcService>()
                .requestDecision(
                    match.competitionId,
                    info,
                    opponentInfo,
                    round,
                    match.makeHistory(info),
                    match.makeHistory(opponentInfo)
                )
        }

    override suspend fun getStrategyFqnList(
        competitionId: CompetitionId,
        gameRequest: GameRequest
    ) =
        streamScoped {
            clientContext.krpcClient
                .withService<KRpcService>()
                .getStrategyFqnList(competitionId, gameRequest)
        }
}