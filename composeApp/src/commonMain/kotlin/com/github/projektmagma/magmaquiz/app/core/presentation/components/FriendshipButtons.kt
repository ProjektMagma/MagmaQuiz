package com.github.projektmagma.magmaquiz.app.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.accept
import magmaquiz.composeapp.generated.resources.cancel
import magmaquiz.composeapp.generated.resources.delete_friend
import magmaquiz.composeapp.generated.resources.reject
import magmaquiz.composeapp.generated.resources.send_invite
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendshipButtons(
    user: ForeignUser,
    usersSharedViewModel: UsersSharedViewModel
){
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WideTonalButton(
                    text = Res.string.accept,
                    action = {
                        usersSharedViewModel.onCommand(
                            UsersSharedCommand.AcceptFriendInvite(user.userId!!)
                        )
                    }
                )
                WideTonalButton(
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
    }
}

@Composable
fun WideTonalButton(
    text: StringResource,
    action: () -> Unit,
){
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = { action() }
    ) {
        Text(stringResource(text))
    }
}
