package com.example.store

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.store.databinding.ItemStoreBinding

class StoreAdapter {

    inner class Viewholder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)
    }
}