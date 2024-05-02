package org.example

import Tournament
import kotlinx.coroutines.ExperimentalCoroutinesApi
import strategy.*

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    /*val list = List(10) { it }
    val fanOut = Channel<Pair<Int, Int>>(UNLIMITED) { }
    val fanIn = Channel<Pair<Int, Int>>(UNLIMITED) { }
    runBlocking {
        val a =
            List(100) {
                async {
                    delay(10.milliseconds)
                    it * 100
                }
            }

        val a1 = async {
            delay(1.seconds)
            75
        }
        a1.await()
        println(a1.getCompleted())
        launch {
            list.forEachIndexed { index, a ->
                list.drop(index + 1).forEach { b ->
                    fanOut.send(a to b)
                }
            }
            fanOut.close()
        }
        for (i in 0..2) {
            launch {
                for (v in fanOut) {
                    fanIn.send(v)
                    delay(1.seconds)
                }
                fanOut.close()
            }
        }
        launch {
            for (v in fanIn) {
                println(v)
            }
        }
        a.forEach {
            it.job.
            println(it.await())
        }
    }*/


    val tournament = Tournament(
        listOf(
            EveryOther(0, 0),
            TitForTat(0, 0),
            Friedman(0, 0),
            DefEveryThree(0, 0),
            AlwaysCoop(0, 0),
            AlwaysDefect(0, 0),
            Random(0, 0),
            Joss(0, 10),
            Tester(0, 0),
            TitForTwoTats(0, 0),
        ),
        1
    )
    tournament.runSimulation()


    /*
        val titForTat = TitForTat(0, 0)
        val friedman = Friedman(0, 0)
        val everyOther = EveryOther(0, 0)
        val everyThree = DefEveryThree(0, 0)
        val alwaysDefect = AlwaysDefect(0, 0)
        val alwaysCoop = AlwaysCoop(0, 0)
        val match = Match(Player(titForTat), Player(alwaysCoop), 5, 3, 1, 0, 1000)
        val players = match.simulateMatch()
        println("TitForTat earned ${players.first.points}")
        players.first.displayHistory()
        println("Friedman earned ${players.second.points}")
        players.second.displayHistory()
    */

    /*val list = List(10) { it }
    println(list.pairPermutations().toList())*/
}