package com.github.projektmagma.magmaquiz.server.data.abstraction

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

abstract class ExtIntIdTable(name: String, columnName: String = "id") : IntIdTable(name, columnName) {
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}