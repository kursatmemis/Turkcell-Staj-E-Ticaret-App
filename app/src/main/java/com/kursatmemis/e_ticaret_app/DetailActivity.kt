package com.kursatmemis.e_ticaret_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kursatmemis.e_ticaret_app.databinding.ActivityDetailBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.ProductToBeAdded
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.Product
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val product = intent.getSerializableExtra("product") as Product
        initViews(product)
        setupViewPager(product.images)

        binding.addToCartButton.setOnClickListener {
            var message: String
            var fancyType: Int
            if (MainActivity.isServiceLogin) {
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    val result = addProductToCartWithService(product.id)
                    if (result != null) {
                        message = "The produces added to cart successfully."
                        fancyType = FancyToast.INFO
                        showFancyToast(message, fancyType)
                    } else {
                        message = "The product could not be added to the cart."
                        fancyType = FancyToast.INFO
                        showFancyToast(message, fancyType)
                    }
                }
            } else {
                addProductToCartWithFirebase(product)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showFancyToast(message: String, fancyType: Int) {
        FancyToast.makeText(this@DetailActivity, message, FancyToast.LENGTH_SHORT, fancyType, false).show()
    }

    private fun setupViewPager(images: List<String>) {
        val viewPager = binding.viewPager
        val indicator = binding.indicator
        val adapter = ImagePagerAdapter(images)
        viewPager.adapter = adapter
        indicator.setViewPager(viewPager)
    }

    private fun initViews(product: Product) {
        binding.titleTextView.text = product.title
        binding.descriptionTextView.text = product.description
        binding.stockTextView.text = product.stock.toString()
        binding.brandTextView.text = product.brand
        binding.categoryTextView.text = product.category
        binding.priceTextView.text = "${product.price}$"
    }

    private fun addProductToCartWithFirebase(product: Product) {
        val message = "The produces added to cart successfully."
        val fancyType = FancyToast.INFO
        showFancyToast(message, fancyType)
        FirebaseManager.addProductToCart(product)
    }

    private suspend fun addProductToCartWithService(productId: Long): AddedToBeCartResponse? {
        val productToBeAdded = ProductToBeAdded(productId, 1)
        val products = mutableListOf<ProductToBeAdded>()
        products.add(productToBeAdded)
        val cartToBeAdded = CartToBeAdded(MainActivity.userId!!.toLong(), products)
        return RetrofitManager.addProductToCart(cartToBeAdded)
    }

    private inner class ImagePagerAdapter(private val imageUrls: List<String>) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            Glide.with(container)
                .load(imageUrls[position])
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageView)

            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return imageUrls.size
        }
    }


}