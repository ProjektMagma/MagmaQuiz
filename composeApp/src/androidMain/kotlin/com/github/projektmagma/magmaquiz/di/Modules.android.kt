package com.github.projektmagma.magmaquiz.di

import com.github.projektmagma.magmaquiz.data.ApiDataStore
import com.github.projektmagma.magmaquiz.data.ServerConfigDataStore
import com.github.projektmagma.magmaquiz.data.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }
    single { ApiDataStore(get()) }
    single { ServerConfigDataStore(get()) }
}