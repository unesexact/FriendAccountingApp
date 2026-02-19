package com.unesexact.friendaccountingapp.data.repository

import com.unesexact.friendaccountingapp.data.local.dao.FriendDao
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

class FriendRepository(
    private val friendDao: FriendDao
) {

    fun getAllFriends(): Flow<List<FriendEntity>> {
        return friendDao.getAllFriends()
    }

    suspend fun addFriend(friend: FriendEntity) {
        friendDao.insertFriend(friend)
    }

    suspend fun updateFriend(friend: FriendEntity) {
        friendDao.updateFriend(friend)
    }

    suspend fun deleteFriend(friend: FriendEntity) {
        friendDao.deleteFriend(friend)
    }
}