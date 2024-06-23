package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class RestTransport(val client: HttpClient) : CallTransport {
    override suspend fun requestDecision(
        match: Match,
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int
    ): Decision {
        return client.post("${info.url}/$STRATEGY/${match.competitionId.id.encode()}/${info.fqn.name.encode()}") {
            setJsonBody(
                HttpObjects.StrategyArgs(
                    round,
                    opponentInfo,
                    match.makeHistory(info),
                    match.makeHistory(opponentInfo)
                )
            )
        }.body<HttpObjects.StrategyResponse>().decision
    }

    override suspend fun getStrategyFqnList(
        competitionId: CompetitionId,
        gameRequest: HttpObjects.GameRequest
    ) =
        client.get("${gameRequest.url}/$STRATEGYFQNS?$COMPETITION_ID=${competitionId.id.encode()}&username=${gameRequest.username.name.encode()}")
            .body<List<StrategyFqn>>()
}