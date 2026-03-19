package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

fun Application.reviewRoutes(quizDataController: QuizDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("quiz/reviews") {

                get("/yourReview/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)

                    call.respondToResource(quizDataController.quizYourReview(session, quizId))
                }

                get("/{quizId}") {
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizReviews(quizId))

                }

                post("/create/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    val review = call.receive<QuizReview>()

                    call.respondToResource(quizDataController.quizCreateReview(session, quizId, review))
                }

                delete("/delete/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)

                    call.respondToResource(quizDataController.quizDeleteReview(session, quizId))
                }
            }
        }
    }
}