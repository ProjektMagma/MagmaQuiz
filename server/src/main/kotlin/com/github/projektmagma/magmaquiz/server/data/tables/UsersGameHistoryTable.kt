package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object UsersGameHistoryTable : ExtUUIDTable("users_game_history", "history_id") {
    val user = reference("user_id", UsersTable)
    val quiz = reference("quiz_id", QuizzesTable)
}