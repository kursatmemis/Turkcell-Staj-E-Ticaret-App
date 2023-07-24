package com.kursatmemis.e_ticaret_app.nav_fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.kursatmemis.e_ticaret_app.MainActivity
import com.kursatmemis.e_ticaret_app.adapters.ExpandableListAdapter
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.databinding.FragmentMyCartBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.models.ProductInCart
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCartFragment : Fragment() {

    private var binding: FragmentMyCartBinding? = null
    lateinit var cartAdapter: ExpandableListAdapter
    lateinit var cartListView: ExpandableListView
    private var productsInCart = mutableListOf<ProductInCart>()
    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCartBinding.inflate(inflater, container, false)
        context = inflater.context
        cartListView = binding?.productsOfCartListView!!
        setListViewAdapter()

        showProgressBar()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            if (MainActivity.isServiceLogin) {
                productsInCart = getProductsInCartFromService()
            } else {
                productsInCart = getProductOfCartWithFirebase()
            }
            updateAdapter()
            hideProgressBar()
        }

        cartListView.setOnItemLongClickListener { parent, view, position, id ->
            showAlertDialog(position)
            true
        }

        return binding!!.root
    }

    private fun showAlertDialog(position: Int) {
        var message: String
        var fancyType: Int

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("Are you sure to remove the product from the cart?")
        alertDialog.setPositiveButton("Yes"
        ) { _, _ ->
            if (MainActivity.isServiceLogin) {
                // Serviste sepetten ürün silme aksiyonu yok.
                val product = productsInCart[position]
                productsInCart.remove(product)
            } else {
                val product = productsInCart[position]
                val productId = product.id
                removeProductFromFirebase(productId.toString())
                productsInCart.remove(product)
            }
            updateAdapter()
            message = "The product removed from the cart."
            fancyType = FancyToast.INFO
            showFancyToast(message, fancyType)
        }

        alertDialog.setNegativeButton("No"
        ) { _, _ ->
            message = "It is cancelled."
            fancyType = FancyToast.DEFAULT
            showFancyToast(message, fancyType)
        }
        alertDialog.show()
    }

    private fun removeProductFromFirebase(productId: String) {
        FirebaseManager.removeProductInCart(productId)
    }

    private suspend fun getProductOfCartWithFirebase(): MutableList<ProductInCart> {
        return FirebaseManager.getProductsInCart()
    }

    private suspend fun getProductsInCartFromService(): MutableList<ProductInCart> {
        return RetrofitManager.getProductsInCart().toMutableList()
    }

    private fun getHeaders(): MutableList<String?> {
        val headers = mutableListOf<String?>()
        for (product in productsInCart) {
            headers.add(product.title)
        }
        return headers
    }

    private fun getChildren(): MutableList<ProductInCart> {
        val children = mutableListOf<ProductInCart>()
        children.addAll(productsInCart)
        return children
    }

    private fun updateAdapter() {
        cartAdapter.clear()
        cartAdapter.addAll(getHeaders(), getChildren())
        cartAdapter.notifyDataSetChanged()
    }

    private fun setListViewAdapter() {
        cartAdapter = ExpandableListAdapter(
            context,
            getHeaders(), getChildren()
        )
        cartListView.setAdapter(cartAdapter)
    }

    private fun showFancyToast(message: String, fancyType: Int) {
        FancyToast.makeText(
            layoutInflater.context,
            message,
            FancyToast.LENGTH_SHORT,
            fancyType,
            false
        ).show()
    }

    private fun hideProgressBar() {
        binding?.progressBar?.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}



