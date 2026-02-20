package com.github.projektmagma.magmaquiz.app.core.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<ServerConfigDatabase>{
    val dbFile = File(System.getProperty("java.io.tmpdir"), "config.db")
    return Room.databaseBuilder<ServerConfigDatabase>(
        name = dbFile.absolutePath
    )
}