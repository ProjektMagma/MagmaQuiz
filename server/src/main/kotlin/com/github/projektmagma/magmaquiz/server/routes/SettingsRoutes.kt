package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.server.storage.ExposedSessionStorage
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordWithOldValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ConfirmChangeValue
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions


fun Application.settingsRoutes(
    settingsDataController: SettingsDataController,
    exposedSessionStorage: ExposedSessionStorage
) {
    routing {
        route("/settings") {
            post("/verificationCode/{email}") {
                val email = call.parameters["email"]!!
                call.respondToResource(
                    settingsDataController.settingsVerificationCode(email)
                )
            }

            route("/change") {
                route("/password") {
                    post("/verifyCode") {
                        val body = call.receive<ConfirmChangeValue>()

                        call.respondToResource(
                            settingsDataController.settingsVerifyPasswordResetCode(
                                body.email,
                                body.payload
                            )
                        )
                    }
                    
                    post("/new") {
                        val body = call.receive<ConfirmChangeValue>()

                        call.respondToResource(
                            settingsDataController.settingsChangePasswordAfterVerifiedCode(
                                body.email,
                                body.payload
                            )
                        )
                    }

                    
                    authenticate(AuthTypes.SessionAuth) {
                        post("/old") {
                            val session = call.sessions.get<UserSession>()!!
                            val body = call.receive<ChangePasswordWithOldValue>()

                            call.respondToResource(
                                settingsDataController.settingsChangePasswordWithOld(
                                    session,
                                    body.oldPassword,
                                    body.newPassword
                                )
                            )
                        }
                    }
                    
                    authenticate(AuthTypes.SessionAuth) {
                        get("/userName/{newUserName}") {
                            val session = call.sessions.get<UserSession>()!!
                            val newUserName = call.parameters["newUserName"]!!

                            call.respondToResource(
                                settingsDataController.settingsChangeUserName(
                                    session, newUserName
                                )
                            )
                        }

                        get("/email/checkIsTaken/{email}") {
                            val email = call.parameters["email"]!!

                            call.respondToResource(
                                settingsDataController.checkIsEmailTaken(email)
                            )
                        }

                        post("/email/confirm") {
                            val session = call.sessions.get<UserSession>()!!
                            val body = call.receive<ConfirmChangeValue>()

                            call.respondToResource(
                                settingsDataController.settingsConfirmEmailChange(
                                    session, body.email, body.payload
                                )
                            )
                        }

                        get("/bio/{newBio}") {
                            val session = call.sessions.get<UserSession>()!!
                            val newBio = call.parameters["newBio"]!!

                            call.respondToResource(
                                settingsDataController.settingsChangeBio(
                                    session, newBio
                                )
                            )
                        }

                        get("/countryCode/{newCountryCode}") {
                            val session = call.sessions.get<UserSession>()!!
                            val newCountryCode = call.parameters["newCountryCode"]!!

                            call.respondToResource(
                                settingsDataController.settingsChangeCountryCode(
                                    session, newCountryCode
                                )
                            )
                        }

                        get("/town/{newTown}") {
                            val session = call.sessions.get<UserSession>()!!
                            val newTown = call.parameters["newTown"]!!

                            call.respondToResource(
                                settingsDataController.settingsChangeTown(
                                    session, newTown
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
                }
                authenticate(AuthTypes.SessionAuth) {
                    delete("/deleteAccount") {
                        val session = call.sessions.get<UserSession>()!!
                        call.sessions.clear<UserSession>()
                        exposedSessionStorage.clearAllUserSessions(session.userId)
                        call.respondToResource(
                            settingsDataController.settingsDelete(
                                session,
                                false
                            )
                        )
                    }
                }
            }
        }
    }
}
