import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Utils.getTimestamp
import com.mattbobambrose.prisoner.player_server.Competition
import com.mattbobambrose.prisoner.strategy.TitForTat.Companion.titForTat
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.CountDownLatch

class GameServerContext(val server: GameServer) {}

fun gameServer(block: GameServerContext.() -> Unit) {
    val server = GameServer()
    val gameServerContext = GameServerContext(server)
    server.startServer()
    gameServerContext.apply(block)
    server.competitionMap.forEach { (_, competition) ->
        competition.completionLatch.await()
    }
    server.competitionMap.keys.toList().forEach { competitionId ->
        server.competitionMap.remove(competitionId)
            ?: error("Error removing competition: $competitionId")
    }
    server.stopServer()
}

fun GameServerContext.competition(
    name: String,
    block: Competition.() -> Unit
) {
    Player.logger.info { "competition() called *****************" }
    val competitionId = CompetitionId("$name-${getTimestamp()}")
    val completionLatch = CountDownLatch(1)
    val competition = Competition(this.server, competitionId, completionLatch)
    server.competitionMap[competitionId] = competition
    competition.apply(block).start()
}

object Player {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        gameServer {
            repeat(5) {
                competition("Competition $it") {
                    rules {
                        rounds = 25
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