package com.unesexact.friendaccountingapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unesexact.friendaccountingapp.data.repository.FriendRepository

class FriendViewModelFactory(
    private val repository: FriendRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            return FriendViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}