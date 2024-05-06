import com.github.michaelbull.itertools.pairCombinations
import io.ktor.client.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

class Generation(
    private val fqnList: List<String>,
    private val rules: Rules,
) {
    private val scoreboard = Scoreboard(fqnList)

    fun playMatches(client: HttpClient) {
        val matchChannel = Channel<Match>(CONCURRENT_MATCHES) { }
        runBlocking {
            launch {
                fqnList
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
                        match.runMatch(client)
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