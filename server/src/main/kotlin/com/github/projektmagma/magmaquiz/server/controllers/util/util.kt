package com.github.projektmagma.magmaquiz.server.controllers.util

import com.github.projektmagma.magmaquiz.server.data.entities.FavoriteQuizzesEntity
import com.github.projektmagma.magmaquiz.server.data.entities.FriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.entities.QuizEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.FriendshipsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun isUserActive(userId: UUID): Boolean {
    val dbUser = transaction {
        UserEntity.findById(userId)
    }

    return !(dbUser == null || transaction { !dbUser.isActive })
}

// Zwraca quiz, jeśli:
// Quiz należy do ciebie,
// Quiz jest publiczny,
// Quiz jest aktywny (nie jest usunięty)
fun quizEntityOrNull(quizId: UUID, userId: UUID): QuizEntity? {
    val dbUser = transaction {
        UserEntity.findById(userId)
    }!!

    val dbQuiz = transaction {
        QuizEntity.findById(quizId)
    }

    if (dbQuiz == null ||
        transaction {
            !dbQuiz.isActive || !dbQuiz.isPublic && dbQuiz.quizCreator.id != dbUser.id
        }
    ) return null

    return dbQuiz
}


// Zwraca znajomość, jeśli:
// Drugi użytkownik jest aktywny (nie jest usunięty)
// Znajomość nie jest usunięta,
// Znajomość jest z "lewej" lub "prawej" połączona z tym użytkownikiem
fun UserEntity.friendshipEntityOrNull(otherUserId: UUID): FriendshipEntity? {
    val thisUser = this

    if (!isUserActive(otherUserId)) return null

    val dbUser = transaction {
        UserEntity.findById(otherUserId)
    }!!

    val friendship = transaction {
        FriendshipEntity.find {
            (FriendshipsTable.userFrom eq thisUser.id and (FriendshipsTable.userTo eq dbUser.id)) or
                    (FriendshipsTable.userFrom eq dbUser.id and (FriendshipsTable.userTo eq thisUser.id))
        }.firstOrNull()
    }

    return friendship
}


fun UserEntity.userFriendList(): List<UserEntity> {
    val thisUser = this

    val friendshipList = transaction {
        FriendshipEntity.find {
            FriendshipsTable.userFrom eq thisUser.id or (FriendshipsTable.userTo eq thisUser.id) and FriendshipsTable.isActive
        }
    }

    return transaction {
        friendshipList
            .map { friendship ->
                if (friendship.userTo == thisUser) friendship.userFrom
                else friendship.userTo
            }
    }
}

fun UserEntity.favoritesQuizzes(): List<QuizEntity> {
    val thisUser = this

    val favorites =
        transaction {
            FavoriteQuizzesEntity.find {
                FavoriteQuizzesTable.user eq thisUser.id and
                        (FavoriteQuizzesTable.isActive)
            }
                .map { it.quiz.id }
        }
    return transaction {
        QuizEntity.find {
            QuizzesTable.isActive eq true and
                    (QuizzesTable.isPublic eq true or (QuizzesTable.quizCreator eq thisUser.id)) and
                    (QuizzesTable.id inList (favorites))
        }.toList()
    }
}