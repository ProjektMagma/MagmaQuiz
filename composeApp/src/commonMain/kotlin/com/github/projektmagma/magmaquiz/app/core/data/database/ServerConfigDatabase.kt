package com.github.projektmagma.magmaquiz.app.core.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.projektmagma.magmaquiz.app.core.data.CountriesData
import com.github.projektmagma.magmaquiz.app.core.domain.Protocols
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ServerConfigEntity::class, CountryEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class ServerConfigDatabase : RoomDatabase() {
    abstract fun getServerConfigDao() : ServerConfigDao
    abstract fun getCountryDao() : CountryDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<ServerConfigDatabase> {
    override fun initialize(): ServerConfigDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ServerConfigDatabase>
): ServerConfigDatabase {
    lateinit var database: ServerConfigDatabase
    
    database = builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)
                CoroutineScope(Dispatchers.IO).launch { 
                    getServerConfigDao(database).insertOrUpdate(
                        ServerConfigEntity(
                            name = "Default",
                            ip = "api.projektmagma.pl",
                            protocol = Protocols.HTTPS,
                            isSelected = true
                        )
                    )
                    getCountryDao(database).insertAll(CountriesData.all)
                }
            }
        })
        .build()
    return database
}

fun getServerConfigDao(serverConfigDatabase: ServerConfigDatabase): ServerConfigDao{
    return serverConfigDatabase.getServerConfigDao()
}

fun getCountryDao(serverConfigDatabase: ServerConfigDatabase): CountryDao{
    return serverConfigDatabase.getCountryDao()
}