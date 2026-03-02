package com.unesexact.friendaccountingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.unesexact.friendaccountingapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE friendId = :friendId ORDER BY timestamp DESC")
    fun getTransactionsForFriend(friendId: Long): Flow<List<TransactionEntity>>
}