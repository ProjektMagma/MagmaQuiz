package com.github.projektmagma.magmaquiz.server.data.conversion


sealed interface UserConversionCommand : ConversionCommand {
    data object ThisUser : UserConversionCommand
    data object ForeignUserWithSmallPicture : UserConversionCommand
    data object ForeignUserWithBigPicture : UserConversionCommand

}