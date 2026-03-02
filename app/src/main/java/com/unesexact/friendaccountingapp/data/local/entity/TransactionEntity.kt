package com.unesexact.friendaccountingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions", foreignKeys = [ForeignKey(
        entity = FriendEntity::class,
        parentColumns = ["id"],
        childColumns = ["friendId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("friendId")]
)
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val friendId: Long,

    val amount: Double,

    val type: TransactionType,

    val timestamp: Long = System.currentTimeMillis()
)