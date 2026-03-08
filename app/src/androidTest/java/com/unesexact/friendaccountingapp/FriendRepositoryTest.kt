package com.unesexact.friendaccountingapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.unesexact.friendaccountingapp.data.local.database.AppDatabase
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendRepositoryTest {

    /** Executes Architecture Components tasks synchronously */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var repository: FriendRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries()   // OK for tests only
            .build()

        repository = FriendRepository(database.friendDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFriend_insertsCorrectly() = runTest {
        val friend = FriendEntity(
            id = 0, name = "Ali", balance = 100.0
        )

        repository.addFriend(friend)

        val friends = repository.getAllFriends().first()

        assertThat(friends).hasSize(1)
        assertThat(friends[0].name).isEqualTo("Ali")
        assertThat(friends[0].balance).isEqualTo(100.0)
    }

    @Test
    fun deleteFriend_removesFromDatabase() = runTest {
        val friend = FriendEntity(
            id = 0, name = "Ali", balance = 50.0
        )

        repository.addFriend(friend)

        val insertedFriend = repository.getAllFriends().first().first()

        repository.deleteFriend(insertedFriend)

        val friends = repository.getAllFriends().first()
        assertThat(friends).isEmpty()
    }

    @Test
    fun updateFriendBalance_updatesCorrectly() = runBlocking {

        val friend = FriendEntity(
            id = 0, name = "Ali", balance = 100.0
        )

        repository.addFriend(friend)

        val insertedFriend = repository.getAllFriends().first().first()

        repository.updateFriend(
            insertedFriend.copy(balance = 150.0)
        )

        val updatedFriend = repository.getAllFriends().first().first()

        assertThat(updatedFriend.balance).isEqualTo(150.0)
    }
}