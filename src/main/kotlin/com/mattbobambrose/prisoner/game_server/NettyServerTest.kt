package com.mattbobambrose.prisoner.game_server

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

object NettyServerTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = embeddedServer(
            Netty,
            port = 8081,
            host = "0.0.0.0",
            module = { }
        ).start(wait = false)
        Thread.sleep(3000)
        server.stop(1000, 1000)
    }
}