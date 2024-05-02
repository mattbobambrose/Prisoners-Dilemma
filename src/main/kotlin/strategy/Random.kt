package strategy

import Decision

class Random(override val forgiveness: Int, override val sneaky: Int) : GameStrategy() {
    override fun chooseOption(roundNumber: Int, strategyId: String): Decision {
        return if (Math.random() < 0.5) {
            Decision.COOPERATE
        } else {
            Decision.DEFECT
        }
    }
}