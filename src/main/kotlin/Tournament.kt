import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class Tournament(val fqns: List<String>, val generationCount: Int) {
    val generations = mutableListOf<Generation>()

    fun runSimulation() {
        runBlocking {
            HttpClient(CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                for (i in 0..<generationCount) {
                    Generation(fqns, Rules()).also {
                        it.playMatches(client)
                        generations.add(it)
                    }
                }
            }
        }
    }
}