package models

import io.ktor.server.auth.*

// This is the "ID Card" inside the cookie
data class UserSession(val username: String) : Principal