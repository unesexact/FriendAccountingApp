package com.unesexact.friendaccountingapp.data.repository

import com.unesexact.friendaccountingapp.data.local.dao.TransactionDao
import com.unesexact.friendaccountingapp.data.local.entity.TransactionEntity

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
}