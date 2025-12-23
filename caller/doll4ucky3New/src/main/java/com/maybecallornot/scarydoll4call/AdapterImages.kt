package com.maybecallornot.scarydoll4call

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdapterImages(private val context: Context, private val images: ArrayList<Int>,
                    private val listener: OnClick):
    RecyclerView.Adapter<AdapterImages.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.kartinka_item,
            parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView).load(image).into(holder.itemView.findViewById(R.id.kartinka))
        holder.itemView.setOnClickListener {
            listener.onImageClick(image)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    interface OnClick {
        fun onImageClick(image: Int)
    }
}