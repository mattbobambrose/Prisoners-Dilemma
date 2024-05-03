package org.example

import Tournament
import kotlinx.coroutines.ExperimentalCoroutinesApi
import strategy.TitForTat

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    Tournament(
        listOf(
//            EveryOther(),
            TitForTat(),
            TitForTat(),
            TitForTat(),
//            Friedman(),
//            DefEveryThree(),
//            AlwaysCoop(),
//            AlwaysDefect(),
//            Random(),
//            Joss(0, 10),
//            Tester(),
//            TitForTwoTats(),
        ),
        1
    ).runSimulation()
}