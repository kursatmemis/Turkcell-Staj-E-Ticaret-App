package com.kursatmemis.e_ticaret_app.activities

import android.content.Intent
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
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.ProductToBeAdded
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.Product
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getSerializableExtra("product") as Product
        initViews(product)
        setupViewPager(product.images)

        binding.addToCartButton.setOnClickListener {
            if (MainActivity.isServiceLogin) {
                addProductToCartWithService(product.id)
            } else {
                addProductToCartWithFirebase(product)
            }
        }

        binding.commentsTextView.setOnClickListener {
            goToCommentActivity(product.id)
        }

    }

    private fun goToCommentActivity(productId: Long) {
        val intent = Intent(this@DetailActivity, CommentActivity::class.java)
        Log.w("aaa", productId.toString())
        intent.putExtra("productId", productId)
        startActivity(intent)
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
        this.showFancyToast(message, fancyType)

        val productInCart = ProductInCart(
            product.thumbnail,
            product.id,
            product.title,
            product.description,
            product.price,
            1,
            product.price,
            product.discountPercentage,
            calculateDiscountedPrice(product.price, product.discountPercentage).toLong()
        )
        FirebaseManager.addProductToCart(productInCart, object : CallBack<Any> {
            override fun onSuccess(data: Any) {
                // Bir şey yapma.
            }

            override fun onFailure(errorMessage: String) {
                showFancyToast(errorMessage, FancyToast.ERROR)
            }

        })
    }

    private fun calculateDiscountedPrice(price: Long, discountPercentage: Double): Double {
        return price - price * (discountPercentage/100.0)
    }

    private fun addProductToCartWithService(productId: Long) {
        val products = mutableListOf<ProductToBeAdded>()
        val productToBeAdded = ProductToBeAdded(productId, 1)
        products.add(productToBeAdded)
        val cartToBeAdded = CartToBeAdded(MainActivity.userId!!.toLong(), products)
        return RetrofitManager.addProductToCart(
            cartToBeAdded,
            object : CallBack<Boolean> {
                override fun onSuccess(data: Boolean) {
                    val message = "The product added to the cart successfully!"
                    val type = FancyToast.INFO
                    this@DetailActivity.showFancyToast(message, type)
                }

                override fun onFailure(errorMessage: String) {
                    val message = "Error!"
                    val type = FancyToast.WARNING
                    this@DetailActivity.showFancyToast(message, type)
                }

            })
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