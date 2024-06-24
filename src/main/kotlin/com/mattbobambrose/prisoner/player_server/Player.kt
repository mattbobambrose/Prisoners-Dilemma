package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Port
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.game_server.CallTransport
import com.mattbobambrose.prisoner.game_server.ClientContext
import com.mattbobambrose.prisoner.game_server.KRpcTransport
import com.mattbobambrose.prisoner.game_server.LocalTransport
import com.mattbobambrose.prisoner.game_server.RestTransport
import com.mattbobambrose.prisoner.game_server.TransportType.GRPC
import com.mattbobambrose.prisoner.game_server.TransportType.KRPC
import com.mattbobambrose.prisoner.game_server.TransportType.LOCAL
import com.mattbobambrose.prisoner.game_server.TransportType.REST
import com.mattbobambrose.prisoner.strategy.GameStrategy

class Player(val competition: Competition, val username: Username, val port: Port) {
    val portNumber get() = port.portNumber

    private val clientContext = ClientContext(portNumber.number)
    val callTransport: CallTransport by lazy {
        PlayerServer.logger.info { "Creating transport for ${competition.gameServer.transportType}" }
        when (competition.gameServer.transportType) {
            LOCAL -> LocalTransport(competition.gameServer)
            REST -> RestTransport(clientContext)
            GRPC -> throw NotImplementedError("gRPC not supported")
            KRPC -> KRpcTransport(clientContext)
        }
    }

    fun addStrategy(strategy: GameStrategy) = competition.addStrategy(username, strategy)
}