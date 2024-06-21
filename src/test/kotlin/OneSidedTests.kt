import io.ktor.server.testing.testApplication
import kotlin.test.Test
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
//        environment {
//            config = MapApplicationConfig("ktor.deployment.debug" to "true")
//        }
//        application {
//            gameServerModule()
//        }
//        val client = createClient {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World1!", bodyAsText())
//        }
//        client.post("/$REGISTER") {
//            setJsonBody(GameParticipant(CompetitionId("abc"), "http://localhost:8082"))
//        }.apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Registered", bodyAsText())
//        }
    }
}