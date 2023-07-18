package com.kursatmemis.e_ticaret_app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kursatmemis.e_ticaret_app.models.AddedToBeCartResponse
import com.kursatmemis.e_ticaret_app.models.AddedToBeProduct
import com.kursatmemis.e_ticaret_app.models.CartToBeAdded
import com.kursatmemis.e_ticaret_app.models.Product
import me.relex.circleindicator.CircleIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private var currentIndex = 0 // Geçerli resmin dizideki indeksi
    private lateinit var titleTextView: TextView
    private lateinit var brandTextView: TextView
    private lateinit var stockTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var addToCartButton: Button


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        stockTextView = findViewById(R.id.stockTextView)
        brandTextView = findViewById(R.id.brandTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        priceTextView = findViewById(R.id.priceTextView)
        addToCartButton = findViewById(R.id.addToCartButton)

        val product = intent.getSerializableExtra("product") as Product
        val images = product.images
        val title = product.title
        val description = product.description
        val stock = product.stock
        val brand = product.brand
        val category = product.category
        val price = "${product.price}$"
        val productId = product.id

        titleTextView.text = title
        descriptionTextView.text = description
        stockTextView.text = stock.toString()
        brandTextView.text = brand
        categoryTextView.text = category
        priceTextView.text = price

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val indicator = findViewById<CircleIndicator>(R.id.indicator)

        val adapter = ImagePagerAdapter(images)
        viewPager.adapter = adapter
        indicator.setViewPager(viewPager)


        addToCartButton.setOnClickListener{
            addNewCart(productId)
        }

    }

    private fun addNewCart(productId: Long) {
        val addedToBeProduct = AddedToBeProduct(productId, 1)
        val products = mutableListOf<AddedToBeProduct>()
        products.add(addedToBeProduct)
        val cartToBeAdded = CartToBeAdded(MainActivity.userId.toLong(), products)
        MainActivity.dummyService.addNewCart(cartToBeAdded).enqueue(object : Callback<AddedToBeCartResponse>{
            override fun onResponse(
                call: Call<AddedToBeCartResponse>,
                response: Response<AddedToBeCartResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    Toast.makeText(priceTextView.context, "The product added to cart successfully.", Toast.LENGTH_SHORT).show()
                    Log.w("mKm - addNewCart", body.toString())
                }
                 else {
                    Log.w("mKm - addNewCart", "Body is null.")
                }
            }

            override fun onFailure(call: Call<AddedToBeCartResponse>, t: Throwable) {
                Log.w("mKm - addNewCart", "onFailure: $t")
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