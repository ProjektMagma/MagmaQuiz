package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesTagsMapTable : ExtUUIDTable("quizzes_tags_map", "quizzes_tags_map_id") {
    val tag = reference("tag_id", QuizzesTagsTable)
    val quiz = reference("quiz_id", QuizzesTable)
}  