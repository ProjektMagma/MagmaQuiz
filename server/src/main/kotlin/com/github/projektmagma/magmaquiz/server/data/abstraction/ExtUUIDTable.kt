package com.github.projektmagma.magmaquiz.server.data.abstraction

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

abstract class ExtUUIDTable(name: String, columnName: String = "id") : UUIDTable(name, columnName) {
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}