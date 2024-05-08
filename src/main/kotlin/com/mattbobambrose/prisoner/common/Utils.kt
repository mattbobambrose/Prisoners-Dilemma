package com.mattbobambrose.prisoner.common

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType

inline fun <reified T> HttpRequestBuilder.setJsonBody(arg: T) {
    contentType(Json)
    setBody(arg)
}