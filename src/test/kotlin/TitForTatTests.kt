import strategy.AlwaysCoop
import strategy.AlwaysDefect
import strategy.DefEveryThree
import strategy.TitForTat
import kotlin.test.Test
import kotlin.test.assertEquals

class TitForTatTests {
    val rules = Rules()

    @Test
    fun `TitForTat vs AlwaysCoop`() {
        val titForTat = TitForTat(0, 0)
        val alwaysCoop = AlwaysCoop(0, 0)
        val scoreboard = Scoreboard(listOf(titForTat, alwaysCoop))
        val match = Match(titForTat, alwaysCoop, scoreboard, rules)
        match.runMatch()
        assertEquals(30, match.getScore(titForTat))
        assertEquals(30, match.getScore(alwaysCoop))
    }

    @Test
    fun `TitForTat vs AlwaysDefect`() {
        val titForTat = TitForTat(0, 0)
        val alwaysDefect = AlwaysDefect(0, 0)
        val scoreboard = Scoreboard(listOf(titForTat, alwaysDefect))
        val match = Match(titForTat, alwaysDefect, scoreboard, rules)
        match.runMatch()
        assertEquals(9, match.getScore(titForTat))
        assertEquals(14, match.getScore(alwaysDefect))
    }

    @Test
    fun `TitForTat vs DefEveryThree`() {
        val titForTat = TitForTat(0, 0)
        val defEveryThree = DefEveryThree(0, 0)
        val scoreboard = Scoreboard(listOf(titForTat, defEveryThree))
        val match = Match(titForTat, defEveryThree, scoreboard, rules)
        match.runMatch()
        assertEquals(27, match.getScore(titForTat))
        assertEquals(27, match.getScore(defEveryThree))
    }

    @Test
    fun `TitForTat vs TitForTat`() {
        val titForTat1 = TitForTat(0, 0)
        val titForTat2 = TitForTat(0, 0)
        val scoreboard = Scoreboard(listOf(titForTat1, titForTat2))
        val match = Match(titForTat1, titForTat2, scoreboard, rules)
        match.runMatch()
        assertEquals(30, match.getScore(titForTat1))
        assertEquals(30, match.getScore(titForTat2))
    }
}