package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.server.storage.ExposedSessionStorage
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ConfirmEmailChangeValue
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
        authenticate(AuthTypes.SessionAuth) {
            route("/settings") {

                get("/verificationCode/{email}") {
                    val session = call.sessions.get<UserSession>()!!
                    val email = call.parameters["email"]!!
                    call.respondToResource(
                        settingsDataController.settingsVerificationCode(session, email)
                    )
                }

                route("/change") {

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
                        val session = call.sessions.get<UserSession>()!!
                        val email = call.parameters["email"]!!

                        call.respondToResource(
                            settingsDataController.checkIsEmailTaken(
                                session, 
                                email
                            )
                        )
                    }
                    
                    get("/email/confirm") {
                        val session = call.sessions.get<UserSession>()!!
                        val body = call.receive<ConfirmEmailChangeValue>()

                        call.respondToResource(
                            settingsDataController.settingsConfirmEmailChange(
                                session,
                                body.email,
                                body.verificationCode
                            )
                        )
                    }

                    get("/password/{newPassword}/{verificationCode}") {
                        val session = call.sessions.get<UserSession>()!!
                        val newPassword = call.parameters["newPassword"]!!
                        val verificationCode = call.parameters["verificationCode"]!!

                        call.respondToResource(
                            settingsDataController.settingsChangePassword(
                                session, newPassword, verificationCode
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
                delete("/deleteAccount") {
                    val session = call.sessions.get<UserSession>()!!
                    call.sessions.clear<UserSession>()
                    exposedSessionStorage.clearAllUserSessions(session.userId)
                    call.respondToResource(settingsDataController.settingsDelete(session, false))
                }
            }
        }
    }
}

