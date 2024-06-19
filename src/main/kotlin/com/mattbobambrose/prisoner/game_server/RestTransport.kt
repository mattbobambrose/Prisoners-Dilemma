package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post

class RestTransport(val client: HttpClient, val match: Match) : CallTransport {
    override suspend fun requestDecision(
        info: HttpObjects.StrategyInfo,
        opponentInfo: HttpObjects.StrategyInfo,
        round: Int
    ): Decision {
        return client.post("${info.url}/$STRATEGY/${info.fqn.name}") {
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
}