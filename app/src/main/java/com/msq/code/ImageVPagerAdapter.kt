package com.msq.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.R
import com.socialgalaxyApp.util.extension.loadWallImage

class ImageVPagerAdapter(
    private val images: ArrayList<Pair<Int, String>>,
    val onClick: () -> Unit,
) : PagerAdapter() {


    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(container.getContext()).inflate(R.layout.slidingimages_layout, null)
        val imageView = view.findViewById<RoundedImageView>(R.id.image)
        val tvCenter = view.findViewById<TextView>(R.id.tvCenter)
        val tvBottomCenter = view.findViewById<TextView>(R.id.tvBottomCenter)
        val tvBottomCenterWith = view.findViewById<TextView>(R.id.tvBottomCenterWith)
        val tvBottom = view.findViewById<TextView>(R.id.tvBottom)
        imageView.loadWallImage(images[position].first)
        imageView.setOnClickListener {
            onClick.invoke()
        }
        val text = when (position) {
            0 -> "How can you keep\nyour home squeaky clean?"
            1 -> "How can you keep\nyour home squeaky clean?"
            2 -> "How to take care of your infant\nlike a mother would?"
            3 -> "How to take care of your infant\nlike a mother would?"
            4 -> "How can you regain\nthe energy you've los over\nthe course of a tough week?"
            5 -> "How can you regain\nthe energy you've los over\nthe course of a tough week?"
            6 -> "How can we get\neasy and fast haircut\nat your home"
            7 -> "How can we get\neasy and fast haircut\nat your home"
            8 -> "How can we educate\nyour child at home\nfor a brighten future."
            9 -> "How can we educate\nyour child at home\nfor a brighten future."
            else -> ""
        }
        tvCenter.text = text
        tvCenter.isVisible = images[position].second=="1"
        tvBottomCenter.text = text
        tvBottomCenter.isVisible = images[position].second=="2"
        tvBottomCenterWith.isVisible = images[position].second=="2"
        tvBottom.text = text
        tvBottom.isVisible = images[position].second=="3"
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}