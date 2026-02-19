package com.unesexact.friendaccountingapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendViewModel(
    private val repository: FriendRepository
) : ViewModel() {

    val friends = repository.getAllFriends().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFriend(name: String, balance: Double) {
        viewModelScope.launch {
            repository.addFriend(FriendEntity(name = name, balance = balance))
        }
    }

    fun deleteFriend(friend: FriendEntity) {
        viewModelScope.launch {
            repository.deleteFriend(friend)
        }
    }

    fun updateFriend(friend: FriendEntity) {
        viewModelScope.launch {
            repository.updateFriend(friend)
        }
    }
}