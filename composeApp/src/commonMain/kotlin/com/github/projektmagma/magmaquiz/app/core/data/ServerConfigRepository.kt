package com.github.projektmagma.magmaquiz.app.core.data

import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigDao
import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigEntity
import com.github.projektmagma.magmaquiz.app.core.data.database.toEntity
import com.github.projektmagma.magmaquiz.app.core.presentation.model.server.ServerConfigState

class ServerConfigRepository(
    private val serverConfigDao: ServerConfigDao
) { 
    suspend fun insert(state: ServerConfigState){
        serverConfigDao.deselectAll()
        serverConfigDao.insert(state.toEntity())
    }
    
    suspend fun update(state: ServerConfigState){
        serverConfigDao.deselectAll()
        serverConfigDao.update(state.toEntity().copy(id = state.id))
    }
    
    fun getAllConfigs() = serverConfigDao.getAllConfigsDesc()
    
    suspend fun getSelectedConfig(): ServerConfigEntity {
        return serverConfigDao.getSelectedConfig()
    }
    
    suspend fun deleteConfig(entity: ServerConfigEntity){
        serverConfigDao.delete(entity)
    }
}