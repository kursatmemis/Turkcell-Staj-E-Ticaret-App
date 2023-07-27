package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.activities.DetailActivity
import com.kursatmemis.e_ticaret_app.models.Product

abstract class BaseFragment : Fragment() {
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<Any>
    abstract var dataSource: MutableList<Any>

    lateinit var appContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appContext = inflater.context
        val fragmentLayout = inflater.inflate(getLayoutResource(), container, false)
        setAdapter()
        listView = fragmentLayout.findViewById(getListViewResource())
        listView.adapter = adapter
        getDataFromServiceOrFirebase()

        listView.setOnItemClickListener { parent, view, position, id ->
            onListItemClick(position)
        }

        return fragmentLayout
    }

    open fun onListItemClick(position: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("product", dataSource[position] as Product)
        startActivity(intent)
    }

    fun updateAdapter() {
        adapter.clear()
        adapter.addAll(dataSource)
        adapter.notifyDataSetChanged()
    }

    abstract fun setAdapter()
    abstract fun getLayoutResource(): Int
    abstract fun getListViewResource(): Int
    abstract fun getDataFromServiceOrFirebase()

}

