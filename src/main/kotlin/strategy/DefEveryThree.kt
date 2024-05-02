package strategy

import Decision
import Decision.COOPERATE
import Decision.DEFECT

class DefEveryThree(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        return if (roundNumber % 3 == 2) {
            DEFECT
        } else {
            COOPERATE
        }
    }
}