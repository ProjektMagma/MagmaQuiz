package com.github.projektmagma.magmaquiz.server.data.daos

object QuestionsTable : ExtIntIdTable("questions", "question_id") {
    val quiz = reference("quiz_id", QuizzesTable)
    val questionNumber = integer("question_number")
    val questionContent = text("question_content")
    val questionImage = binary("question_image").nullable()
    val firstAnswer = varchar("first_answer", 255)
    val secondAnswer = varchar("second_answer", 255)
    val thirdAnswer = varchar("third_answer", 255)
    val fourthAnswer = varchar("fourth_answer", 255)
    val correctAnswerNumber = integer("correct_answer_number")
}