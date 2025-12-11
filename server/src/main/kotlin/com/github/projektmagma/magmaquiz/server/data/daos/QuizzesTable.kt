package com.github.projektmagma.magmaquiz.server.data.daos

object QuizzesTable : ExtIntIdTable("quizzes", "quiz_id") {
    val quizCreator = reference("user_id", UsersTable)
    val quizName = varchar("quiz_name", 255)
    val quizDescription = text("quiz_description")
    val quizImage = binary("quiz_image").nullable()
}