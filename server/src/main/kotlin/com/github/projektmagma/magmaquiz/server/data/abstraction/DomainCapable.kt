package com.github.projektmagma.magmaquiz.server.data.abstraction

import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand


interface DomainCapable<T, U : ConversionCommand> {
    fun toDomain(command: U): T
}

