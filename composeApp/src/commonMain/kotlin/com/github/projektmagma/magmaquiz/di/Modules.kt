package com.github.projektmagma.magmaquiz.di

import com.github.projektmagma.magmaquiz.data.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module { 
    singleOf(::UserRepository)
}