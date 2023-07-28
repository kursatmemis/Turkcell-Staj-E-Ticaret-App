package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.activities.ProductOfCategoryActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.shashank.sony.fancytoastlib.FancyToast

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
        RetrofitManager.getCategoryNames(object : CallBack<List<String>> {
            override fun onSuccess(data: List<String>) {
                for (categoryName in data) {
                    val result = makeFirstCharUpperCase(categoryName)
                    dataSource.add(result)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(errorMessage: String) {
                showFancyToast(errorMessage, FancyToast.ERROR)
            }
        })
    }

    private fun makeFirstCharUpperCase(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        val firstChar = input[0].uppercaseChar()
        return firstChar + input.substring(1)
    }

}