import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.setJsonBody
import com.mattbobambrose.prisoner.game_server.gameServerModule
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OneSidedTests {
    @Test
    fun `one sided test`() {
        assertTrue(true)
    }

//    @Test
//    fun testRoot() = testApplication {
//        application {
//            install(Resources)
//            routing {
//                get("/") {
//                    call.respondText("Hello World!")
//                }
//            }
//        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//        }
//    }

    @Test
    fun testGameServerModule() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.deployment.debug" to "true")
        }
        application {
            gameServerModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World1!", bodyAsText())
        }
        client.post("/$REGISTER") {
            setJsonBody(GameParticipant(GameId("abc"), "http://localhost:8082"))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Registered", bodyAsText())
        }
    }
}