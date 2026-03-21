package com.github.projektmagma.magmaquiz.app.settings.domain

import java.util.Locale

fun localizedCountryName(
    code: String,
    fallbackName: String,
): String{
    val localized = Locale.Builder()
        .setRegion(code)
        .build()
        .displayCountry
    return if (localized.isNullOrBlank()) fallbackName else localized
} 
