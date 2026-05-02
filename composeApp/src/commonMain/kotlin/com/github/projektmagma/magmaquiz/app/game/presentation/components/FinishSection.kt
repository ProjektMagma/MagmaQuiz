package com.github.projektmagma.magmaquiz.app.game.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameState
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.end_game
import magmaquiz.composeapp.generated.resources.end_of_game
import magmaquiz.composeapp.generated.resources.leave_room
import magmaquiz.composeapp.generated.resources.percent_correct
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun FinishSection(
    gameState: GameState,
    correctQuestions: Map<ForeignUser, Int>,
    isHost: Boolean,
    onEnd: () -> Unit,
    onDisconnect: () -> Unit
) {
    val sorted = correctQuestions.entries
        .sortedByDescending { it.value }
        .toList()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ScoreCard(
            score = gameState.score,
            total = gameState.totalQuestions
        )
        
        if (sorted.size >= 3) {
            PodiumCard(entries = sorted.take(3))
        }
        
        PlayerListCard(
            entries = sorted,
            total = gameState.totalQuestions
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = if (isHost) onEnd else onDisconnect,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = if (isHost)
                    stringResource(Res.string.end_game)
                else
                    stringResource(Res.string.leave_room)
            )
        }
    }
}

@Composable
private fun ScoreCard(score: Int, total: Int) {
    val pct = if (total > 0) ((score.toDouble() / total) * 100).roundToInt() else 0

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "/ $total",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Text(
                text = stringResource(Res.string.end_of_game),
                style = MaterialTheme.typography.titleMedium
            )
            Surface(
                shape = RoundedCornerShape(99.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = stringResource(Res.string.percent_correct, pct),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun PodiumCard(entries: List<Map.Entry<ForeignUser, Int>>) {
    val ordered = listOf(entries[1], entries[0], entries[2])
    val heights = listOf(52.dp, 72.dp, 36.dp)
    val blockColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val avatarColors = listOf(
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    )
    val ranks = listOf(2, 1, 3)
    val totalQuestions = entries.size

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom
        ) {
            ordered.forEachIndexed { i, entry ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val avatarSize = if (ranks[i] == 1) 42.dp else 32.dp
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(avatarColors[i].first)
                            .then(
                                if (ranks[i] == 1)
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.key.userName.take(2).uppercase(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = avatarColors[i].second
                        )
                    }
                    Text(
                        text = entry.key.userName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 80.dp)
                    )
                    Text(
                        text = "${entry.value} / $totalQuestions",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(heights[i])
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(blockColors[i]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ranks[i]}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = avatarColors[i].second
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerListCard(
    entries: List<Map.Entry<ForeignUser, Int>>,
    total: Int
) {
    val avatarPairs = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    )
    val barColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary
    )

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            entries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center
                    )
                    ContentImage(
                        imageData = entry.key.userProfilePicture,
                        imageSize = 32.dp
                    )
                    Text(
                        text = entry.key.userName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    val fraction = if (total > 0) entry.value.toFloat() / total else 0f
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .clip(RoundedCornerShape(99.dp))
                                .background(barColors[index % barColors.size])
                        )
                    }
                    Text(
                        text = "${entry.value} / $total",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.widthIn(min = 40.dp),
                        textAlign = TextAlign.End
                    )
                }
                if (index < entries.size - 1) {
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }
}