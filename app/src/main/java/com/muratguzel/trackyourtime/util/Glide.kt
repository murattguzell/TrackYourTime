package com.muratguzel.trackyourtime.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muratguzel.trackyourtime.R

fun ImageView.imageDownload(url: String?, placeholder: CircularProgressDrawable) {
    val options = RequestOptions().placeholder(placeholder).error(R.drawable.ic_profile)
    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

fun placeHolderCreate(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}