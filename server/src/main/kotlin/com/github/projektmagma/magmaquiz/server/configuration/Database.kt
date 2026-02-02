package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.tables.*
import com.github.projektmagma.magmaquiz.server.data.util.DatabaseConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

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
            UsersSessionsTable,
            QuizzesTable,
            QuestionsTable,
            AnswersTable,
            FavoriteQuizzesTable,
            FriendshipsTable
        )
        SchemaUtils.addMissingColumnsStatements(
            UsersTable,
            UsersSessionsTable,
            QuizzesTable,
            QuestionsTable,
            AnswersTable,
            FavoriteQuizzesTable,
            FriendshipsTable
        )
    }
}