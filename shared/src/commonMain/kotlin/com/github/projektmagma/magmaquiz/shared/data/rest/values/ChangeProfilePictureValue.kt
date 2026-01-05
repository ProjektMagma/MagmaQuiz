package com.github.projektmagma.magmaquiz.shared.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ChangeProfilePictureValue(
    val profilePictureBig: ByteArray? = null,
    val profilePictureSmall: ByteArray? = null
)
