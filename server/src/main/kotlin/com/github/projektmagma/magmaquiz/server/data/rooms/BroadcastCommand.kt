package com.github.projektmagma.magmaquiz.server.data.rooms

import com.github.projektmagma.magmaquiz.server.data.entities.QuizQuestionEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.shared.data.domain.UserAnswer
import java.util.*

sealed interface BroadcastCommand {
    data class UserJoined(val userEntity: UserEntity) : BroadcastCommand

    data class UserLeft(val userEntity: UserEntity) : BroadcastCommand

    data class SettingsChanged(val roomSettingsEntity: RoomSettingsEntity) : BroadcastCommand

    data class NextQuestion(val quizQuestionEntity: QuizQuestionEntity) : BroadcastCommand

    data class UserAnswered(val userEntity: UserEntity) : BroadcastCommand

    data class GameEnded(val userAnswerMap: Map<UUID, List<UserAnswer>>) : BroadcastCommand
}