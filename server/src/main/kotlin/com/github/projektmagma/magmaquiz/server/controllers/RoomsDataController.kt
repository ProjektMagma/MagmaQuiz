package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.rooms.GameRoom
import com.github.projektmagma.magmaquiz.server.data.rooms.RoomSettingsEntity
import com.github.projektmagma.magmaquiz.server.data.rooms.UserConnection
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.QuizRepository
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.RoomSettingsValue
import io.ktor.http.HttpStatusCode
import io.ktor.websocket.CloseReason
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

class RoomsDataController(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository
) {
    private val _openRoomList = mutableListOf<GameRoom>()


    init {
        val healthScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        healthScope.launch {
            while (true) {
                delay(2.minutes)
                _openRoomList.removeAll { room ->
                    room.isClosing || room.isRoomEmpty
                }
            }
        }
    }

    suspend fun roomConnect(session: UserSession, roomId: UUID, webSocketSession: WebSocketSession) {
        val thisUser = userRepository.getUserData(session)
        val userConnection = UserConnection(thisUser, webSocketSession)
        val existingRoom = _openRoomList.firstOrNull { it.roomId == roomId }

        if (existingRoom == null || existingRoom.isClosing) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room $roomId is closed"))
            return
        }

        val hasAccess = existingRoom.isPublic ||
                existingRoom.isUserOwner(thisUser) ||
                thisUser.checkFriendship(existingRoom.roomOwner) == FriendshipStatus.Friends

        if (!hasAccess) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "You're not allowed to join this room"))
            return
        }

        if (!existingRoom.connectUser(userConnection)) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Cannot connect to room $roomId"))
            return
        }

        existingRoom.handleResponses(userConnection)
    }

    fun roomCreate(session: UserSession, roomSettingsValue: RoomSettingsValue): NetworkResource<RoomSettings> {
        val thisUser = userRepository.getUserData(session)

        val existingRoom = _openRoomList.firstOrNull { !it.isClosing && it.isUserOwner(thisUser) }

        if (existingRoom != null) return NetworkResource.Error(HttpStatusCode.Conflict)

        val quizData = quizRepository.getQuizData(roomSettingsValue.quizId) ?: return NetworkResource.Error(
            HttpStatusCode.NotFound
        )

        val gameRoom = GameRoom(
            roomSettingsEntity = RoomSettingsEntity(
                roomName = roomSettingsValue.roomName,
                currentQuiz = quizData,
                roomOwner = thisUser,
                questionTimeInMillis = roomSettingsValue.questionTimeInMillis,
                isPublic = roomSettingsValue.isPublic
            )
        )

        _openRoomList.add(gameRoom)

        return NetworkResource.Success(gameRoom.getRoomSettings(thisUser))
    }

    fun roomList(
        session: UserSession,
        count: Int,
        offset: Int,
        stringToSearch: String
    ): NetworkResource<List<RoomSettings>> {
        val thisUser = userRepository.getUserData(session)

        val roomList = _openRoomList
            .asSequence()
            .filter { !it.isClosing && it.roomName.contains(stringToSearch, true) }
            .filter {
                it.isPublic ||
                        it.isUserOwner(thisUser) ||
                        thisUser.checkFriendship(it.roomOwner) == FriendshipStatus.Friends
            }
            .drop(offset)
            .take(count)
            .map { it.getRoomSettings(thisUser) }
            .toList()

        return NetworkResource.Success(roomList, HttpStatusCode.PartialContent)

    }
}