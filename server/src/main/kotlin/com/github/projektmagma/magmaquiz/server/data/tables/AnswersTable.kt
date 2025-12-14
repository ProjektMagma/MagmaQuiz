package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtIntIdTable

object AnswersTable : ExtIntIdTable("answers", "answer_id") {

    val question = reference("question_id", QuestionsTable)
    val answerContent = text("option_content")
    val isCorrect = bool("is_correct")
}