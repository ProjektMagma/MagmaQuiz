package com.github.projektmagma.magmaquiz.shared.data.domain

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatus {
    None,
    Incoming,
    Outgoing,
    Friends
}