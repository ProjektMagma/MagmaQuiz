package com.github.projektmagma.magmaquiz.server.data.entities

interface DomainCapable<T> {
    fun toDomain(): T
}