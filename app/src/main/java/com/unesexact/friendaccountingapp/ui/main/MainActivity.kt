package com.unesexact.friendaccountingapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.unesexact.friendaccountingapp.R
import com.unesexact.friendaccountingapp.data.local.database.DatabaseProvider
import com.unesexact.friendaccountingapp.data.repository.FriendRepository

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: FriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup ViewModel with Repository
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = FriendRepository(db.friendDao())
        val factory = FriendViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        // Observe data from ViewModel
        lifecycleScope.launch {
            viewModel.friends.collect { friends ->
                friends.forEach {
                    Log.d("VM_TEST", "Friend: ${it.name}, Balance: ${it.balance}")
                }
            }
        }

        // Insert test data through ViewModel (NOT DAO)
        viewModel.addFriend("John", 100.0)
    }
}