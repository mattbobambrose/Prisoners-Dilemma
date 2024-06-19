import com.mattbobambrose.prisoner.player_server.Competition
import com.mattbobambrose.prisoner.player_server.PlayerServer
import com.mattbobambrose.prisoner.strategy.AlwaysCoop.Companion.alwaysCoop
import com.mattbobambrose.prisoner.strategy.AlwaysDefect.Companion.alwaysDefect
import com.mattbobambrose.prisoner.strategy.Random.Companion.random

class GameServerContext {
    val server = PlayerServer()
}

fun gameServer(block: GameServerContext.() -> Unit) {
    val server = GameServer()
    val gameServerContext = GameServerContext()
    server.startServer()
    gameServerContext.apply(block)
    Thread.sleep(100000)
//    server.stopServer()
}

fun GameServerContext.competition(
    block: Competition.() -> Unit
) = Competition().apply(block)

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        gameServer {
            competition {
                rules {
                    rounds = 10
                }
                player("Matthew") {
                    alwaysCoop()
                    alwaysCoop()
                }
                player("Paul") {
                    alwaysDefect()
                    alwaysDefect()
                }
                player("Anh") {
                    random()
                    random()
                }

            }.start()
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