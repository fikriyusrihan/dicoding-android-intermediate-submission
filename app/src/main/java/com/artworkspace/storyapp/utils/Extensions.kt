package com.artworkspace.storyapp.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.artworkspace.storyapp.R
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function for set the ImageView's src with Glide library
 *
 * @param context Context
 * @param url Image's URL
 */
fun ImageView.setImageFromUrl(context: Context, url: String) {
    Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.image_loading_placeholder)
        .centerCrop()
        .into(this)
}

/**
 * Set TextView text attribute to locale date format
 *
 * @param timestamp Timestamp
 */
fun TextView.setLocalDateFormat(timestamp: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = sdf.parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formattedDate
}