package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.data.rest.values.ImageValue
import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


fun Application.settingsRoutes(settingsDataController: SettingsDataController) {
    routing {
        authenticate("session-auth") {
            route("/settings") {

                post("/changePassword") {
                    val session = call.sessions.get<UserSession>()!!
                    val postContent = call.receive<ChangePasswordValue>()


                    call.respondToResource(
                        settingsDataController.settingsChangePassword(
                            session, postContent
                        )
                    )
                }

                post("/changeProfilePicture") {
                    val session = call.sessions.get<UserSession>()!!
                    val postContent = call.receive<ImageValue>()
                    call.respondToResource(
                        settingsDataController.settingsChangeProfilePicture(
                            session, postContent
                        )
                    )
                }

                get("/delete") {
                    val session = call.sessions.get<UserSession>()!!
                    call.sessions.clear<UserSession>()
                    call.respondToResource(settingsDataController.settingsDelete(session, false))
                }

            }
        }
    }
}

