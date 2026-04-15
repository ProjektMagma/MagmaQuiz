package com.github.projektmagma.magmaquiz.shared.data.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.serializers.UUIDSerializer
import com.github.projektmagma.magmaquiz.shared.data.rest.values.RoomSettingsValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed interface WebSocketMessages {

    @Serializable
    sealed interface IncomingMessage {


        @Serializable
        @SerialName("Answer")
        data class Answer(@Serializable(UUIDSerializer::class) val answerId: UUID) : IncomingMessage

        @Serializable
        @SerialName("Disconnect")
        data object Disconnect : IncomingMessage


        @Serializable
        @SerialName("StartGame")
        data object StartGame : IncomingMessage

        @Serializable
        @SerialName("CloseRoom")
        data object CloseRoom : IncomingMessage

        @Serializable
        @SerialName("ChangeSettings")
        data class ChangeSettings(val roomSettingsValue: RoomSettingsValue) : IncomingMessage
    }

    @Serializable
    sealed interface OutgoingMessage {
        @Serializable
        @SerialName("UserJoined")
        data class UserJoined(val user: ForeignUser) : OutgoingMessage

        @Serializable
        @SerialName("UserLeft")
        data class UserLeft(@Serializable(UUIDSerializer::class) val userId: UUID) : OutgoingMessage

        @Serializable
        @SerialName("SettingsChanged")
        data class SettingsChanged(val roomSettings: RoomSettings) : OutgoingMessage

        @Serializable
        @SerialName("NextQuestion")
        data class NextQuestion(val question: Question) : OutgoingMessage

        @Serializable
        @SerialName("UserAnswered")
        data class UserAnswered(
            @Serializable(UUIDSerializer::class) val userId: UUID,
            @Serializable(UUIDSerializer::class) val answerId: UUID? = null
        ) : OutgoingMessage

        @Serializable
        @SerialName("GameEnded")
        data class GameEnded(val userAnswerMap: Map<@Serializable(UUIDSerializer::class) UUID, List<UserAnswer>>) :
            OutgoingMessage

        @Serializable
        sealed interface Error {
            data class Forbidden(val message: String) : Error
            data class NoQuestion(val message: String) : Error
            data class Unknown(val message: String) : Error
        }
    }
}