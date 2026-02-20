package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.createDataStore
import com.github.projektmagma.magmaquiz.app.core.data.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore() }
    single { getDatabaseBuilder() }
    singleOf(::ApiDataStore)
}