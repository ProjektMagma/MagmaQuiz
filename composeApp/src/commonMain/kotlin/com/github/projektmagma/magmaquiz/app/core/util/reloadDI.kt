package com.github.projektmagma.magmaquiz.app.core.util

import com.github.projektmagma.magmaquiz.app.core.di.appModule
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

fun reloadDI(){
    unloadKoinModules(appModule)
    loadKoinModules(appModule)
}