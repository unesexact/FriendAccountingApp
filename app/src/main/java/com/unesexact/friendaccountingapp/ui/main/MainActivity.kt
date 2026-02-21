package com.unesexact.friendaccountingapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.unesexact.friendaccountingapp.data.local.database.DatabaseProvider
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import com.unesexact.friendaccountingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FriendViewModel
    private val adapter = FriendAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel with Repository
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = FriendRepository(db.friendDao())
        val factory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        // Setup RecyclerView
        binding.recyclerViewFriends.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFriends.adapter = adapter

        // Observe friends list
        lifecycleScope.launch {
            viewModel.friends.collect { friends ->
                adapter.submitList(friends)
            }
        }
    }
}