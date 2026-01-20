@file:Suppress("D")

package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Pokemon
import models.PokemonData
import models.UserSession
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.pokeRoutes() {

    authenticate("auth-session") {

        route("/catch") {
            post {
                val newPokemon: Pokemon = call.receive()
                val user = call.principal<UserSession>()!!

                transaction {
                    PokemonData.insert { row ->
                        row[name] = newPokemon.name
                        row[type] = newPokemon.type
                        row[powerLevel] = newPokemon.powerLevel
                        row[owner] = user.username
                    }
                }
                call.respondText("Pokemon Captured by ${user.username}!")
            }
        }

        route("/pokedex") {
            get {
                val user = call.principal<UserSession>()!!

                val myPokemon = transaction {
                    PokemonData.selectAll()
                        .where { PokemonData.owner eq user.username }
                        .map { row ->
                            Pokemon(
                                name = row[PokemonData.name],
                                type = row[PokemonData.type],
                                powerLevel = row[PokemonData.powerLevel]
                            )
                        }
                }
                call.respond(HttpStatusCode.OK, myPokemon)
            }
        }

        route("/release/{name}") {
            delete {
                val user = call.principal<UserSession>()!!
                val pokemonName = call.parameters["name"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val deletedRows = transaction {
                    PokemonData.deleteWhere {
                        (PokemonData.owner eq user.username) and (PokemonData.name eq pokemonName)
                    }
                }

                if (deletedRows > 0) {
                    call.respondText("Goodbye, $pokemonName! You are free.")
                } else {
                    call.respondText(
                        "You don't own a Pokemon named $pokemonName (or it doesn't exist).",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }

        route("/train/{name}") {
            patch {
                val user = call.principal<UserSession>()!!
                val pokemonName = call.parameters["name"] ?: return@patch call.respond(HttpStatusCode.BadRequest)

                val responseMessage = transaction {
                    val row = PokemonData.selectAll()
                        .where { (PokemonData.owner eq user.username) and (PokemonData.name eq pokemonName) }
                        .singleOrNull()

                    if (row == null) {
                        null
                    } else {

                        val oldPower = row[PokemonData.powerLevel]
                        val newPower = oldPower + 10

                        PokemonData.update({ PokemonData.name eq pokemonName }) {
                            it[powerLevel] = newPower
                        }
                        "$pokemonName power has been updated to $newPower from $oldPower!"
                    }
                }

                if (responseMessage == null) {
                    call.respondText("Pokemon not found or you don't own it.", status = HttpStatusCode.NotFound)
                } else {
                    call.respondText(responseMessage)
                }
            }
        }
    }
}