package com.unesexact.friendaccountingapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.unesexact.friendaccountingapp.R
import com.unesexact.friendaccountingapp.data.local.database.DatabaseProvider
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔥 ROOM TEST
        lifecycleScope.launch {

            val db = DatabaseProvider.getDatabase(applicationContext)
            val dao = db.friendDao()

            // Insert test data
            dao.insertFriend(FriendEntity(name = "John", balance = 100.0))

            // Observe database
            dao.getAllFriends().collectLatest { friends ->
                friends.forEach {
                    Log.d("ROOM_TEST", "Friend: ${it.name}, Balance: ${it.balance}")
                }
            }
        }
    }
}
