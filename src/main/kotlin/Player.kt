import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.player_server.Game
import com.mattbobambrose.prisoner.strategy.AlwaysCoop
import com.mattbobambrose.prisoner.strategy.AlwaysDefect
import com.mattbobambrose.prisoner.strategy.TitForTat

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        Game(
            AlwaysCoop(),
            AlwaysCoop(),
            AlwaysDefect(),
//            DefEveryThree(),
//            EveryOther(),
//            Friedman(),
//            Joss(),
//            Random(),
//            Tester(),
            TitForTat(),
            TitForTat(),
//            TitForTat(),
//            TitForTwoTats()
        ).play(Rules(rounds = 1000))
    }
}