package com.kursatmemis.e_ticaret_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductOfCategoryActivity : AppCompatActivity() {

    private lateinit var productsOfCategoryListView: ListView
    private lateinit var productsOfCategoryAdapter: ProductAdapter
    private var productsOfCategory = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_of_category)

        productsOfCategoryListView = findViewById(R.id.productsOfCategoryListView)
        val categoryName = intent.getStringExtra("categoryName")
        getProductsOfCategory(categoryName)
        productsOfCategoryAdapter = ProductAdapter(productsOfCategoryListView.context, productsOfCategory)
        productsOfCategoryListView.adapter = productsOfCategoryAdapter
    }

    private fun getProductsOfCategory(categoryName: String?) {
        MainActivity.dummyService.getProductsOfCategory(categoryName!!).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    productsOfCategory = body.products.toMutableList()
                    updateAdapter()
                    Log.w("mKm - getProducts", "Body: $productsOfCategory")
                } else {
                    Log.w("mKm - getProducts", "Body is null.")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.w("mKm - getProducts", "onFailure: $t")
            }

        })
    }

    private fun updateAdapter() {
        productsOfCategoryAdapter.clear()
        productsOfCategoryAdapter.addAll(productsOfCategory)
        productsOfCategoryAdapter.notifyDataSetChanged()
    }
}