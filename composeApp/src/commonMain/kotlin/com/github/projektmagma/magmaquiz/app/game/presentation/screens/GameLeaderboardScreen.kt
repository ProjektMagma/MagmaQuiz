package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.game.presentation.GameLeaderboardViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.toQuestionModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.*


@Composable
fun GameLeaderboardScreen(
    viewModel: GameLeaderboardViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val scores by viewModel.scores.collectAsStateWithLifecycle()
    val room by viewModel.room.collectAsStateWithLifecycle()
    val isGameEnded by viewModel.isGameEnded.collectAsStateWithLifecycle()
    val currentQuestion by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val questionList by viewModel.questionList.collectAsStateWithLifecycle()


    val sortedEntries = remember(scores) {
        scores.entries
            .sortedByDescending { it.value.count() }
            .filter { it.key.userId != room?.roomOwner?.userId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = room!!.roomName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = room!!.currentQuiz.quizName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (isGameEnded)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (isGameEnded) stringResource(Res.string.game_ended)
                        else stringResource(Res.string.online, room!!.connectedUsers - 1),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = if (isGameEnded)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }


        if (currentQuestion != null)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.current_question),
                    style = MaterialTheme.typography.titleMedium
                )
                QuestionCard(currentQuestion!!.toQuestionModel(), lockClickable = true)
            }



        if (sortedEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.waiting_for_players),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            if (isGameEnded && sortedEntries.size >= 3) {
                PodiumCard(entries = sortedEntries.take(3))
            }

            PlayerListCard(
                entries = sortedEntries,
                total = room!!.currentQuiz.questionList.size
            )

            if (isGameEnded) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(questionList) { question ->
                        QuestionCard(
                            question.toQuestionModel(),
                            userAnswers = sortedEntries,
                            lockClickable = true
                        )
                    }
                }

                OutlinedButton(
                    onClick = navigateBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = stringResource(Res.string.leave_room))
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(entries: List<Map.Entry<ForeignUser, List<UUID?>>>) {
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
fun PlayerListCard(
    entries: List<Map.Entry<ForeignUser, List<UUID?>>>,
    total: Int
) {
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
                    ProfilePictureIcon(
                        imageData = entry.key.userProfilePicture,
                        size = 32.dp
                    )
                    Text(
                        text = entry.key.userName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    val fraction = if (total > 0) entry.value.count().toFloat() / total else 0f
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
                        text = "${entry.value.count()} / $total",
                        fontSize = 16.sp,
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