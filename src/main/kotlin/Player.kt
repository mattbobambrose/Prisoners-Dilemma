import com.mattbobambrose.prisoner.player_server.Game
import com.mattbobambrose.prisoner.strategy.AlwaysCoop
import com.mattbobambrose.prisoner.strategy.AlwaysDefect
import com.mattbobambrose.prisoner.strategy.DefEveryThree
import com.mattbobambrose.prisoner.strategy.EveryOther
import com.mattbobambrose.prisoner.strategy.Friedman
import com.mattbobambrose.prisoner.strategy.Joss
import com.mattbobambrose.prisoner.strategy.Random
import com.mattbobambrose.prisoner.strategy.Tester
import com.mattbobambrose.prisoner.strategy.TitForTat
import com.mattbobambrose.prisoner.strategy.TitForTwoTats

object Player {
    @JvmStatic
    fun main(args: Array<String>) {
        Game(
            AlwaysCoop(),
            AlwaysDefect(),
            DefEveryThree(),
            EveryOther(),
            Friedman(),
            Joss(),
            Random(),
            Tester(),
            TitForTat(),
            TitForTwoTats()
        ).play()
    }
}