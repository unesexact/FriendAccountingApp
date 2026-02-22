package com.unesexact.friendaccountingapp.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unesexact.friendaccountingapp.data.local.entity.FriendEntity
import com.unesexact.friendaccountingapp.databinding.ItemFriendBinding

class FriendAdapter : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    private var friends: List<FriendEntity> = emptyList()

    fun submitList(newList: List<FriendEntity>) {
        friends = newList
        notifyDataSetChanged()
    }

    class FriendViewHolder(
        private val binding: ItemFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendEntity) {
            binding.textName.text = friend.name
            binding.textBalance.text = friend.balance.toString()

            when {
                friend.balance > 0 -> binding.textBalance.setTextColor(Color.GREEN)
                friend.balance < 0 -> binding.textBalance.setTextColor(Color.RED)
                else -> binding.textBalance.setTextColor(Color.GRAY)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FriendViewHolder(binding)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }
}