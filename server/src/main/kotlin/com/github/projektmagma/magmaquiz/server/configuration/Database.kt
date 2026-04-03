package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.tables.*
import com.github.projektmagma.magmaquiz.server.data.util.DatabaseConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import java.io.File

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun Application.configureDatabase() {

    DatabaseConfig.populate(environment)

    Database.connect(
        "jdbc:postgresql://${DatabaseConfig.host}:${DatabaseConfig.port}/${DatabaseConfig.database}",
        driver = "org.postgresql.Driver",
        user = DatabaseConfig.user,
        password = DatabaseConfig.password
    )

    transaction {

        val tables = arrayOf(
            UsersTable,
            UsersSessionsTable,
            QuizzesTable,
            QuizzesQuestionsTable,
            QuizzesQuestionsAnswersTable,
            UsersFavoriteQuizzesTable,
            UsersFriendshipsTable,
            UsersGameHistoryTable,
            QuizzesReviewsTable,
            QuizzesTagsTable,
            QuizzesTagsMapTable,
            VerificationCodesTable
        )

        SchemaUtils.run {
            create(*tables)
            addMissingColumnsStatements(*tables)
        }

        MigrationUtils.run {
            val dir = File("./migrations/")
            if (!dir.exists())
                dir.mkdir()

            generateMigrationScript(
                *tables,
                scriptDirectory = "./migrations/",
                scriptName = "migration"
            )
            dropUnmappedIndices(*tables)
            dropUnmappedSequences(*tables)
        }
    }
}