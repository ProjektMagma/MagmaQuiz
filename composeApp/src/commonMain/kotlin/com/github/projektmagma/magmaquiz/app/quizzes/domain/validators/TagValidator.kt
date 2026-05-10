package com.github.projektmagma.magmaquiz.app.quizzes.domain.validators

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.duplicated_tags
import org.jetbrains.compose.resources.StringResource

object TagValidator {
    const val TAGLIMIT = 20
    
    fun validateTag(tagName: String, tagList: List<String>) : TagError?{
        if (tagName in tagList) return TagError.DUPLICATE
        return null
    }



    fun TagError.toResId(): StringResource{
        return when(this){
            TagError.DUPLICATE -> Res.string.duplicated_tags
        }
    }
}

enum class TagError : Error {
    DUPLICATE
}