package com.example.store

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.store.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())
            Thread{
                StoreApplication.databse.storeDao().addStore(store)
            }.start()
            mAdapter.add(store)
        }*/
        mBinding.fab.setOnClickListener { launchEditFragment() }
        setupRecyclerView()
    }

    private fun launchEditFragment(args:Bundle? = null) {
        val fragment = EditStoreFragment()

        if(args != null) fragment.arguments=args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //mBinding.fab.hide()
        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columnus))
        getStore()



        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStore(){
        doAsync {
        val stores = StoreApplication.databse.storeDao().getAllStores()
            uiThread {
                mAdapter.setStore(stores)
            }
        }

    }

    /*
    * OnClickListener
    * */

    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.key_id), storeId)

        launchEditFragment(args)
    }



    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isfavorite = !storeEntity.isfavorite

        doAsync {
            StoreApplication.databse.storeDao().updateStore(storeEntity)
            uiThread {
                //mAdapter.update(storeEntity)
                updateStore(storeEntity)

            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items, { dialogInterface, i ->
                when(i){
                    0 -> confirmDelete(storeEntity)

                    1 -> dial(storeEntity.phone)

                    2 -> goToWebsite(storeEntity.website)
                }
            })
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm,{ dialogInterface, which ->
                doAsync {
                    StoreApplication.databse.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
            })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }
    private fun dial(phone: String){
        val callItent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
       startIntent(callItent)
    }

    private fun goToWebsite(website: String){
        if(website.isEmpty()){
            Toast.makeText(this, R.string.main_error_website, Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)

        }

    }

    private fun startIntent(intent: Intent){
        if(intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }


    /*
    *MainAux
    * */
    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)

    }
}