package com.mattbobambrose.prisoner.game_server

enum class TransportType(val requiresHttp: Boolean = true) {
    LOCAL(false), REST, KRPC, GRPC
}