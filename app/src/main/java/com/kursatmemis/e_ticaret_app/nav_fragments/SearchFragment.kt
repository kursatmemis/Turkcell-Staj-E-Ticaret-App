package com.kursatmemis.e_ticaret_app.nav_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SearchFragment : BaseFragment() {
    override var dataSource: MutableList<Any> = mutableListOf()
    private lateinit var searchView: SearchView
    private var text: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentLayout = super.onCreateView(inflater, container, savedInstanceState)
        searchView = fragmentLayout!!.findViewById(R.id.searchView)
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                text = query!!
                getDataFromServiceOrFirebase()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                text = newText!!
                getDataFromServiceOrFirebase()
                return true
            }

        })



        return fragmentLayout
    }

    override fun setAdapter() {
        adapter = ProductAdapter(appContext, dataSource as List<Product>) as ArrayAdapter<Any>
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_search
    }

    override fun getListViewResource(): Int {
        return R.id.searchListView
    }

    override fun getDataFromServiceOrFirebase() {
        Log.w("mKm-ssd", text)
        if (text.trim().isNotEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                dataSource = RetrofitManager.searchProduct(text).toMutableList()
                updateAdapter()
            }
        } else {
            adapter.clear()
            adapter.notifyDataSetChanged()
        }

    }

}