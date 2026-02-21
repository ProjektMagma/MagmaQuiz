package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object UsersFavoriteQuizzesTable : ExtUUIDTable("users_favorite_quizzes", "favorite_quiz_id") {
    val quiz = reference("quiz_id", QuizzesTable)
    val user = reference("user_id", UsersTable)
}