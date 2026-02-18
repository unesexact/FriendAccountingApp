package com.unesexact.friendaccountingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,

    val balance: Double
)