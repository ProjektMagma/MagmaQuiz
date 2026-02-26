package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesQuestionsAnswersTable : ExtUUIDTable("quizzes_questions_answers", "answer_id") {

    val question = reference("question_id", QuizzesQuestionsTable)
    val answerContent = text("option_content")
    val isCorrect = bool("is_correct")
}