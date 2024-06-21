package com.mattbobambrose.prisoner.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

object ParallelForEach {
    fun <T> Iterable<T>.forEach(coroutineCount: Int, action: (T) -> Unit) {
        require(coroutineCount > 0) { "Coroutine count must be greater than 0" }
        val channels = List(coroutineCount) { Channel<T>() }
        runBlocking {
            launch {
                this@forEach.forEach { writeChannels(channels, it) }
                channels.forEach { it.close() }
            }
            readChannels(channels, action)
        }
        channels.forEach { it.close() }
    }

    fun <T> Sequence<T>.forEach(coroutineCount: Int, action: suspend (T) -> Unit) {
        require(coroutineCount > 0) { "Coroutine count must be greater than 0" }
        val channels = List(coroutineCount) { Channel<T>() }
        runBlocking {
            launch {
                this@forEach.forEach { writeChannels(channels, it) }
                channels.forEach { it.close() }
            }
            readChannels(channels, action)
        }
        channels.forEach { it.close() }
    }

    private suspend fun <T> writeChannels(
        channels: List<Channel<T>>,
        elem: T
    ) {
        select { channels.forEach { it.onSend(elem) {} } }
    }

    private fun <T> CoroutineScope.readChannels(
        channels: List<Channel<T>>,
        action: suspend (T) -> Unit
    ) {
        for (channel in channels) {
            launch {
                for (elem in channel) {
                    action(elem)
                }
            }
        }
    }
}