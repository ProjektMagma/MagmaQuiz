package com.github.projektmagma.magmaquiz.server.data.rooms

import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.QuizEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.UserAnswer
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.IncomingMessage
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage.Error.Forbidden
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage.Error.NoQuestion
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class GameRoom {

    constructor(roomSettings: RoomSettingsEntity) {
        _roomSettingsEntity = roomSettings
    }

    val roomId get() = _roomSettingsEntity.roomId
    val roomName get() = _roomSettingsEntity.roomName
    val isClosing get() = _roomSettingsEntity.isClosing
    val isRoomEmpty get() = _userConnections.isEmpty()
    private var _roomSettingsEntity: RoomSettingsEntity
    private val _userConnections: MutableList<UserConnection> = mutableListOf()
    private var _ownerId: UUID? = null
    private var _currentQuestionId: UUID? = null

    private val _userAnswerList = mutableMapOf<UUID, MutableList<UserAnswer>>()
    private val _scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun connectUser(userConnection: UserConnection): Boolean {
        synchronized(_userConnections) {
            if (_userConnections.any { transaction { it.userEntity.id == userConnection.userEntity.id } })
                return false
            _scope.launch {

                @Suppress("UNCHECKED_CAST")
                userConnection.webSocketSession.send(
                    Frame.Text(
                        Json.encodeToString(
                            _roomSettingsEntity.toDomain(
                                userConnection.userEntity,
                                _userConnections.map {
                                    it.userEntity.toDomain(
                                        UserConversionCommand.ForeignUser(
                                            userConnection.userEntity
                                        )
                                    )
                                } as List<ForeignUser>
                            ))
                    )
                )
                _roomSettingsEntity.connectedUsers++
                broadcast(BroadcastCommand.UserJoined(userConnection.userEntity))
            }
            _userConnections.add(userConnection)
            if (transaction { userConnection.userEntity.id == _roomSettingsEntity.roomOwner.id })
                _ownerId = transaction { userConnection.userEntity.id.value }
        }
        return true
    }


    fun changeRoomSettings(roomName: String, quizEntity: QuizEntity, questionTimeInMillis: Long) {
        _roomSettingsEntity.roomName = roomName
        _roomSettingsEntity.currentQuiz = quizEntity
        _roomSettingsEntity.questionTimeInMillis = questionTimeInMillis

        _scope.launch {
            broadcast(BroadcastCommand.SettingsChanged(_roomSettingsEntity))
        }
    }

    fun closeRoom() {
        _scope.launch {
            _userConnections.forEach { (_, connection) ->
                connection.outgoing.close()
            }
            _roomSettingsEntity.isClosing = true
        }
    }

    fun getRoomSettings(caller: UserEntity): RoomSettings {
        return _roomSettingsEntity.toDomain(caller)
    }

    fun isUserOwner(userEntity: UserEntity): Boolean {
        return transaction { userEntity.id == _roomSettingsEntity.roomOwner.id }
    }

    fun startGame() {
        val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        _userAnswerList.clear()

        gameScope.launch {
            _roomSettingsEntity.isInProgress = true
            _roomSettingsEntity.currentQuiz.getQuestions()
                .forEach {
                    _currentQuestionId = transaction { it.id.value }
                    _userAnswerList[_currentQuestionId!!] = mutableListOf()
                    broadcast(BroadcastCommand.NextQuestion(it))
                    delay(_roomSettingsEntity.questionTimeInMillis.milliseconds)
                }

            _roomSettingsEntity.isInProgress = false
            broadcast(BroadcastCommand.GameEnded(_userAnswerList))
        }

    }

    suspend fun handleResponses(userConnection: UserConnection) {
        runCatching {
            val userId = transaction { userConnection.userEntity.id.value }
            userConnection.webSocketSession.incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    try {
                        when (val receivedMessage =
                            Json.decodeFromString<IncomingMessage>(frame.readText())) {
                            is IncomingMessage.Answer -> {
                                if (_currentQuestionId == null)
                                    userConnection.webSocketSession.outgoing.send(
                                        encodedFrame(
                                            NoQuestion("No question to answer")
                                        )

                                    )
                                else {
                                    _userAnswerList[_currentQuestionId]?.add(
                                        UserAnswer(userId, receivedMessage.answerId)
                                    )
                                    broadcast(BroadcastCommand.UserAnswered(userConnection.userEntity))
                                }
                            }

                            IncomingMessage.StartGame -> {
                                if (_ownerId == userId)
                                    startGame()
                                else userConnection.webSocketSession.outgoing.send(
                                    encodedFrame(
                                        Forbidden("You are not owning this room")
                                    )
                                )

                            }

                            IncomingMessage.CloseRoom -> {
                                if (_ownerId == userId)
                                    closeRoom()
                                else userConnection.webSocketSession.outgoing.send(
                                    encodedFrame(
                                        Forbidden("You are not owning this room")
                                    )
                                )
                            }

                            IncomingMessage.Disconnect -> {
                                broadcast(BroadcastCommand.UserLeft(userConnection.userEntity))
                                synchronized(_userConnections)
                                {
                                    _roomSettingsEntity.connectedUsers--
                                    userConnection.webSocketSession.outgoing.close()
                                    _userConnections.remove(userConnection)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        userConnection.webSocketSession.outgoing.send(Frame.Text(e.toString()))
                    }

                }
            }
        }.onFailure { e ->
            broadcast(BroadcastCommand.UserLeft(userConnection.userEntity))
            synchronized(_userConnections)
            {
                _roomSettingsEntity.connectedUsers--
                userConnection.webSocketSession.outgoing.close(e)
                _userConnections.remove(userConnection)
                println("WebSocket exception: ${e.localizedMessage}")
            }


        }
    }

    suspend fun broadcast(broadcastCommand: BroadcastCommand) {
        _userConnections.forEach { (userEntity, wsSession) ->
            wsSession.outgoing.send(
                encodedFrame(
                    when (broadcastCommand) {
                        is BroadcastCommand.UserJoined ->
                            OutgoingMessage.UserJoined(
                                broadcastCommand.userEntity.toDomain(
                                    UserConversionCommand.ForeignUser(userEntity)
                                ) as ForeignUser
                            )

                        is BroadcastCommand.UserLeft ->
                            OutgoingMessage.UserLeft(transaction { broadcastCommand.userEntity.id.value })

                        is BroadcastCommand.SettingsChanged ->
                            OutgoingMessage.SettingsChanged(
                                _roomSettingsEntity.toDomain(userEntity)
                            )

                        is BroadcastCommand.NextQuestion -> {
                            OutgoingMessage.NextQuestion(
                                broadcastCommand.quizQuestionEntity.toDomain(
                                    ConversionCommand.Default
                                )
                            )
                        }

                        is BroadcastCommand.UserAnswered ->
                            OutgoingMessage.UserAnswered(transaction { broadcastCommand.userEntity.id.value })

                        is BroadcastCommand.GameEnded -> OutgoingMessage.GameEnded(broadcastCommand.userAnswerMap)
                    }
                )
            )
        }
    }
}