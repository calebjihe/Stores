package com.example.store

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.store.databinding.ItemStoreBinding

class StoreAdapter(private var stores:MutableList<Store>, private var  listener: OnClickListener):
    RecyclerView.Adapter<StoreAdapter.Viewholder>() {

    private lateinit var mContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return Viewholder(view)

    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        var store = stores.get(position)

        with(holder){
            setListener(store)
            binding.tvName.text=store.name
        }
    }

    override fun getItemCount(): Int = stores.size

    inner class Viewholder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)

        fun setListener(store: Store){
            binding.root.setOnLongClickListener { listener.onClick(store) }
        }
    }
}