package com.github.projektmagma.magmaquiz.app.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.model.shared.UsersSharedCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendshipButtons(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel,
    useColumn: Boolean = false
) {
    return when (user.friendshipStatus) {
        FriendshipStatus.None -> {
            WideTonalButton(
                text = Res.string.send_invite,
                action = {
                    usersSharedViewModel.onCommand(
                        UsersSharedCommand.SendFriendInvite(user.userId!!)
                    )
                }
            )
        }

        FriendshipStatus.Incoming -> {
            if (useColumn)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    WideTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = Res.string.accept,
                        action = {
                            usersSharedViewModel.onCommand(
                                UsersSharedCommand.AcceptFriendInvite(user.userId!!)
                            )
                        }
                    )
                    WideTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = Res.string.reject,
                        action = {
                            usersSharedViewModel.onCommand(
                                UsersSharedCommand.CancelFriendInvite(user.userId!!)
                            )
                        }
                    )
                }
            else
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WideTonalButton(
                        modifier = Modifier.weight(1f),
                        text = Res.string.accept,
                        action = {
                            usersSharedViewModel.onCommand(
                                UsersSharedCommand.AcceptFriendInvite(user.userId!!)
                            )
                        }
                    )
                    WideTonalButton(
                        modifier = Modifier.weight(1f),
                        text = Res.string.reject,
                        action = {
                            usersSharedViewModel.onCommand(
                                UsersSharedCommand.CancelFriendInvite(user.userId!!)
                            )
                        }
                    )
                }
        }

        FriendshipStatus.Outgoing -> {
            WideTonalButton(
                text = Res.string.cancel,
                action = {
                    usersSharedViewModel.onCommand(
                        UsersSharedCommand.CancelFriendInvite(user.userId!!)
                    )
                }
            )
        }

        FriendshipStatus.Friends -> {
            WideTonalButton(
                text = Res.string.delete_friend,
                action = {
                    usersSharedViewModel.onCommand(
                        UsersSharedCommand.DeleteFriend(user.userId!!)
                    )
                }
            )
        }

        FriendshipStatus.Unknown -> {}// todo nie wiem co tu powinno byc 
    }
}

@Composable
fun WideTonalButton(
    text: StringResource,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = { action() }
    ) {
        Text(stringResource(text))
    }
}
