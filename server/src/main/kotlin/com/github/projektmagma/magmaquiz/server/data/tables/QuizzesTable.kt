package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesTable : ExtUUIDTable("quizzes", "quiz_id") {
    val quizCreator = reference("user_id", UsersTable)
    val quizName = varchar("quiz_name", 255)
    val quizDescription = text("quiz_description").nullable()
    val quizImage = binary("quiz_image").nullable()
    val isPublic = bool("is_public").default(false)
    val likesCount = integer("likes_count")
}