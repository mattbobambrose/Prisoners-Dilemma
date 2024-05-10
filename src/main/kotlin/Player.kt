import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.player_server.Game
import com.mattbobambrose.prisoner.strategy.AlwaysCoop
import com.mattbobambrose.prisoner.strategy.AlwaysDefect

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        Game(
            AlwaysCoop(),
            AlwaysCoop(),
//            AlwaysDefect(),
//            DefEveryThree(),
//            EveryOther(),
//            Friedman(),
//            Joss(),
//            Random(),
//            Tester(),
//            TitForTat(),
//            TitForTat(),
//            TitForTat(),
//            TitForTwoTats()
        ).apply {
            play("Mattbob", Rules(rounds = 10))
        }

        Game(
            AlwaysCoop(),
//            AlwaysCoop(),
            AlwaysDefect(),
//            DefEveryThree(),
//            EveryOther(),
//            Friedman(),
//            Joss(),
//            Random(),
//            Tester(),
//            TitForTat(),
//            TitForTat(),
//            TitForTat(),
//            TitForTwoTats()
        ).apply {
            play("Paul", Rules(rounds = 10))
            go()
        }
    }
}