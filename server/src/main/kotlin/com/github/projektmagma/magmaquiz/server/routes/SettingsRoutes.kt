package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeEmailValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeUserNameValue
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


fun Application.settingsRoutes(settingsDataController: SettingsDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/settings") {

                route("/change") {

                    post("/userName") {
                        val session = call.sessions.get<UserSession>()!!
                        val postContent = call.receive<ChangeUserNameValue>()

                        call.respondToResource(
                            settingsDataController.settingsChangeUserName(
                                session, postContent
                            )
                        )
                    }

                    post("/email") {
                        val session = call.sessions.get<UserSession>()!!
                        val postContent = call.receive<ChangeEmailValue>()

                        call.respondToResource(
                            settingsDataController.settingsChangeEmail(
                                session, postContent
                            )
                        )
                    }

                    post("/password") {
                        val session = call.sessions.get<UserSession>()!!
                        val postContent = call.receive<ChangePasswordValue>()

                        call.respondToResource(
                            settingsDataController.settingsChangePassword(
                                session, postContent
                            )
                        )
                    }

                    post("/profilePicture") {
                        val session = call.sessions.get<UserSession>()!!
                        val postContent = call.receive<ChangeProfilePictureValue>()

                        call.respondToResource(
                            settingsDataController.settingsChangeProfilePicture(
                                session, postContent
                            )
                        )
                    }
                }
                get("/deleteAccount") {
                    val session = call.sessions.get<UserSession>()!!
                    call.sessions.clear<UserSession>()
                    call.respondToResource(settingsDataController.settingsDelete(session, false))
                }
            }
        }
    }
}

