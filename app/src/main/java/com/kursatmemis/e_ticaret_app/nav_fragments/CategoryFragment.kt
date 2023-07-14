package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.ProductOfCategoryActivity
import com.kursatmemis.e_ticaret_app.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment() {

    private lateinit var categoryNamesListView: ListView
    private var categoryNames = mutableListOf<String>()
    private lateinit var categoryNamesAdapter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val categoryFragmentLayout = inflater.inflate(R.layout.fragment_category, container, false)
        categoryNamesListView = categoryFragmentLayout.findViewById(R.id.categoryNamesListView)
        getCategoryNames()
        categoryNamesAdapter = ArrayAdapter<String>(
            categoryNamesListView.context,
            android.R.layout.simple_list_item_1,
            categoryNames
        )
        categoryNamesListView.adapter = categoryNamesAdapter


        categoryNamesListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(categoryNamesListView.context, ProductOfCategoryActivity::class.java)
            intent.putExtra("categoryName", categoryNames[position])
            startActivity(intent)
        }

        return categoryFragmentLayout
    }

    private fun getCategoryNames() {
        MainActivity.dummyService.getCategoryNames().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                val body = response.body()
                if (body != null) {
                    categoryNames = body.toMutableList()
                    updateAdapter()
                } else {
                    Log.w("mKm - categoryNames", "Body is null")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.w("mKm - categoryNames", "onFailure: $t")
            }

        })
    }

    private fun updateAdapter() {
        categoryNamesAdapter.clear()
        Log.w("mKm - categoryNames", "geldiler, $categoryNames")
        categoryNamesAdapter.addAll(categoryNames)
        categoryNamesAdapter.notifyDataSetChanged()
    }

}