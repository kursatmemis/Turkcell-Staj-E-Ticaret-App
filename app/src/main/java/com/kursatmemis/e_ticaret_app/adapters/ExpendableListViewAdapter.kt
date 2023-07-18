package com.kursatmemis.e_ticaret_app.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.models.ProductOfCart


class ExpandableListAdapter(
    private val context: Context,
    private var listDataHeader: List<String>,
    private var listDataChild: List<ProductOfCart>
) : BaseExpandableListAdapter() {

    fun setData(headers: List<String>, children: MutableList<ProductOfCart>) {
        listDataHeader = headers
        listDataChild = children
    }

    override fun getGroup(groupPosition: Int): String {
        return listDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ProductOfCart {
        return listDataChild[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = LayoutInflater.from(context)
        val headerTitle = getGroup(groupPosition)
        val view = layoutInflater.inflate(R.layout.orders_group_item, parent, false)
        val groupIndicatorImageView = view.findViewById<ImageView>(R.id.groupIndicatorImageView)
        val titleTextView = view.findViewById<TextView>(R.id.productTitleTextView)
        titleTextView.text = headerTitle

        groupIndicatorImageView.setColorFilter(ContextCompat.getColor(context, if (isExpanded) R.color.blue_gorotto else R.color.white), PorterDuff.Mode.SRC_IN)
        groupIndicatorImageView.rotation = if (isExpanded) 180f else 0f
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = LayoutInflater.from(context)
        val childData = getChild(groupPosition, childPosition)
        val view = layoutInflater.inflate(R.layout.orders_child_text_view, parent, false)
        val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
        val totalTextView = view.findViewById<TextView>(R.id.totalTextView)
        val discountPercentageTextView = view.findViewById<TextView>(R.id.discountPercentageTextView)
        val discountedPriceTextView = view.findViewById<TextView>(R.id.discountedPriceTextView)
        val quantityTextView = view.findViewById<TextView>(R.id.quantityTextView)

        priceTextView.text = "${childData.price}$"
        totalTextView.text = "${childData.total}$"
        discountPercentageTextView.text = childData.discountPercentage.toString()
        discountedPriceTextView.text = "${childData.discountedPrice}$"
        quantityTextView.text = childData.quantity.toString()
        return view
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}