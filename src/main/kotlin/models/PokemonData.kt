package models

import org.jetbrains.exposed.sql.Table

object PokemonData : Table() {
    val name = varchar("name", 255)
    val type = varchar("type", 255)
    val powerLevel = integer("power_level")
    val owner = varchar("owner", 50)
}