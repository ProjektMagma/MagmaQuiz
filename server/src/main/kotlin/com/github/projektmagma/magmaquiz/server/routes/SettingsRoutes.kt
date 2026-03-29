package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
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


fun Application.settingsRoutes(settingsDataController: SettingsDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/settings") {

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

                    get("/email/{newEmail}") {
                        val session = call.sessions.get<UserSession>()!!
                        val newEmail = call.parameters["newEmail"]!!

                        call.respondToResource(
                            settingsDataController.settingsChangeEmail(
                                session, newEmail
                            )
                        )
                    }

                    get("/password/{newPassword}") {
                        val session = call.sessions.get<UserSession>()!!
                        val newPassword = call.parameters["newPassword"]!!

                        call.respondToResource(
                            settingsDataController.settingsChangePassword(
                                session, newPassword
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
                    call.respondToResource(settingsDataController.settingsDelete(session, false))
                }
            }
        }
    }
}

