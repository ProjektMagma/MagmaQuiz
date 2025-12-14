package com.github.projektmagma.magmaquiz.server.data.abstraction

interface DomainCapable<T> {
    fun toDomain(): T
}