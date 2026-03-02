package com.unesexact.friendaccountingapp.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unesexact.friendaccountingapp.data.local.database.DatabaseProvider
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.local.entity.TransactionType
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import com.unesexact.friendaccountingapp.data.repository.TransactionRepository

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = FriendRepository(db.friendDao())
        val transactionRepository = TransactionRepository(db.transactionDao())
        val factory = FriendViewModelFactory(repository, transactionRepository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        setContent {
            MaterialTheme {
                FriendsScreen(viewModel)
            }
        }
    }
}

@Composable
fun FriendsScreen(viewModel: FriendViewModel) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var friendToDelete by remember { mutableStateOf<FriendEntity?>(null) }
    var transactionFriend by remember { mutableStateOf<FriendEntity?>(null) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddFriendDialog = true }) {
                Text("+")
            }
        }) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(friends) { friend ->
                FriendItem(
                    friend = friend,
                    onClick = { transactionFriend = friend },
                    onLongClick = { friendToDelete = friend })
            }
        }

        // Add Friend Dialog
        if (showAddFriendDialog) {
            AddFriendDialog(onDismiss = { showAddFriendDialog = false }, onAdd = { name, balance ->
                viewModel.addFriend(name, balance)
                showAddFriendDialog = false
            })
        }

        if (friendToDelete != null) {
            AlertDialog(
                onDismissRequest = { friendToDelete = null },
                title = { Text("Delete Friend") },
                text = { Text("Delete ${friendToDelete!!.name}?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.deleteFriend(friendToDelete!!)
                        Toast.makeText(context, "Friend deleted", Toast.LENGTH_SHORT).show()
                        friendToDelete = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { friendToDelete = null }) {
                        Text("Cancel")
                    }
                })
        }

        if (transactionFriend != null) {
            AddTransactionDialog(
                friend = transactionFriend!!,
                onDismiss = { transactionFriend = null },
                onTransaction = { amount, isCredit ->

                    viewModel.addTransaction(
                        transactionFriend!!, amount, if (isCredit) TransactionType.CREDIT
                        else TransactionType.DEBIT
                    )

                    Toast.makeText(
                        context, "Transaction added", Toast.LENGTH_SHORT
                    ).show()

                    transactionFriend = null
                })
        }
    }
}

@Composable
fun AddTransactionDialog(
    friend: FriendEntity, onDismiss: () -> Unit, onTransaction: (Double, Boolean) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var isCredit by remember { mutableStateOf(true) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction for ${friend.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = input
                        }
                    },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { isCredit = true }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Credit (+)")
                    }
                    Button(
                        onClick = { isCredit = false }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCredit) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Debit (-)")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0.0) {
                    Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onTransaction(amountValue, isCredit)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })
}

@Composable
fun FriendItem(
    friend: FriendEntity, onClick: () -> Unit, onLongClick: () -> Unit
) {

    val balanceColor = when {
        friend.balance > 0 -> Color(0xFF2E7D32)
        friend.balance < 0 -> Color(0xFFC62828)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = friend.name, style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = kotlin.math.abs(friend.balance).toString(),
                color = balanceColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit, onAdd: (String, Double) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(
            onClick = {
                if (name.isBlank()) {
                    Toast.makeText(
                        context, "Name cannot be empty", Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val balanceValue = balance.toDoubleOrNull() ?: 0.0
                onAdd(name.trim(), balanceValue)

                Toast.makeText(
                    context, "Friend added", Toast.LENGTH_SHORT
                ).show()
            }) {
            Text("Add")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, title = { Text("Add Friend") }, text = {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = balance, onValueChange = { input ->
                if (input.matches(Regex("^\\d*\\.?\\d*$"))) {
                    balance = input
                }
            }, label = { Text("Balance (optional)") }, keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ), singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
    })
}