package com.github.projektmagma.magmaquiz.server.data.conversion

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity


sealed interface UserConversionCommand : ConversionCommand {
    data object ThisUser : UserConversionCommand
    data class ForeignUser(val caller: UserEntity? = null) : UserConversionCommand
    data class ForeignUserWithData(val caller: UserEntity) : UserConversionCommand

}