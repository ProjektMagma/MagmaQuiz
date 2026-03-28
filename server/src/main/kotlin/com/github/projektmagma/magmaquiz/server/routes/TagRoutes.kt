package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.tagRoutes(quizDataController: QuizDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("quiz/tags") {
                get("/{count}/") {
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizGetPossibleTags(count, ""))
                }

                get("/{count}/{stringToSearch}") {
                    val stringToSearch = call.parameters["stringToSearch"]!!
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizGetPossibleTags(count, stringToSearch))
                }
            }
        }
    }
}