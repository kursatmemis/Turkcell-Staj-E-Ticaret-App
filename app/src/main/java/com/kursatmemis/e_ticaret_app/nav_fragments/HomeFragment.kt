package com.kursatmemis.e_ticaret_app.nav_fragments

import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    override var dataSource: MutableList<Any> = mutableListOf()

    override fun setAdapter() {
        adapter = ProductAdapter(appContext, dataSource as List<Product>) as ArrayAdapter<Any>
    }

    override fun getListViewResource(): Int {
        return R.id.productsListView
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getDataFromService() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            dataSource = RetrofitManager.getAllProducts().toMutableList()
            updateAdapter()
        }
    }

}