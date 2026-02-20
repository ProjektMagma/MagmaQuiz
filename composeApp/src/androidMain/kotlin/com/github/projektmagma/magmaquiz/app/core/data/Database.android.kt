package com.github.projektmagma.magmaquiz.app.core.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ServerConfigDatabase>{
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("config.db")
    return Room.databaseBuilder<ServerConfigDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}