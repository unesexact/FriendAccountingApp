package com.unesexact.friendaccountingapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unesexact.friendaccountingapp.data.local.dao.FriendDao
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity

@Database(
    entities = [FriendEntity::class], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun friendDao(): FriendDao
}