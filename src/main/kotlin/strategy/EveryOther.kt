package strategy

import Decision.*

class EveryOther(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String) =
        if (player.roundNumber % 2 == 0) {
            COOPERATE
        } else {
            DEFECT
        }
}