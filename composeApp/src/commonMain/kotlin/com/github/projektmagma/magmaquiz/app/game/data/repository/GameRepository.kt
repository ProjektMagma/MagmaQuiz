package com.github.projektmagma.magmaquiz.app.game.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.game.data.service.GameService
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.map
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class GameRepository(
    private val gameService: GameService
) {
    val roomSettings = MutableStateFlow<RoomSettings?>(null)
    val questions = MutableStateFlow<List<Question>>(emptyList())

    suspend fun createGameRoom(
        roomName: String, 
        id: UUID, 
        isPublic: Boolean, 
        questionTimeMillis: Long
    ): Resource<RoomSettings, NetworkError>{
        return gameService.createGameRoom(roomName, id, isPublic, questionTimeMillis)
    }

    suspend fun getRoomList(roomName: String = "", count: Int = 5, offset: Int): Resource<List<RoomSettings>, NetworkError> {
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
                 roomSettings.value = it.data
             }
             .whenError {
                 Resource.Error(it.error)
             }.map{}
         }
    
    
    fun getMessages() = gameService.getMessages()
    
    suspend fun sendMessage(message: WebSocketMessages.IncomingMessage) {
        gameService.sendMessage(message)
    }
}