package models

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val username = varchar("username", 50)
    val password = varchar("password", 128)
    override val primaryKey = PrimaryKey(username)
}