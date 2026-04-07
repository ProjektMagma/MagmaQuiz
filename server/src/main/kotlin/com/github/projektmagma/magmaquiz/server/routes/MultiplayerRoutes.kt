package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.RoomsDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.RoomSettingsValue
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import java.util.*

fun Application.multiplayerRoutes(roomsDataController: RoomsDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/multiplayerRooms") {
                webSocket("/join/{roomId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val roomId = UUID.fromString(call.parameters["roomId"])
                    roomsDataController.roomConnect(session, roomId, this)

                }

                post("/create/") {
                    val session = call.sessions.get<UserSession>()!!
                    val postContent = call.receive<RoomSettingsValue>()

                    call.respondToResource(roomsDataController.roomCreate(session, postContent))
                }

                get("/roomList/{count}/{offset}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()

                    call.respondToResource(roomsDataController.roomList(session, count, offset, ""))
                }

                get("/roomList/{count}/{offset}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()
                    val stringToSearch = call.parameters["stringToSearch"]!!

                    call.respondToResource(roomsDataController.roomList(session, count, offset, stringToSearch))
                }
            }

        }
    }
}