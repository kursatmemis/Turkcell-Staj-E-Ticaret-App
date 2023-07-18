package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.DetailActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.configs.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    override var dataSource: MutableList<Any> = mutableListOf()

    override fun setAdapter() {
        adapter = ProductAdapter(appContext, dataSource as List<Product>) as ArrayAdapter<Any>
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getListViewResource(): Int {
        return R.id.productsListView
    }

    override fun onListItemClick(position: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("product", dataSource[position] as Product)
        startActivity(intent)
    }

    override fun getDataFromService() {
        GlobalScope.launch(Dispatchers.Main) {
            dataSource = RetrofitManager.getAllProducts().toMutableList()
            updateAdapter()
        }
    }

}