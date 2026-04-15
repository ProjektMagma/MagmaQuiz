package com.github.projektmagma.magmaquiz.server.data.rooms

import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.QuizEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.RoomSettings
import java.util.*

data class RoomSettingsEntity(
    val roomId: UUID = UUID.randomUUID(),
    var roomName: String,
    var currentQuiz: QuizEntity,
    val roomOwner: UserEntity,
    var questionTimeInMillis: Long,
    var isPublic: Boolean = false,
    var isInProgress: Boolean = false,
    var isClosing: Boolean = false,
    var connectedUsers: Int = 0
) {
    fun toDomain(caller: UserEntity, userList: List<ForeignUser> = emptyList()): RoomSettings {
        return RoomSettings(
            roomId = this.roomId,
            roomName = this.roomName,
            currentQuiz = this.currentQuiz.toDomain(QuizConversionCommand.WithUserNoQuestions(caller)),
            roomOwner = this.roomOwner.toDomain(UserConversionCommand.ForeignUser(caller)) as ForeignUser,
            questionTimeInMillis = this.questionTimeInMillis,
            isPublic = this.isPublic,
            isInProgress = this.isInProgress,
            connectedUsers = connectedUsers,
            userList = userList,
        )
    }
}
