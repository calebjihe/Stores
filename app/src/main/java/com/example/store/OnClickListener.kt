package com.example.store

interface OnClickListener {

    fun onClick(storeID: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}