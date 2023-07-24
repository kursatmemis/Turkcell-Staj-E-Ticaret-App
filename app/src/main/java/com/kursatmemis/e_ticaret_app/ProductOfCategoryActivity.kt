package com.kursatmemis.e_ticaret_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kursatmemis.e_ticaret_app.adapters.ProductAdapter
import com.kursatmemis.e_ticaret_app.databinding.ActivityProductOfCategoryBinding
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductOfCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductOfCategoryBinding
    private lateinit var productsOfCategoryAdapter: ProductAdapter
    private var productsOfCategoryList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductOfCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categoryName = intent.getStringExtra("categoryName")

        RetrofitManager.getProductsOfCategory(categoryName, object : RetrofitManager.CallBack<MutableList<Product>> {
            override fun onSuccess(data: MutableList<Product>) {
                productsOfCategoryList = data
                updateAdapter()
            }

            override fun onFailure() {
                Toast.makeText(this@ProductOfCategoryActivity, "Error.", Toast.LENGTH_SHORT).show()
            }

        })

        binding.productsOfCategoryListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("product", productsOfCategoryList[position])
            startActivity(intent)
        }

    }

    private fun setAdapter() {
        productsOfCategoryAdapter =
            ProductAdapter(this@ProductOfCategoryActivity, productsOfCategoryList)
        binding.productsOfCategoryListView.adapter = productsOfCategoryAdapter
    }

    private fun updateAdapter() {
        productsOfCategoryAdapter.clear()
        productsOfCategoryAdapter.addAll(productsOfCategoryList)
        productsOfCategoryAdapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}