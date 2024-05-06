import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class Tournament(val generationCount: Int) {
    val generations = mutableListOf<Generation>()

    fun runSimulation() {
        runBlocking {
            HttpClient(CIO).use { client ->
                val response = client.get("http://localhost:8080/participants")
                val str = response.body<String>()
                val fqns = Json.decodeFromString<List<String>>(str)

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