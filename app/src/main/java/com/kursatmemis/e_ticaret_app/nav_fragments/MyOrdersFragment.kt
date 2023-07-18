package com.kursatmemis.e_ticaret_app.nav_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnGroupExpandListener
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.ExpandableListAdapter
import com.kursatmemis.e_ticaret_app.configs.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.ProductOfCart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyOrdersFragment : Fragment() {
    lateinit var  cartAdapter: ExpandableListAdapter
    lateinit var  cartListView: ExpandableListView
    private var productOfCart = mutableListOf<ProductOfCart>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout = inflater.inflate(R.layout.fragment_my_orders, container, false)

        cartAdapter = ExpandableListAdapter(inflater.context, getHeaders(), getChildren())
        cartListView = fragmentLayout.findViewById(R.id.productsOfCartListView)

        cartListView.setAdapter(cartAdapter)
        GlobalScope.launch(Dispatchers.Main) {
            productOfCart = RetrofitManager.getCartOfUser().toMutableList()
            updateAdapter()
        }

        return fragmentLayout
    }

    private fun getHeaders(): List<String> {
        val headers = mutableListOf<String>()
        for (product in productOfCart) {
            headers.add(product.title)
        }
        return headers
    }

    private fun getChildren(): List<ProductOfCart> {
        val children = mutableListOf<ProductOfCart>()
        for (product in productOfCart) {
            children.add(product)
        }
        return children
    }

    private fun updateAdapter() {
        cartAdapter.setData(getHeaders(), productOfCart)
        cartAdapter.notifyDataSetChanged()
    }
}



