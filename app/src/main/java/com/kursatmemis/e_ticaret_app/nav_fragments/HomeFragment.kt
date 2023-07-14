package com.kursatmemis.e_ticaret_app.nav_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var productsListView: ListView
    private lateinit var productAdapter: ProductAdapter
    private var products = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeFragmentLayout = inflater.inflate(R.layout.fragment_home, container, false)
        productsListView = homeFragmentLayout.findViewById(R.id.productsListView)


        productAdapter = ProductAdapter(productsListView.context, products)
        productsListView.adapter = productAdapter
        getProducts()

        return homeFragmentLayout
    }

    private fun getProducts() {
        MainActivity.dummyService.getAllProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    products = body.products.toMutableList()
                    updateAdapter()
                    Log.w("mKm - getProducts", "Body: $products")
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
        productAdapter.clear()
        Log.w("sad", products.toString())
        productAdapter.addAll(products)
        productAdapter.notifyDataSetChanged()
    }

}