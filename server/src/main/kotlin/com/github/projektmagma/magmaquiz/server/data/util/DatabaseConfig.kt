package com.github.projektmagma.magmaquiz.server.data.util

import io.ktor.server.application.*

object DatabaseConfig {

    lateinit var host: String
        private set

    lateinit var port: String
        private set

    lateinit var user: String
        private set

    lateinit var password: String
        private set

    lateinit var database: String
        private set

    fun populate(environment: ApplicationEnvironment) {
        host = environment.config.propertyOrNull("database.host")?.getString()!!
        port = environment.config.propertyOrNull("database.port")?.getString()!!
        user = environment.config.propertyOrNull("database.user")?.getString()!!
        password = environment.config.propertyOrNull("database.password")?.getString()!!
        database = environment.config.propertyOrNull("database.database")?.getString()!!
    }
}