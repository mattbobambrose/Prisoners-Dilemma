package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.EndpointNames.KRPC_DECISION
import com.mattbobambrose.prisoner.common.Utils.createRpcClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RPCClient

class ClientContext(val portNumber: Int) : Closeable {
    private lateinit var _httpClient: HttpClient
    val httpClient: HttpClient
        get() {
            synchronized(this) {
                if (!::_httpClient.isInitialized) {
                    _httpClient = HttpClient(CIO) {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                }
            }
            return _httpClient
        }

    private lateinit var _krpcClient: RPCClient
    val krpcClient: RPCClient
        get() {
            synchronized(this) {
                if (!::_krpcClient.isInitialized) {
                    _krpcClient =
                        runBlocking {
                            logger.info { "KRPC client created on port $portNumber" }
                            createRpcClient("ws://localhost:$portNumber/$KRPC_DECISION")
                        }
                }
            }
            return _krpcClient
        }

    override fun close() {
        if (::_httpClient.isInitialized) {
            _httpClient.close()
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}