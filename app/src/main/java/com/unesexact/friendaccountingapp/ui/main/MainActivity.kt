package com.unesexact.friendaccountingapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unesexact.friendaccountingapp.data.local.database.DatabaseProvider
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.data.local.entity.TransactionType
import com.unesexact.friendaccountingapp.data.repository.FriendRepository
import com.unesexact.friendaccountingapp.data.repository.TransactionRepository
import com.unesexact.friendaccountingapp.ui.theme.AccentColor
import com.unesexact.friendaccountingapp.ui.theme.CardBackground
import com.unesexact.friendaccountingapp.ui.theme.DarkBackground
import com.unesexact.friendaccountingapp.ui.theme.FriendAccountingTheme
import com.unesexact.friendaccountingapp.ui.theme.NegativeBalance
import com.unesexact.friendaccountingapp.ui.theme.NeutralBalance
import com.unesexact.friendaccountingapp.ui.theme.PositiveBalance
import com.unesexact.friendaccountingapp.ui.theme.WhiteText
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FriendViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val friendRepository = FriendRepository(db.friendDao())
        val transactionRepository = TransactionRepository(db.transactionDao())
        val factory = FriendViewModelFactory(friendRepository, transactionRepository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        setContent {
            FriendAccountingTheme {

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                var showAddFriendDialog by rememberSaveable { mutableStateOf(false) }

                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Friend Accounting") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkBackground, titleContentColor = WhiteText
                        )
                    )
                }, floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showAddFriendDialog = true },
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Text("+", style = MaterialTheme.typography.headlineMedium)
                    }
                }) { padding ->

                    FriendsScreen(
                        viewModel = viewModel,
                        padding = padding,
                        snackbarHostState = snackbarHostState
                    )

                    if (showAddFriendDialog) {
                        AddFriendDialog(
                            onDismiss = { showAddFriendDialog = false },
                            onAdd = { name, balance ->
                                viewModel.addFriend(name, balance)
                                showAddFriendDialog = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Friend added")
                                }
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsScreen(
    viewModel: FriendViewModel, padding: PaddingValues, snackbarHostState: SnackbarHostState
) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    var friendToDelete by remember { mutableStateOf<FriendEntity?>(null) }
    var transactionFriend by remember { mutableStateOf<FriendEntity?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = friends, key = { it.id }   // 🔥 performance improvement
        ) { friend ->
            FriendItem(
                friend = friend,
                onClick = { transactionFriend = friend },
                onLongClick = { friendToDelete = friend })
        }
    }

    friendToDelete?.let { friend ->
        AlertDialog(
            onDismissRequest = { friendToDelete = null },
            title = { Text("Delete Friend") },
            text = { Text("Delete ${friend.name}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteFriend(friend)
                    scope.launch {
                        snackbarHostState.showSnackbar("Friend deleted")
                    }
                    friendToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { friendToDelete = null }) {
                    Text("Cancel")
                }
            })
    }

    transactionFriend?.let { friend ->
        AddTransactionDialog(
            friend = friend,
            onDismiss = { transactionFriend = null },
            snackbarHostState = snackbarHostState,
            onTransaction = { amount, isCredit ->
                viewModel.addTransaction(
                    friend, amount, if (isCredit) TransactionType.CREDIT else TransactionType.DEBIT
                )
                scope.launch {
                    snackbarHostState.showSnackbar("Transaction added")
                }
                transactionFriend = null
            })
    }
}

@Composable
fun FriendItem(
    friend: FriendEntity, onClick: () -> Unit, onLongClick: () -> Unit
) {
    val balanceColor = when {
        friend.balance > 0 -> PositiveBalance
        friend.balance < 0 -> NegativeBalance
        else -> NeutralBalance
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = friend.name, style = MaterialTheme.typography.titleMedium, color = WhiteText
            )

            Text(
                text = kotlin.math.abs(friend.balance).toString(),
                style = MaterialTheme.typography.titleMedium,
                color = balanceColor
            )
        }
    }
}

@Composable
fun AddTransactionDialog(
    friend: FriendEntity,
    onDismiss: () -> Unit,
    onTransaction: (Double, Boolean) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var isCredit by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction for ${friend.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        focusedBorderColor = WhiteText,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = WhiteText,
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = WhiteText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isCredit = true }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCredit) PositiveBalance else AccentColor
                        )
                    ) { Text("Credit (+)") }

                    Button(
                        onClick = { isCredit = false }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCredit) NegativeBalance else AccentColor
                        )
                    ) { Text("Debit (-)") }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.toDoubleOrNull()
                    if (value == null || value <= 0.0) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Enter valid amount")
                        }
                        return@Button
                    }
                    onTransaction(value, isCredit)
                }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        })
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit, onAdd: (String, Double) -> Unit, snackbarHostState: SnackbarHostState
) {
    var name by rememberSaveable { mutableStateOf("") }
    var balance by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Friend") }, text = {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    focusedBorderColor = WhiteText,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = WhiteText,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = WhiteText
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = balance,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        balance = it
                    }
                },
                label = { Text("Balance (optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    focusedBorderColor = WhiteText,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = WhiteText,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = WhiteText
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }, confirmButton = {
        Button(onClick = {
            if (name.isBlank()) {
                scope.launch {
                    snackbarHostState.showSnackbar("Name cannot be empty")
                }
                return@Button
            }

            val value = balance.toDoubleOrNull() ?: 0.0
            onAdd(name.trim(), value)
        }) { Text("Add") }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Cancel") }
    })
}