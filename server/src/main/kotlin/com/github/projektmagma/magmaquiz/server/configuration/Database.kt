package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.daos.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.daos.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.util.DatabaseConfig
import com.github.projektmagma.magmaquiz.server.data.daos.UsersTable
import io.ktor.server.application.Application
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
        SchemaUtils.create(UsersTable)
        SchemaUtils.create(QuizzesTable)
        SchemaUtils.create(QuestionsTable)
    }
}