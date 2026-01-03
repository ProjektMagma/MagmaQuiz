package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigDataStore
import com.github.projektmagma.magmaquiz.app.core.data.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }
    singleOf(::ApiDataStore)
    singleOf(::ServerConfigDataStore)
}