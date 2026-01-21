package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.auth.di.authModule
import com.github.projektmagma.magmaquiz.app.home.di.homeModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)

        Napier.base(DebugAntilog())

        modules(
            sharedModule,
            platformModule,
            authModule,
            homeModule
        )
    }
}