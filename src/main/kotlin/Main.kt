import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import models.PokemonData
import models.UserSession
import models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import routes.pokeRoutes
import routes.userRoutes

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }

        install(Sessions) {
            cookie<UserSession>("USER_SESSION") {
                cookie.maxAgeInSeconds = 60 * 60
                cookie.path = "/"
            }
        }
        install(Authentication) {
            session<UserSession>("auth-session") {
                validate { session ->
                    // If the cookie has a username, let them in!
                    if (session.username.isNotEmpty()) {
                        session
                    } else {
                        null
                    }
                }
                challenge {
                    call.respondText("You must be logged in to catch Pokemon!", status = HttpStatusCode.Unauthorized)
                }
            }
        }

        Database.connect(
            url = "jdbc:h2:file:./data;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "root",
            password = ""
        )

        transaction {
            SchemaUtils.create(PokemonData, Users)
        }

        routing {
            pokeRoutes()
            userRoutes()
        }
    }.start(wait = true)
}
