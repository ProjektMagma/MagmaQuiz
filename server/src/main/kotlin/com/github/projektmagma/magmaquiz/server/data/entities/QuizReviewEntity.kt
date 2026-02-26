package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesReviewsTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizReviewEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesReviewsTable),
    DomainCapable<QuizReview, ConversionCommand> {
    companion object : UUIDEntityClass<QuizReviewEntity>(QuizzesReviewsTable)

    var author by UserEntity referencedOn QuizzesReviewsTable.author
    var quiz by QuizEntity referencedOn QuizzesReviewsTable.quiz
    var rating by QuizzesReviewsTable.rating
    var comment by QuizzesReviewsTable.comment

    override fun toDomain(command: ConversionCommand): QuizReview {
        return transaction {
            QuizReview(
                author = author.toDomain(UserConversionCommand.ForeignUser()) as ForeignUser,
                rating = rating,
                comment = comment
            )
        }
    }
}