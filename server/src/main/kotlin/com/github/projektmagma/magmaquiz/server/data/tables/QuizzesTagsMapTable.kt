package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesTagsMapTable : ExtUUIDTable("quizzes_tags_map", "quizzes_tags_map_id") {
    val tagId = reference("tag_id", QuizzesTagsTable)
    val quizId = reference("quiz_id", QuizzesTable)
}  