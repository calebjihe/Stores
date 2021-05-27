package com.example.store

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.store.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
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
        mGridLayout = GridLayoutManager(this, 2)
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