package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun StarRating(
    rating: Float,
    starSize: Dp = 30.dp,
    activeColor: Color = Color(0xFFFFB400),
    inactiveColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val halfRating = (rating * 2).roundToInt() / 2f
        
        for (index in 0 until 5) {
            val starStart = index.toFloat()
            val fillFraction = (halfRating - starStart).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .size(starSize)
            ) {
                Icon(
                    imageVector = Icons.Rounded.StarBorder,
                    contentDescription = null,
                    tint = inactiveColor,
                    modifier = Modifier.size(starSize)
                )

                if (fillFraction > 0f) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier
                            .size(starSize)
                            .drawWithContent {
                                clipRect(right = size.width * fillFraction) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                    )
                }
            }
        }

        val ratingText = if (halfRating % 1f == 0f) {
            "${halfRating.toInt()}"
        } else {
            "$halfRating"
        }
        Text(ratingText)
    }
}
