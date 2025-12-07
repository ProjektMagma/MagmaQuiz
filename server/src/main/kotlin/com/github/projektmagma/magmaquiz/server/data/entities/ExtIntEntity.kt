package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.daos.ExtIntIdTable
import org.jetbrains.exposed.dao.EntityBatchUpdate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

abstract class ExtIntEntity(id: EntityID<Int>, table: ExtIntIdTable) : IntEntity(id) {
    var isActive by table.isActive
    var createdAt by table.createdAt
    var modifiedAt by table.modifiedAt

    override fun flush(batch: EntityBatchUpdate?): Boolean {
        modifiedAt = Instant.now()
        return super.flush(batch)
    }
}