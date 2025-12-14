package com.github.projektmagma.magmaquiz.server.data.abstraction

interface DomainWithChildrenCapable<T> : DomainCapable<T> {
    fun toDomainWithChildren(): T
}