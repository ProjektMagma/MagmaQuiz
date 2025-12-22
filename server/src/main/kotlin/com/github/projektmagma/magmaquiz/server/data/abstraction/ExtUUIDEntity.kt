package com.github.projektmagma.magmaquiz.server.data.abstraction

import org.jetbrains.exposed.dao.EntityBatchUpdate
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
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