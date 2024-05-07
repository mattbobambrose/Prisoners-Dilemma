//import kotlinx.coroutines.runBlocking
//import strategy.AlwaysCoop
//import strategy.AlwaysDefect
//import strategy.DefEveryThree
//import strategy.EveryOther
//import strategy.Friedman
//import strategy.Joss
//import strategy.Random
//import strategy.Tester
//import strategy.TitForTat
//import strategy.TitForTwoTats
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//class AlwaysCoopTests {
//    private val rules = Rules()
//
//    @Test
//    fun `AlwaysCoop vs AlwaysCoop`() {
//        val alwaysCoop1 = AlwaysCoop()
//        val alwaysCoop2 = AlwaysCoop()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop1.fqn, alwaysCoop2.fqn))
//        val match =
//            com.mattbobambrose.prisoner.game_server.Match("http://localhost:8082", alwaysCoop1.fqn, alwaysCoop2.fqn, scoreboard, rules)
//        runBlocking {
//            match.runMatch()
//        }
//        assertEquals(30, match.getScore(alwaysCoop1))
//        assertEquals(30, match.getScore(alwaysCoop2))
//    }
//
//    @Test
//    fun `AlwaysCoop vs AlwaysDefect`() {
//        val alwaysCoop = AlwaysCoop()
//        val alwaysDefect = AlwaysDefect()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, alwaysDefect))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, alwaysDefect, scoreboard, rules)
//        match.runMatch()
//        assertEquals(0, match.getScore(alwaysCoop))
//        assertEquals(50, match.getScore(alwaysDefect))
//    }
//
//    @Test
//    fun `AlwaysCoop vs DefEveryThree`() {
//        val alwaysCoop = AlwaysCoop()
//        val defEveryThree = DefEveryThree()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, defEveryThree))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, defEveryThree, scoreboard, rules)
//        match.runMatch()
//        assertEquals(21, match.getScore(alwaysCoop))
//        assertEquals(36, match.getScore(defEveryThree))
//    }
//
//    @Test
//    fun `AlwaysCoop vs EveryOther`() {
//        val alwaysCoop = AlwaysCoop()
//        val everyOther = EveryOther()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, everyOther))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, everyOther, scoreboard, rules)
//        match.runMatch()
//        assertEquals(15, match.getScore(alwaysCoop))
//        assertEquals(40, match.getScore(everyOther))
//    }
//
//    @Test
//    fun `AlwaysCoop vs Friedman`() {
//        val alwaysCoop = AlwaysCoop()
//        val friedman = Friedman()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, friedman))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, friedman, scoreboard, rules)
//        match.runMatch()
//        assertEquals(30, match.getScore(alwaysCoop))
//        assertEquals(30, match.getScore(friedman))
//    }
//
//    @Test
//    fun `AlwaysCoop vs Joss`() {
//        val alwaysCoop = AlwaysCoop()
//        val joss = Joss()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, joss))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, joss, scoreboard, rules)
//        match.runMatch()
//        assertEquals(, match.getScore(alwaysCoop))
//        assertEquals(, match.getScore(joss))
//    }
//
//    @Test
//    fun `AlwaysCoop vs Random`() {
//        val alwaysCoop = AlwaysCoop()
//        val random = Random()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, random))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, random, scoreboard, rules)
//        match.runMatch()
//        assertEquals(, match.getScore(alwaysCoop))
//        assertEquals(, match.getScore(random))
//    }
//
//    @Test
//    fun `AlwaysCoop vs Tester`() {
//        val alwaysCoop = AlwaysCoop()
//        val tester = Tester()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, tester))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, tester, scoreboard, rules)
//        match.runMatch()
//        assertEquals(15, match.getScore(alwaysCoop))
//        assertEquals(40, match.getScore(tester))
//    }
//
//    @Test
//    fun `AlwaysCoop vs TitForTat`() {
//        val alwaysCoop = AlwaysCoop()
//        val titForTat = TitForTat()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, titForTat))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, titForTat, scoreboard, rules)
//        match.runMatch()
//        assertEquals(30, match.getScore(alwaysCoop))
//        assertEquals(30, match.getScore(titForTat))
//    }
//
//
//    @Test
//    fun `AlwaysCoop vs TitForTwoTats`() {
//        val alwaysCoop = AlwaysCoop()
//        val titForTwoTats = TitForTwoTats()
//        val scoreboard = com.mattbobambrose.prisoner.game_server.Scoreboard(listOf(alwaysCoop, titForTwoTats))
//        val match = com.mattbobambrose.prisoner.game_server.Match(alwaysCoop, titForTwoTats, scoreboard, rules)
//        match.runMatch()
//        assertEquals(30, match.getScore(alwaysCoop))
//        assertEquals(30, match.getScore(titForTwoTats))
//    }
//}