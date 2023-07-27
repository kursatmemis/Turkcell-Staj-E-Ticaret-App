package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.activities.ProductOfCategoryActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class CategoryFragment : BaseFragment() {
    override var dataSource: MutableList<Any> = mutableListOf()

    override fun setAdapter() {
        adapter = ArrayAdapter(appContext, R.layout.category_name, dataSource)
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_category
    }

    override fun getListViewResource(): Int {
        return R.id.categoryNamesListView
    }

    override fun onListItemClick(position: Int) {
        val intent = Intent(requireContext(), ProductOfCategoryActivity::class.java)
        intent.putExtra("categoryName", dataSource[position] as String)
        startActivity(intent)
    }

    override fun getDataFromServiceOrFirebase() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val categoryNames = RetrofitManager.getCategoryNames().toMutableList()
            for (name in categoryNames) {
                val result = makeFirstCharUpperCase(name)
                dataSource.add(result)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun makeFirstCharUpperCase(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        val firstChar = input[0].uppercaseChar()
        return firstChar + input.substring(1)
    }

}