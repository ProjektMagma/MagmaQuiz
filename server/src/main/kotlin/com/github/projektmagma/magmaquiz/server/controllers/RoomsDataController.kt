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
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.*
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
                _openRoomList.forEach { room ->
                    if (room.isClosing || room.isRoomEmpty)
                        _openRoomList.remove(room)
                }
            }
        }
    }

    suspend fun roomConnect(session: UserSession, roomId: UUID, webSocketSession: WebSocketSession) {
        val thisUser = userRepository.getUserData(session)
        val userConnection = UserConnection(thisUser, webSocketSession)
        val existingRoom = _openRoomList.firstOrNull { it.roomId == roomId }

        if (existingRoom == null) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room $roomId not found"))
            return
        }

        if (!existingRoom.connectUser(userConnection)) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Cannot connect to room $roomId"))
            return
        }

        if (!existingRoom.isPublic && thisUser.checkFriendship(existingRoom.roomOwner) != FriendshipStatus.Friends) {
            webSocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "You're not a friend of room owner"))
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
            .filter { if (it.isPublic) true else thisUser.checkFriendship(it.roomOwner) == FriendshipStatus.Friends }
            .drop(offset)
            .take(count)
            .map { it.getRoomSettings(thisUser) }
            .toList()

        return NetworkResource.Success(roomList, HttpStatusCode.PartialContent)

    }
}