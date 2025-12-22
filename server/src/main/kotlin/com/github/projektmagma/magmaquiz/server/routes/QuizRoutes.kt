package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.data.rest.values.CreateOrModifyQuizValue
import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

fun Application.quizRoutes(quizDataController: QuizDataController) {
    routing {
        authenticate("session-auth") {
            route("/quiz") {
                get("/{id}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["id"]!!)
                    call.respondToResource(quizDataController.tryGetQuizData(quizId, session))

                }

                get("/find/{stringToSearch}") {
                    val stringToSearch = call.parameters["stringToSearch"]!!

                    call.respondToResource(quizDataController.findQuizzesByName(stringToSearch))
                }

                post("/create") {
                    val postContent = call.receive<CreateOrModifyQuizValue>()
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(
                        quizDataController.tryCreateQuiz(
                            postContent,
                            session
                        )
                    )
                }

                post("/modify") {
                    val postContent = call.receive<CreateOrModifyQuizValue>()
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(
                        quizDataController.tryModifyQuiz(
                            postContent,
                            session
                        )
                    )
                }
            }
        }
    }
}