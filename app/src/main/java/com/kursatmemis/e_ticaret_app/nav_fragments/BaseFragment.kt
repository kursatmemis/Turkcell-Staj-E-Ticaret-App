package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.configs.RetrofitManager

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
        getDataFromService()

        listView.setOnItemClickListener { parent, view, position, id ->
            onListItemClick(position)
        }

        return fragmentLayout
    }

    fun updateAdapter() {
        adapter.clear()
        adapter.addAll(dataSource)
        adapter.notifyDataSetChanged()
    }

    abstract fun setAdapter()
    abstract fun getLayoutResource(): Int
    abstract fun getListViewResource(): Int
    abstract fun onListItemClick(position: Int)
    abstract fun getDataFromService()
}

