package com.qure.eattogether2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.ItemPeopleBinding
import com.qure.eattogether2.view.people.PeopleFragmentDirections

class PeopleAdapter(val userClick: (User) -> Unit) :
    ListAdapter<User, PeopleAdapter.ViewHolder>(PeopleDiffUtil) {


    override fun onBindViewHolder(holder: PeopleAdapter.ViewHolder, position: Int) {
        val people = getItem(position)
        val myUid = FirebaseAuth.getInstance().currentUser?.uid
        if (!people!!.uid.equals(myUid)) {
            holder.bind(people!!)
        }
    }

    inner class ViewHolder(private val binding: ItemPeopleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(people: User) {


            binding.user = people


            binding.apply {

                root.setOnClickListener { view ->
                    userClick(people)
                }

            }


        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_people
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemPeopleBinding>(layoutInflater, viewType, parent, false)

        return ViewHolder(binding)
    }


    companion object PeopleDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

    }
}

