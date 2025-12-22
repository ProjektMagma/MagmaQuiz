package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuestionsTable : ExtUUIDTable("questions", "question_id") {
    val quiz = reference("quiz_id", QuizzesTable)
    val questionNumber = integer("question_number")
    val questionContent = text("question_content")
    val questionImage = binary("question_image").nullable()

}