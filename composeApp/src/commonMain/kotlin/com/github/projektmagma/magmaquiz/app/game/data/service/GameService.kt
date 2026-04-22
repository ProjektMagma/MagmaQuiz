package com.github.projektmagma.magmaquiz.app.game.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.game.data.WsEvent
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.RoomSettingsValue
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.closed_room
import org.jetbrains.compose.resources.getString
import java.util.UUID

class GameService(
    private val httpClient: HttpClient
) {
    var session: WebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _events = MutableSharedFlow<WsEvent>(
        replay = 0,
        extraBufferCapacity = 64
    )

    fun getMessages() = _events.asSharedFlow()
    
    suspend fun createGameRoom(name: String, id: UUID, isPublic: Boolean, questionTime: Long): Resource<RoomSettings, NetworkError> {
        return safeCall<RoomSettings> {
            httpClient.post("multiplayerRooms/create/") {
                contentType(ContentType.Application.Json)
                setBody(
                    RoomSettingsValue(
                        roomName = name,
                        quizId = id,
                        isPublic = isPublic,
                        questionTimeInMillis = questionTime
                    )
                )
            }
        }
    }

    suspend fun getRoomList(
        count: Int,
        offset: Int,
        name: String
    ): Resource<List<RoomSettings>, NetworkError> {
        return safeCall<List<RoomSettings>> {
            httpClient.get("multiplayerRooms/roomList/$count/$offset/$name")
        }
    }

    suspend fun joinRoom(id: UUID): Resource<RoomSettings, NetworkError> {
        return try {
            session?.close()
            
            val ws = httpClient.webSocketSession {
                url {
                    encodedPath = "/multiplayerRooms/join/$id"
                    protocol = URLProtocol.WSS
                }
            }

            when (val firstFrame = ws.incoming.receive()) {
                is Frame.Text -> {
                    val room = Json.decodeFromString<RoomSettings>(firstFrame.readText())
                    session = ws
                    startReader()
                    Resource.Success(room)
                }
    
                is Frame.Close -> {
                    ws.close()
                    Resource.Error(NetworkError.UNKNOWN)
                }

                else -> {
                    ws.close()
                    Resource.Error(NetworkError.UNKNOWN)
                }
            }
        } catch (_: Exception) {
            Resource.Error(NetworkError.NETWORK)
        }
    }
    
    private fun startReader() {
        scope.launch {
            try {
                for (frame in session!!.incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val raw = frame.readText()
                            _events.emit(WsEvent.OutGoing(Json.decodeFromString<WebSocketMessages.OutgoingMessage>(raw)))
                        }
                        is Frame.Close -> {
                            _events.emit(WsEvent.Closed(frame.readReason()?.message))
                        }
                        else -> Unit
                    }
                }
            } catch(e : Exception) {
                _events.emit(WsEvent.Closed(e.message))
            } finally {
                _events.emit(WsEvent.Closed(getString(Res.string.closed_room)))
            }
        }
    }
    
    suspend fun sendMessage(message: WebSocketMessages.IncomingMessage){
        session?.outgoing?.send(
            Frame.Text(Json.encodeToString<WebSocketMessages.IncomingMessage>(message))
        )
    }
}