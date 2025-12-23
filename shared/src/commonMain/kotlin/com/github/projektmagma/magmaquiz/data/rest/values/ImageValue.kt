package com.github.projektmagma.magmaquiz.data.rest.values

import kotlinx.serialization.Serializable

@Serializable
data class ImageValue(
    val image: ByteArray? = null
)
