package com.github.projektmagma.magmaquiz.server.data.conversion

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity


sealed interface UserConversionCommand : ConversionCommand {
    data object ThisUser : UserConversionCommand
    data class ForeignUserWithSmallPicture(val caller: UserEntity) : UserConversionCommand
    data class ForeignUserWithBigPicture(val caller: UserEntity) : UserConversionCommand

}