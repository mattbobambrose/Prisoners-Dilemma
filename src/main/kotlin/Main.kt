package org.example

import Tournament
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    Tournament(1).runSimulation()
}