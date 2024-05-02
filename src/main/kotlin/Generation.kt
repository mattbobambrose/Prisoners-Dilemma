import com.github.michaelbull.itertools.pairCombinations
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import strategy.GameStrategy

class Generation(
    val strategies: List<GameStrategy>,
    val rules: Rules,
) {
    val scoreboard = strategies.associateWith { Scorecard() }

    fun playMatches() {
        val matchChannel = Channel<Match>(CONCURRENT_MATCHES) { }
        runBlocking {
            launch {
                strategies
                    .pairCombinations()
                    .map { (s1, s2) ->
                        Match(this@Generation, s1, s2, rules)
                    }.forEach {
                        matchChannel.send(it)
                    }
            }
            for (i in 0 until CONCURRENT_MATCHES) {
                launch {
                    for (match in matchChannel) {
                        match.runMatch()
                    }
                    matchChannel.close()
                }
            }
        }
    }

    companion object {
        private const val CONCURRENT_MATCHES = 5
    }
}