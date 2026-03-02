package com.unesexact.friendaccountingapp.data.repository

import com.unesexact.friendaccountingapp.data.local.dao.TransactionDao
import com.unesexact.friendaccountingapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    fun getTransactionsForFriend(friendId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsForFriend(friendId)
    }
}