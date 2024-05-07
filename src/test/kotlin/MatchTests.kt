import org.junit.jupiter.api.BeforeEach
import strategy.AlwaysCoop
import strategy.TitForTat
import kotlin.test.Test
import kotlin.test.assertEquals

class MatchTests {
    val rules = Rules()

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `TitForTat vs AlwaysCoop`() {
        val titForTat = TitForTat()
        val alwaysCoop = AlwaysCoop()
        val scoreboard = Scoreboard(listOf(titForTat, alwaysCoop))
        val match = Match(titForTat, alwaysCoop, scoreboard, rules)
        match.runMatch()
        assertEquals(30, match.getScore(titForTat))
        assertEquals(30, match.getScore(alwaysCoop))
    }
}