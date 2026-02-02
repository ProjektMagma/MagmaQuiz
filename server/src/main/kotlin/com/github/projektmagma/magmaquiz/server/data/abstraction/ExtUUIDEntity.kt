package com.github.projektmagma.magmaquiz.server.data.abstraction

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.EntityBatchUpdate
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import java.time.Instant
import java.util.*

abstract class ExtUUIDEntity(id: EntityID<UUID>, table: ExtUUIDTable) : UUIDEntity(id) {
    var isActive by table.isActive
    var createdAt by table.createdAt
    var modifiedAt by table.modifiedAt

    override fun flush(batch: EntityBatchUpdate?): Boolean {
        modifiedAt = Instant.now()
        return super.flush(batch)
    }
}