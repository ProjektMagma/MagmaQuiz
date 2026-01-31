package com.github.projektmagma.magmaquiz.app.home.di

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

val slideInAndOutHorizontallyAnimation = NavDisplay.transitionSpec {
    slideInHorizontally(initialOffsetX = { it }) togetherWith
            slideOutHorizontally(targetOffsetX = { -it })
} + NavDisplay.popTransitionSpec {
    slideInHorizontally(initialOffsetX = { -it }) togetherWith
            slideOutHorizontally(targetOffsetX = { it })
} + NavDisplay.predictivePopTransitionSpec {
    slideInHorizontally(initialOffsetX = { -it }) togetherWith
            slideOutHorizontally(targetOffsetX = { it })
}
    