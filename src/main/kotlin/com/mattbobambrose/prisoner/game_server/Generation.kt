package com.mattbobambrose.prisoner.game_server

import com.github.michaelbull.itertools.pairCombinations
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.StrategyFqn
import io.ktor.client.HttpClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Generation(
    private val fqnList: List<StrategyFqn>,
    private val rules: Rules,
) {
    val scoreboard = Scoreboard(fqnList)

    fun playMatches(participantURL: String, client: HttpClient) {
        val matchChannel = Channel<Match>(CONCURRENT_MATCHES) { }
        runBlocking {
            launch {
                fqnList
                    .pairCombinations()
                    .map { (s1, s2) ->
                        Match(participantURL, s1, s2, scoreboard, rules)
                    }.forEach {
                        println("Match: $it")
                        matchChannel.send(it)
                    }
                matchChannel.close()
            }
            for (i in 0 until CONCURRENT_MATCHES) {
                launch {
                    for (match in matchChannel) {
                        println()
                        println("Running match: $match")
                        match.runMatch(client)
//                        delay(1.seconds)
                        println("$match")
                    }
                }
            }
        }
    }

    companion object {
        private const val CONCURRENT_MATCHES = 5
    }
}