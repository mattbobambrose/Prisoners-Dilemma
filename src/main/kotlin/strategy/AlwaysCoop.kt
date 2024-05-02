package strategy

import Decision
import Decision.COOPERATE

class AlwaysCoop(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        return COOPERATE
    }
}