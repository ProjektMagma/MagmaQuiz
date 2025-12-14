package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.DatabaseConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {

    DatabaseConfig.populate(environment)

    Database.connect(
        "jdbc:postgresql://${DatabaseConfig.host}:${DatabaseConfig.port}/${DatabaseConfig.database}",
        driver = "org.postgresql.Driver",
        user = DatabaseConfig.user,
        password = DatabaseConfig.password
    )

    transaction {
        SchemaUtils.create(
            UsersTable,
            QuizzesTable,
            QuestionsTable,
            AnswersTable
        )
        SchemaUtils.addMissingColumnsStatements(
            UsersTable,
            QuizzesTable,
            QuestionsTable,
            AnswersTable
        )
    }
}