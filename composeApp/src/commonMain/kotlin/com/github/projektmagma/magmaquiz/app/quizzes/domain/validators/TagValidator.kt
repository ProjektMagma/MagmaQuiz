package com.github.projektmagma.magmaquiz.app.quizzes.domain.validators

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.duplicated_tags
import magmaquiz.composeapp.generated.resources.too_many_tag
import org.jetbrains.compose.resources.StringResource

fun validateTag(tagName: String, tagList: List<String>) : TagError?{
    if (tagList.size >= 20) return TagError.TOO_MANY
    if (tagName in tagList) return TagError.DUPLICATE
    return null
}

enum class TagError : Error {
    TOO_MANY,
    DUPLICATE
}

fun TagError.toResId(): StringResource{
    return when(this){
        TagError.TOO_MANY -> Res.string.too_many_tag
        TagError.DUPLICATE -> Res.string.duplicated_tags
    }
}