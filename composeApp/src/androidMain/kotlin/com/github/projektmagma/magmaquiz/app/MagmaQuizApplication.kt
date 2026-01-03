package com.github.projektmagma.magmaquiz.app

import android.app.Application
import com.github.projektmagma.magmaquiz.app.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class MagmaQuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MagmaQuizApplication)
        }
    }
}