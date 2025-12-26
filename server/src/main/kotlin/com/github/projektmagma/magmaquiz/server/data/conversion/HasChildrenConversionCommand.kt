package com.github.projektmagma.magmaquiz.server.data.conversion

sealed interface HasChildrenConversionCommand : ConversionCommand {
    data object NoChildren : HasChildrenConversionCommand
    data object WithChildren : HasChildrenConversionCommand
}