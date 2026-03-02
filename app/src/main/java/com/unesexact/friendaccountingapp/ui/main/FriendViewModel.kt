package com.unesexact.friendaccountingapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.local.entity.TransactionEntity
import com.unesexact.friendaccountingapp.data.local.entity.TransactionType
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import com.unesexact.friendaccountingapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendViewModel(
    private val friendRepository: FriendRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val friends = friendRepository.getAllFriends().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addFriend(name: String, balance: Double) {
        viewModelScope.launch {
            friendRepository.addFriend(FriendEntity(name = name, balance = balance))
        }
    }

    fun deleteFriend(friend: FriendEntity) {
        viewModelScope.launch {
            friendRepository.deleteFriend(friend)
        }
    }

    fun updateFriend(friend: FriendEntity) {
        viewModelScope.launch {
            friendRepository.updateFriend(friend)
        }
    }

    fun addTransaction(
        friend: FriendEntity, amount: Double, type: TransactionType
    ) {
        viewModelScope.launch {

            transactionRepository.insertTransaction(
                TransactionEntity(
                    friendId = friend.id, amount = amount, type = type
                )
            )

            val newBalance = when (type) {
                TransactionType.CREDIT -> friend.balance + amount
                TransactionType.DEBIT -> friend.balance - amount
            }

            friendRepository.updateFriend(
                friend.copy(balance = newBalance)
            )
        }
    }
}