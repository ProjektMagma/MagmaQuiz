package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.HasChildrenConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class QuizEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTable),
    DomainCapable<Quiz, HasChildrenConversionCommand> {
    companion object : UUIDEntityClass<QuizEntity>(QuizzesTable)

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    var likesCount by QuizzesTable.likesCount
    val questionList by QuestionEntity referrersOn QuestionsTable.quiz

    override fun toDomain(command: HasChildrenConversionCommand): Quiz {
        return transaction {
            when (command) {
                HasChildrenConversionCommand.WithChildren ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        quizCreatorName = quizCreator.userName,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = questionList.map { it.toDomain(ConversionCommand.Default) }
                    )

                HasChildrenConversionCommand.NoChildren ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        quizCreatorName = quizCreator.userName,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                    )
            }
        }
    }
}
