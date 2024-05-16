package com.mattbobambrose.prisoner.game_server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
class SuspendingCountDownLatch(private val count: Int = 1) {
    private val awaitMutex = Mutex(locked = true)
    private val countDownMutex = Mutex()
    private var currentCount = count

    init {
        require(count > 0) { "Count must be greater than 0" }
    }

    suspend fun await() {
        awaitMutex.withLock {}
    }

    suspend fun countDown() {
        countDownMutex.withLock {
            if (currentCount > 0) {
                currentCount--
                if (currentCount == 0) {
                    awaitMutex.unlock()
                }
            }
        }
    }
}