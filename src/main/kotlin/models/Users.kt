package models

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    // 1. Username (Must be unique, so we make it the Primary Key)
    val username = varchar("username", 50)

    // 2. Password (Stores the BCrypt Hash, not plain text!)
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(username)
}