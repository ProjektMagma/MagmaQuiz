package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object QuizzesTagsTable : ExtUUIDTable("quizzes_tags", "tag_id") {
    val tagName = varchar("tag_name", 255)
}