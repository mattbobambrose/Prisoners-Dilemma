import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class Tournament(
    private val participantURL: String,
    val fqns: List<String>,
    val generationCount: Int
) {
    private val generations = mutableListOf<Generation>()

    fun runSimulation() {
        runBlocking {
            HttpClient(CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    println("Configuring ContentNegotiation...")
                    json()
                }
            }.use { client ->
                for (i in 0..<generationCount) {
                    Generation(fqns, Rules()).also {
                        it.playMatches(participantURL, client)
                        generations.add(it)
                    }
                }
            }
        }
    }
}