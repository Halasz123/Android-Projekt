package com.example.trafficsigns.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trafficsigns.R
import com.example.trafficsigns.data.TrafficSignsCollection
import com.example.trafficsigns.ui.fragments.List.CollectionListFragment
import com.example.trafficsigns.ui.interfaces.ItemClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.group_item.view.*
import kotlinx.android.synthetic.main.sample_list_item.view.*


class MainMenuAdapter(private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MainMenuAdapter.MyViewHolder>(){

    private var collectionList =  emptyList<TrafficSignsCollection>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val  currentItem = collectionList[position]

        holder.itemView.text_view_list_item.text = currentItem.groupId.capitalize() + " signs"
        myGlide(holder.itemView, currentItem.trafficSigns[0].image, holder.itemView.image_view_list_item)
        myGlide(holder.itemView, currentItem.trafficSigns[1].image, holder.itemView.imageView)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClickListener(position)
        }
    }

    override fun getItemCount() = collectionList.count()

    fun setData(collection: List<TrafficSignsCollection>){
        this.collectionList = collection
        notifyDataSetChanged()
    }

    private fun myGlide(view: View, url: String?, imageView: ImageView) {
        Glide
            .with(view)
            .load(url)
            .override(imageView.width, imageView.height)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imageView)
    }
}