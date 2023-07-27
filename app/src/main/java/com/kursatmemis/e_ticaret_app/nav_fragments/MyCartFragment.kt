package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import com.kursatmemis.e_ticaret_app.activities.DetailActivity
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.CartAdapter
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCartFragment : BaseFragment() {
    override var dataSource: MutableList<Any> = mutableListOf()

    override fun setAdapter() {
        adapter = CartAdapter(appContext, dataSource as MutableList<ProductInCart>) as ArrayAdapter<Any>
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_my_cart
    }

    override fun getListViewResource(): Int {
        return R.id.cartListView
    }

    override fun getDataFromServiceOrFirebase() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            if (MainActivity.isServiceLogin) {
                RetrofitManager.getProductsInCart(dataSource, adapter)
                updateAdapter()
                Log.w("mKm-service", dataSource.toString())
            } else {
                dataSource = FirebaseManager.getProductsInCart() as MutableList<Any>
                updateAdapter()
            }
        }
    }

    override fun onListItemClick(position: Int) {

        RetrofitManager.dummyService.getSingleProduct((dataSource[position] as ProductInCart).id!!)
            .enqueue(object : Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    val product = response.body()
                    val intent = Intent(appContext, DetailActivity::class.java)
                    intent.putExtra("product", product as Product)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {

                }

            })
    }


}



