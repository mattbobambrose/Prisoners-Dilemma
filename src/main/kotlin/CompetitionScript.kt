import com.mattbobambrose.prisoner.game_server.TransportType.REST
import com.mattbobambrose.prisoner.player_server.PlayerDSL.competition
import com.mattbobambrose.prisoner.player_server.PlayerDSL.config
import com.mattbobambrose.prisoner.player_server.PlayerDSL.gameServer
import com.mattbobambrose.prisoner.player_server.PlayerDSL.onBegin
import com.mattbobambrose.prisoner.player_server.PlayerDSL.onEnd
import com.mattbobambrose.prisoner.player_server.PlayerDSL.player
import com.mattbobambrose.prisoner.player_server.PlayerDSL.rules
import com.mattbobambrose.prisoner.strategy.TitForTat.Companion.titForTat
import io.github.oshai.kotlinlogging.KotlinLogging

object CompetitionScript {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        gameServer(REST) {
            config {
                concurrentMatches = 10
            }
            onBegin {
                logger.info { "Game server started" }
            }
            onEnd {
                logger.info { "Game server ended" }
            }
            repeat(1) {
                competition("Competition $it") {
                    onBegin {
                        logger.info { "Competition $it started" }
                    }
                    onEnd {
                        logger.info { "Competition $it ended" }
                    }
                    rules {
                        rounds = 25
                    }
                    player("Matthew") {
                        titForTat()
                        titForTat()
                    }
                    player("Paul") {
//                        titForTat()
                    }
                    player("Anh") {
//                        titForTat()
                    }
                }
            }
        }
    }
}