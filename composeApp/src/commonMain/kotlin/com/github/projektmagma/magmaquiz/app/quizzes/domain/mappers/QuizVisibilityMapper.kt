package com.github.projektmagma.magmaquiz.app.quizzes.domain.mappers

import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.friend_only
import magmaquiz.composeapp.generated.resources.private
import magmaquiz.composeapp.generated.resources.public
import org.jetbrains.compose.resources.StringResource

fun QuizVisibility.toResId(): StringResource{
    return when (this) {
        QuizVisibility.Private -> Res.string.private
        QuizVisibility.FriendsOnly -> Res.string.friend_only
        QuizVisibility.Public -> Res.string.public
    }
}