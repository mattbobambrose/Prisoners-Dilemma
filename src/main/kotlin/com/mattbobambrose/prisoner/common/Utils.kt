package com.mattbobambrose.prisoner.common

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import kotlinx.rpc.RPCClient
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig

object Utils {
    private val chars = ('0'..'9') + ('a'..'z') + ('A'..'Z')

    inline fun <reified T> HttpRequestBuilder.setJsonBody(arg: T) {
        contentType(Json)
        setBody(arg)
    }

    fun randomId(size: Int = 10): String {
        require(size > 0) { "Size must be greater than 0: $size" }
        return (0..<size)
            .map { chars.random() }
            .joinToString("")
    }

    suspend fun createRpcClient(url: String): RPCClient =
        HttpClient {
            installRPC()
        }.rpc {
            url(url)
            rpcConfig {
                serialization {
                    json()
                }
            }
        }

    fun getRandomString(length: Int) =
        (1..length)
            .map { allowedChars.random() }
            .joinToString("")

    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
}