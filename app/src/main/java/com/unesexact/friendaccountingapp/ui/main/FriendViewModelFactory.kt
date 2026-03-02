package com.unesexact.friendaccountingapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import com.unesexact.friendaccountingapp.data.repository.TransactionRepository

class FriendViewModelFactory(
    private val friendRepository: FriendRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return FriendViewModel(
                friendRepository, transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}