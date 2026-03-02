package com.unesexact.friendaccountingapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unesexact.friendaccountingapp.data.local.dao.FriendDao
import com.unesexact.friendaccountingapp.data.local.dao.TransactionDao
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.local.entity.TransactionEntity

@Database(
    entities = [FriendEntity::class, TransactionEntity::class], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun friendDao(): FriendDao
    abstract fun transactionDao(): TransactionDao
}