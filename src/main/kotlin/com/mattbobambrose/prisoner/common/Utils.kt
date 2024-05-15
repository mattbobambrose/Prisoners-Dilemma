package com.mattbobambrose.prisoner.common

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType

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
}