package com.qure.eattogether2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.ItemPeopleBinding
import com.qure.eattogether2.databinding.ItemSelectUserBinding
import com.qure.eattogether2.view.people.PeopleFragmentDirections

class ChatAllUserAdapter :
    PagingDataAdapter<User, ChatAllUserAdapter.ViewHolder>(PeopleDiffUtil) {

    interface ItemClickListener {
        fun onClick(view: View, user : User)
    }

    private lateinit var callBack: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.callBack = itemClickListener
    }

    override fun onBindViewHolder(holder: ChatAllUserAdapter.ViewHolder, position: Int) {
        val people = getItem(position)
        val myUid = FirebaseAuth.getInstance().currentUser?.uid
        if (!people!!.uid.equals(myUid)) {
            holder.bind(people!!)
        }
    }

    inner class ViewHolder(private val binding: ItemSelectUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(people: User) {

            binding.user = people

            binding.root.setOnClickListener { view ->
                if(binding.chatCheckbox.isChecked) {
                    binding.chatCheckbox.isChecked = false

                } else {
                    binding.chatCheckbox.isChecked = true

                }
                callBack.onClick(view, people)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_select_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemSelectUserBinding>(layoutInflater, viewType, parent, false)

        return ViewHolder(binding)
    }


    companion object PeopleDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}

