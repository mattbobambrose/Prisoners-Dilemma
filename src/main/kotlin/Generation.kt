import com.github.michaelbull.itertools.pairCombinations
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import strategy.GameStrategy
import kotlin.time.Duration.Companion.seconds

class Generation(
    private val strategies: List<GameStrategy>,
    private val rules: Rules,
) {
    private val scoreboard = Scoreboard(strategies)

    fun playMatches() {
        val matchChannel = Channel<Match>(CONCURRENT_MATCHES) { }
        runBlocking {
            launch {
                strategies
                    .pairCombinations()
                    .map { (s1, s2) ->
                        Match(s1, s2, scoreboard, rules)
                    }.forEach {
                        println("Match: $it")
                        matchChannel.send(it)
                    }
                println()
                matchChannel.close()
            }
            for (i in 0 until CONCURRENT_MATCHES) {
                launch {
                    for (match in matchChannel) {
                        println()
                        println("Running match: $match")
                        match.runMatch()
                        delay(3.seconds)
                        println("$match")
                    }
                }
            }
        }
    }

    companion object {
        private const val CONCURRENT_MATCHES = 2
    }
}