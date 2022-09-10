package com.example.lesson10networkandscrolledlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.lesson10networkandscrolledlist.databinding.ItemLoadingBinding
import com.example.lesson10networkandscrolledlist.databinding.ItemUserBinding

class UserAdapter(
    context: Context,
    private val onUserClicked: (User) -> Unit
) : ListAdapter<PagingData<User>, RecyclerView.ViewHolder>(DIFF_UTIL) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingData.Item -> TYPE_USER
            PagingData.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER -> {
                UserViewHolder(
                    binding = ItemUserBinding.inflate(layoutInflater, parent, false),
                    onUserClicked = onUserClicked
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> error("Unsupported viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PagingData.Item -> {
                checkNotNull(holder as UserViewHolder) { "Incorrect viewHolder $item" }
                holder.bind(item.data)
            }
            PagingData.Loading -> {
                // no op
            }
        }
     }

    companion object {

        private const val TYPE_USER = 0
        private const val TYPE_LOADING = 1

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingData<User>>() {
            override fun areItemsTheSame(
                oldItem: PagingData<User>,
                newItem: PagingData<User>
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PagingData<User>,
                newItem: PagingData<User>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onUserClicked: (User) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: User) {
        with(binding) {
            imageAvatar.load(item.avatarUrl)
            textName.text = item.login

            root.setOnClickListener {
                onUserClicked(item)
            }
        }
    }
}

class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)