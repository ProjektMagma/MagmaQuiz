package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.daos.QuestionsTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class QuestionEntity(id: EntityID<Int>) : ExtIntEntity(id, QuestionsTable) {
    companion object : IntEntityClass<QuestionEntity>(QuestionsTable)

    var quiz by QuizEntity referencedOn QuestionsTable.quiz
    var questionNumber by QuestionsTable.questionNumber
    var questionContent by QuestionsTable.questionContent
    var questionImage by QuestionsTable.questionImage
    var firstAnswer by QuestionsTable.firstAnswer
    var secondAnswer by QuestionsTable.secondAnswer
    var thirdAnswer by QuestionsTable.thirdAnswer
    var fourthAnswer by QuestionsTable.fourthAnswer
    var correctAnswerNumber by QuestionsTable.correctAnswerNumber
}