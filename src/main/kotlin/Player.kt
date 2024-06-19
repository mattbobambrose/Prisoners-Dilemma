import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.Utils.getTimestamp
import com.mattbobambrose.prisoner.player_server.Competition
import com.mattbobambrose.prisoner.strategy.TitForTat.Companion.titForTat
import java.util.concurrent.CountDownLatch

class GameServerContext(val server: GameServer) {}

fun gameServer(block: GameServerContext.() -> Unit) {
    val server = GameServer()
    val gameServerContext = GameServerContext(server)
    server.startServer()
    gameServerContext.apply(block)
    Thread.sleep(100000)
//    server.stopServer()
}

fun GameServerContext.competition(
    name: String,
    block: Competition.() -> Unit
) {
    val gameid = GameId("$name-${getTimestamp()}")
    val completionLatch = CountDownLatch(1)
    server.completionCountDownLatchMap[gameid] = completionLatch
    Competition(gameid).apply(block).start()
    completionLatch.await()
    server.completionCountDownLatchMap.remove(gameid)
        ?: error("Error removing completion latch for $gameid")
}

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        gameServer {
            competition("Competition 1") {
                rules {
                    rounds = 50
                }
                player("Matthew", 8083) {
                    titForTat()
                }
                player("Paul", 8084) {
                    titForTat()
                }
                player("Anh", 8085) {
                    titForTat()
                }
            }
            println("Hello world!")
        }

//        StrategyGroup(
//            AlwaysCoop(),
//            AlwaysCoop(),
//            AlwaysDefect(),
////            DefEveryThree(),
////            EveryOther(),
////            Friedman(),
////            Joss(),
////            Tester(),
//            TitForTat(),
////            TitForTat(),
////            TitForTat(),
////            TitForTat(),
////            TitForTat(),
////            TitForTwoTats()
//        ).apply {
//            registerGroup("123", "Mattbob", Rules(rounds = 200))
//        }
//
//        StrategyGroup(
//            AlwaysCoop(),
////            AlwaysCoop(),
//            AlwaysDefect(),
////            DefEveryThree(),
////            EveryOther(),
////            Friedman(),
////            Joss(),
////            Random(),
////            Tester(),
////            TitForTat(),
////            TitForTat(),
////            TitForTat(),
////            TitForTwoTats()
//        ).apply {
//            registerGroup("abc", "Paul", Rules(rounds = 10))
//        }
    }
}