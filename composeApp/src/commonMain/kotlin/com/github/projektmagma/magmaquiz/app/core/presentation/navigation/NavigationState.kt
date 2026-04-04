package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }

    fun resetAllBackStacks() {
        backStacks.forEach { (key, stack) -> 
            stack.clear()
            stack.add(key)
        }
        topLevelRoute = startRoute
    }
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializersConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class))
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(configuration = serializersConfig, key)
    }

    return remember(startRoute, topLevelRoute) {
        NavigationState(startRoute, topLevelRoute, backStacks)
    }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            entryDecorators = decorators,
            entryProvider = entryProvider,
            backStack = stack
        )
    }

    return stacksInUse.flatMap {
        decoratedEntries[it] ?: emptyList()
    }.toMutableStateList()
}

val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Route.Menus.Home::class, Route.Menus.Home.serializer())
            subclass(Route.Menus.Quizzes::class, Route.Menus.Quizzes.serializer())
            subclass(Route.Menus.Users::class, Route.Menus.Users.serializer())
            subclass(Route.Menus.Settings::class, Route.Menus.Settings.serializer())
            subclass(Route.Menus.Quizzes.QuizList::class, Route.Menus.Quizzes.QuizList.serializer())
            subclass(
                Route.Menus.Quizzes.CreateQuiz::class,
                Route.Menus.Quizzes.CreateQuiz.serializer()
            )
            subclass(
                Route.Menus.Quizzes.CreateQuestion::class,
                Route.Menus.Quizzes.CreateQuestion.serializer()
            )
            subclass(
                Route.Menus.Quizzes.QuizDetails::class,
                Route.Menus.Quizzes.QuizDetails.serializer()
            )
            subclass(
                Route.Menus.Users.UserDetails::class,
                Route.Menus.Users.UserDetails.serializer()
            )
            subclass(Route.Menus.Quizzes.QuizReviews::class, Route.Menus.Quizzes.QuizReviews.serializer())
            subclass(Route.Menus.Users.Find::class, Route.Menus.Users.Find.serializer())
            subclass(Route.Menus.Users.Friends::class, Route.Menus.Users.Friends.serializer())
            subclass(Route.Menus.Settings.DetailsChange::class, Route.Menus.Settings.DetailsChange.serializer())
            subclass(Route.Menus.Settings.LocationChange::class, Route.Menus.Settings.LocationChange.serializer())
            subclass(Route.Menus.Settings.EmailChange::class, Route.Menus.Settings.EmailChange.serializer())
            subclass(Route.Menus.Settings.EmailVerification::class, Route.Menus.Settings.EmailVerification.serializer())
            subclass(Route.Menus.Settings.PasswordChange::class, Route.Menus.Settings.PasswordChange.serializer())
            subclass(Route.Menus.Settings.PasswordVerification::class, Route.Menus.Settings.PasswordVerification.serializer())
            subclass(Route.Menus.Settings.PasswordEmailEntry::class, Route.Menus.Settings.PasswordEmailEntry.serializer())
            subclass(Route.Menus.Settings.Options::class, Route.Menus.Settings.Options.serializer())
        }
    }
}