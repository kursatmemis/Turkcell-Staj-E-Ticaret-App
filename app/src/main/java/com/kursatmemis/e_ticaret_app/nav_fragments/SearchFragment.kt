package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import com.kursatmemis.e_ticaret_app.DetailActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.configs.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SearchFragment: BaseFragment() {
    override var dataSource: MutableList<Any> = mutableListOf()
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private var text: String = "ğ"

    var handler: Handler = Handler()
    var searchRunnable: Runnable? = null
    private val SEARCH_DELAY_MILLIS: Long = 800 // 0.8 s
    private var lastTextChangedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentLayout = super.onCreateView(inflater, container, savedInstanceState)
        searchEditText = fragmentLayout!!.findViewById(R.id.searchEditText)
        searchButton = fragmentLayout!!.findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            text = searchEditText.text.toString()
            getDataFromService()
        }

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

    override fun onListItemClick(position: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("product", dataSource[position] as Product)
        startActivity(intent)
    }

    override fun getDataFromService() {
        GlobalScope.launch(Dispatchers.Main) {
            dataSource = RetrofitManager.searchProduct(text).toMutableList()
            updateAdapter()
        }

    }

}