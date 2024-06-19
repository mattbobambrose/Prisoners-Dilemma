import com.mattbobambrose.prisoner.common.CompetitionId
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
    val competitionId = CompetitionId("$name-${getTimestamp()}")
    val completionLatch = CountDownLatch(1)
    val competition = Competition(this.server, competitionId, completionLatch)
    server.competitionMap[competitionId] = competition
    competition.apply(block).start()
    completionLatch.await()
}

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        gameServer {
            repeat(10) {
                competition("Competition $it") {
                    rules {
                        rounds = 50
                    }
                    player("Matthew") {
                        titForTat()
                    }
                    player("Paul") {
                        titForTat()
                    }
                    player("Anh") {
                        titForTat()
                    }
                }
                println("Hello world!")
            }
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