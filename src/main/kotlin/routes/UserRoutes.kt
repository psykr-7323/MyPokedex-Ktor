package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import models.UserCredentials
import models.UserSession
import models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.userRoutes() {
    route("/register") {
        post {
            val user = call.receive<UserCredentials>()
            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())

            try {
                transaction {
                    Users.insert { row ->
                        row[username] = user.username
                        row[password] = hashedPassword
                    }
                }
                call.respondText("User registered successfully!")
            } catch (e: Exception) {
                call.respondText("Username already taken! ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
    }
    route("/login") {
        post {
            val user = call.receive<UserCredentials>()

            val dbUser = transaction {
                Users.selectAll()
                    .where { Users.username eq user.username }
                    .singleOrNull()
            }

            if (dbUser == null) {
                call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
                return@post
            }

            val storedHash = dbUser[Users.password]
            val passwordMatches = BCrypt.checkpw(user.password, storedHash)

            if (passwordMatches) {
                call.sessions.set(UserSession(username = user.username))
                call.respondText("Login Successful!")
            } else {
                call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}