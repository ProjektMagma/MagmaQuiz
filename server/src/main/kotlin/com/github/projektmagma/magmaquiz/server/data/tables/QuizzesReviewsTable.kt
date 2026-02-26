package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesReviewsTable : ExtUUIDTable("quizzes_reviews", "id") {
    val author = reference("review_creator", UsersTable)
    val quiz = reference("quiz_id", QuizzesTable)
    val rating = integer("rating")
    val comment = text("comment")
}