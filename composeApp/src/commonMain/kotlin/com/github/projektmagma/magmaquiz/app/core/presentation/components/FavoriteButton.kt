package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.favoritePink

@Composable
fun FavoriteButton(
    likesCount: Int,
    isLiked: Boolean,
    changeFavoriteStatus: () -> Unit
) {

    var isLiked by remember { mutableStateOf(isLiked) }
    var likesCount by remember { mutableIntStateOf(likesCount) }

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "$likesCount",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                changeFavoriteStatus()
                isLiked = !isLiked
                if (isLiked) likesCount++
                else likesCount--
            }
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                tint = favoritePink,
                contentDescription = "FavoriteButton"
            )

        }
    }
}