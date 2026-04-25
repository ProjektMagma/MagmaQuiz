package com.github.projektmagma.magmaquiz.app.game.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.app.game.data.service.GameService
import com.github.projektmagma.magmaquiz.app.quizzes.data.service.QuizService
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.map
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GameRepository(
    private val gameService: GameService,
    private val quizService: QuizService
) {
    val roomSettings = MutableStateFlow<RoomSettings?>(null)
    val questions = MutableStateFlow<List<Question>>(emptyList())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _messages = MutableSharedFlow<WsEvent>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val messages = _messages.asSharedFlow()

    init {
        scope.launch {
            gameService.getMessages().collect { event ->
                when (event) {
                    is WsEvent.OutGoing -> {
                        when (val msg = event.message) {
                            is WebSocketMessages.OutgoingMessage.NextQuestion -> {
                                questions.update { it + msg.question }
                            }

                            is WebSocketMessages.OutgoingMessage.SettingsChanged -> {
                                roomSettings.value = msg.roomSettings
                            }

                            is WebSocketMessages.OutgoingMessage.UserJoined -> {
                                roomSettings.update { state ->
                                    val current = state ?: return@update state
                                    val updatedUsers = current.userList
                                        .plus(msg.user).distinctBy { it.userId }
                                    current.copy(
                                        userList = updatedUsers,
                                        connectedUsers = updatedUsers.size
                                    )
                                }
                            }

                            is WebSocketMessages.OutgoingMessage.UserLeft -> {
                                roomSettings.update { state ->
                                    val current = state ?: return@update state
                                    val updatedUsers = current.userList.filter { it.userId != msg.userId }
                                    current.copy(
                                        userList = updatedUsers,
                                        connectedUsers = updatedUsers.size
                                    )
                                }
                            }

                            else -> Unit
                        }
                    }

                    is WsEvent.Closed -> Unit
                }

                _messages.tryEmit(event)
            }
        }
    }

    suspend fun createGameRoom(
        roomName: String,
        id: UUID,
        isPublic: Boolean,
        questionTimeMillis: Long
    ): Resource<RoomSettings, NetworkError> {
        return gameService.createGameRoom(roomName, id, isPublic, questionTimeMillis)
    }

    suspend fun getRoomList(
        roomName: String = "",
        count: Int = 5,
        offset: Int
    ): Resource<List<RoomSettings>, NetworkError> {
        return gameService.getRoomList(
            name = roomName,
            count = count,
            offset = offset * count
        )
    }

    suspend fun joinRoom(id: UUID): Resource<Unit, NetworkError> {
        questions.value = emptyList()
        return gameService.joinRoom(id)
            .whenSuccess {
                val room = it.data
                roomSettings.value = room
                quizService.markQuizAsPlayed(room.currentQuiz.id!!)
            }
            .whenError {
                Resource.Error(it.error)
            }
            .map { }
    }

    suspend fun sendMessage(message: WebSocketMessages.IncomingMessage) {
        gameService.sendMessage(message)
    }
}