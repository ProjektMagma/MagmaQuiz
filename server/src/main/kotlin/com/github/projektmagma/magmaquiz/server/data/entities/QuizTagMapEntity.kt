package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTagsMapTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class QuizTagMapEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTagsMapTable) {
    companion object : UUIDEntityClass<QuizTagMapEntity>(QuizzesTagsMapTable)

    var tag by QuizTagEntity referencedOn QuizzesTagsMapTable.tag
    var quiz by QuizEntity referencedOn QuizzesTagsMapTable.quiz
}