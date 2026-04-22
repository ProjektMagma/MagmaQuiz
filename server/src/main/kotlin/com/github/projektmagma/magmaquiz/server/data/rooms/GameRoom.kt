package com.github.projektmagma.magmaquiz.server.data.rooms

import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.repository.QuizRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.UserAnswer
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.IncomingMessage
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage.Error.Forbidden
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages.OutgoingMessage.Error.NoQuestion
import com.github.projektmagma.magmaquiz.shared.data.rest.values.RoomSettingsValue
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class GameRoom {

    companion object {
        private lateinit var _quizRepository: QuizRepository

        fun setRepository(quizRepository: QuizRepository) {
            _quizRepository = quizRepository
        }
    }


    constructor(roomSettingsEntity: RoomSettingsEntity) {
        _roomSettingsEntity = roomSettingsEntity
    }

    val roomId get() = _roomSettingsEntity.roomId
    val roomName get() = _roomSettingsEntity.roomName
    val isClosing get() = _roomSettingsEntity.isClosing
    val isPublic get() = _roomSettingsEntity.isPublic
    val isRoomEmpty get() = _userConnections.isEmpty()
    val roomOwner get() = _roomSettingsEntity.roomOwner

    private val _userConnections: MutableList<UserConnection> = mutableListOf()
    private val _userAnswerList = mutableMapOf<UUID, MutableList<UserAnswer>>()

    private var _roomSettingsEntity: RoomSettingsEntity
    private var _ownerId: UUID? = null
    private var _currentQuestionId: UUID? = null


    suspend fun connectUser(userConnection: UserConnection): Boolean {
        synchronized(_userConnections) {
            if (_userConnections.any { transaction { it.userEntity.id == userConnection.userEntity.id } }) {
                return false
            }
            _userConnections.add(userConnection)
            _roomSettingsEntity.connectedUsers = _userConnections.size
        }

        if (transaction { userConnection.userEntity.id == _roomSettingsEntity.roomOwner.id }) {
            _ownerId = transaction { userConnection.userEntity.id.value }
        }

        @Suppress("UNCHECKED_CAST")
        userConnection.webSocketSession.send(
            Frame.Text(
                Json.encodeToString(
                    _roomSettingsEntity.toDomain(
                        userConnection.userEntity,
                        _userConnections.map {
                            it.userEntity.toDomain(
                                UserConversionCommand.ForeignUser(userConnection.userEntity)
                            )
                        } as List<ForeignUser>
                    )
                )
            )
        )

        broadcast(BroadcastCommand.UserJoined(userConnection.userEntity))
        return true
    }

    suspend fun disconnectUser(userConnection: UserConnection) {
        broadcast(BroadcastCommand.UserLeft(userConnection.userEntity))
        synchronized(_userConnections)
        {
            _roomSettingsEntity.connectedUsers--
            userConnection.webSocketSession.outgoing.close()
            _userConnections.remove(userConnection)
        }
    }


    suspend fun changeRoomSettings(roomSettingsValue: RoomSettingsValue) {
        _roomSettingsEntity.roomName = roomSettingsValue.roomName
        _roomSettingsEntity.currentQuiz = _quizRepository.getQuizData(roomSettingsValue.quizId)!!
        _roomSettingsEntity.questionTimeInMillis = roomSettingsValue.questionTimeInMillis

        broadcast(BroadcastCommand.SettingsChanged(_roomSettingsEntity))
    }

    fun closeRoom() {
        _userConnections.forEach { (_, connection) ->
            connection.outgoing.close()
        }
        _roomSettingsEntity.isClosing = true
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

    suspend fun answerQuestion(answer: IncomingMessage.Answer, userConnection: UserConnection) {
        if (_currentQuestionId == null)
            userConnection.webSocketSession.outgoing.send(
                encodedFrame(
                    NoQuestion("No question to answer")
                )

            )
        else {
            _userAnswerList[_currentQuestionId]?.add(
                UserAnswer(transaction { userConnection.userEntity.id.value }, answer.answerId)
            )
            broadcast(
                BroadcastCommand.UserAnswered(
                    userConnection.userEntity,
                    answer.answerId
                )
            )
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

                            IncomingMessage.Disconnect -> disconnectUser(userConnection)

                            is IncomingMessage.Answer -> answerQuestion(receivedMessage, userConnection)

                            is IncomingMessage.ChangeSettings -> {
                                if (_ownerId == userId)
                                    changeRoomSettings(receivedMessage.roomSettingsValue)
                                else userConnection.webSocketSession.outgoing.send(
                                    encodedFrame(
                                        Forbidden("You are not owning this room")
                                    )
                                )
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
                            OutgoingMessage.UserAnswered(
                                transaction { broadcastCommand.userEntity.id.value },
                                if (isUserOwner(userEntity)) broadcastCommand.answerId else null
                            )

                        is BroadcastCommand.GameEnded -> OutgoingMessage.GameEnded(broadcastCommand.userAnswerMap)
                    }
                )
            )
        }
    }
}