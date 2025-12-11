package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.daos.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.daos.QuizzesTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class QuizEntity(id: EntityID<Int>) : ExtIntEntity(id, QuizzesTable) {
    companion object : IntEntityClass<QuizEntity>(QuizzesTable)

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    val questionList by QuestionEntity referrersOn QuestionsTable.quiz
}