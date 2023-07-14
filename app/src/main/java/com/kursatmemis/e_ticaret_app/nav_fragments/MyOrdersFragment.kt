package com.kursatmemis.e_ticaret_app.nav_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ProductOfCartAdapter
import com.kursatmemis.e_ticaret_app.models.CartOfUserResponse
import com.kursatmemis.e_ticaret_app.models.ProductOfCart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrdersFragment : Fragment() {

    private lateinit var noCartTextView: TextView
    private lateinit var productsOfCartListView: ListView
    private lateinit var productsOfCartAdapter: ArrayAdapter<ProductOfCart>
    private var productsOfCart = mutableListOf<ProductOfCart>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myOrdersLayout = layoutInflater.inflate(R.layout.fragment_my_orders, container, false)
        productsOfCartListView = myOrdersLayout.findViewById(R.id.productsOfCartListView)
        noCartTextView = myOrdersLayout.findViewById(R.id.noCartTextView)
        noCartTextView.visibility = View.INVISIBLE
        productsOfCartAdapter = ProductOfCartAdapter(productsOfCartListView.context,productsOfCart)
        productsOfCartListView.adapter = productsOfCartAdapter
        getProducts()


        return myOrdersLayout
    }

    private fun getProducts() {
        MainActivity.dummyService.getCartOfUser(MainActivity.userId).enqueue(object : Callback<CartOfUserResponse> {
            override fun onResponse(
                call: Call<CartOfUserResponse>,
                response: Response<CartOfUserResponse>
            ) {
                val body = response.body()
                if (body != null ) {
                    if (body.carts.isNotEmpty()) {
                        productsOfCart = body.carts[0].products as MutableList<ProductOfCart>
                        updateAdapter()
                    } else {
                        noCartTextView.visibility = View.VISIBLE
                    }

                } else {
                    Log.w("mKm - cartOfUser", "Body is null.")
                }
            }

            override fun onFailure(call: Call<CartOfUserResponse>, t: Throwable) {
                Log.w("mKm - cartOfUser", "onFailure: $t")
            }


        })
    }

    private fun updateAdapter() {
        productsOfCartAdapter.clear()
        productsOfCartAdapter.addAll(productsOfCart)
        productsOfCartAdapter.notifyDataSetChanged()
    }

}