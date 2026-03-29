package com.github.projektmagma.magmaquiz.app.users.presentation.model.details

import java.util.UUID

interface UserDetailsCommand {
    data class SelectedTabIndexChanged(val newIndex: Int): UserDetailsCommand
    data object LoadNextItems: UserDetailsCommand
    data object LoadQuizzesOrHistory: UserDetailsCommand
    data object LoadData: UserDetailsCommand
    data object GetUserData: UserDetailsCommand
    data class ChangeFavoriteStatus(val id: UUID): UserDetailsCommand
    data class DeleteQuiz(val id: UUID): UserDetailsCommand
}