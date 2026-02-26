package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTagsMapTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTagsTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Tag
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizTagEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTagsTable),
    DomainCapable<Tag, ConversionCommand> {
    companion object : UUIDEntityClass<QuizTagEntity>(QuizzesTagsTable)

    var tagName by QuizzesTagsTable.tagName
    private val quizzesList by QuizEntity via QuizzesTagsMapTable

    var quizzesCount = quizzesList.count()

    override fun toDomain(command: ConversionCommand): Tag {
        return transaction {
            Tag(
                tagName = tagName,
                quizzesCount = quizzesCount
            )
        }
    }
}