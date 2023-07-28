package com.kursatmemis.e_ticaret_app.nav_fragments

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kursatmemis.e_ticaret_app.activities.DetailActivity
import com.kursatmemis.e_ticaret_app.activities.MainActivity
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.adapters.CartAdapter
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.shashank.sony.fancytoastlib.FancyToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCartFragment : BaseFragment() {
    override var dataSource: MutableList<Any> = mutableListOf()

    override fun setAdapter() {
        adapter =
            CartAdapter(appContext, dataSource as MutableList<ProductInCart>) as ArrayAdapter<Any>
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_my_cart
    }

    override fun getListViewResource(): Int {
        return R.id.cartListView
    }

    override fun getDataFromServiceOrFirebase() {

        if (MainActivity.isServiceLogin) {
            RetrofitManager.getProductsInCart(object : CallBack<List<ProductInCart>>{
                override fun onSuccess(data: List<ProductInCart>) {
                    dataSource = data.toMutableList()
                    if (dataSource.isEmpty()) {
                        showAlertDialog()
                    }
                    updateAdapter()
                }

                override fun onFailure(errorMessage: String) {
                    showFancyToast(errorMessage, FancyToast.ERROR)
                }

            })

        } else {
            FirebaseManager.getProductsInCart(object : CallBack<MutableList<ProductInCart>> {
                override fun onSuccess(data: MutableList<ProductInCart>) {
                    dataSource = data as MutableList<Any>
                    if (dataSource.isEmpty()) {
                        showAlertDialog()
                    }
                    updateAdapter()
                }

                override fun onFailure(errorMessage: String) {
                    showFancyToast(errorMessage, FancyToast.ERROR)
                }
            })
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle("Empty Cart")
        builder.setMessage("Your cart is empty. Let's add some products.")
        builder.show()
    }


    override fun onListItemClick(position: Int) {

        RetrofitManager.dummyService.getSingleProduct((dataSource[position] as ProductInCart).id!!)
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    val product = response.body()
                    val intent = Intent(appContext, DetailActivity::class.java)
                    intent.putExtra("product", product as Product)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    showFancyToast(t.message.toString(), FancyToast.ERROR)
                }

            })
    }


}



