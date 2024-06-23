package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.KRpcService
import com.mattbobambrose.prisoner.player_server.Competition
import kotlin.coroutines.CoroutineContext

class KRpcServiceImpl(
    val competitionMap: Map<CompetitionId, Competition>,
    override val coroutineContext: CoroutineContext
) : KRpcService {
    override suspend fun requestDecision(
        competitionId: CompetitionId,
        info: HttpObjects.StrategyInfo,
        opponentInfo: HttpObjects.StrategyInfo,
        round: Int,
        myHistory: List<Decision>,
        opponentHistory: List<Decision>
    ): Decision {
        val competition =
            competitionMap[competitionId] ?: error("Invalid competition id: $competitionId")
        val strategy =
            competition.strategyMap[info.fqn] ?: error("Invalid fqn: ${info.fqn}")
        return strategy.chooseOption(
            round,
            opponentInfo.fqn.name,
            myHistory,
            opponentHistory
        )
    }
}