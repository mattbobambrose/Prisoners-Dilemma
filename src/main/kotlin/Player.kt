import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.player_server.StrategyGroup
import com.mattbobambrose.prisoner.strategy.AlwaysCoop
import com.mattbobambrose.prisoner.strategy.AlwaysDefect

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        StrategyGroup(
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
            registerGroup("123", "Mattbob", Rules(rounds = 10))
        }

        StrategyGroup(
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
            registerGroup("abc", "Paul", Rules(rounds = 10))
        }
    }
}