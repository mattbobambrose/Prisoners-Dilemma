import strategy.AlwaysCoop
import strategy.AlwaysDefect
import strategy.DefEveryThree
import strategy.EveryOther
import strategy.Friedman
import strategy.Joss
import strategy.Random
import strategy.Tester
import strategy.TitForTat
import strategy.TitForTwoTats

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