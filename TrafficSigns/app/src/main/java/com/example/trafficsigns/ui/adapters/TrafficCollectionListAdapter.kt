package com.example.trafficsigns.ui.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trafficsigns.data.TrafficSignsCollection
import com.example.trafficsigns.ui.fragments.List.ARG_OBJECT
import com.example.trafficsigns.ui.fragments.List.SampleListFragment
import com.example.trafficsigns.ui.interfaces.SetOnCheckedChangeListener

class TrafficCollectionListAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private var collectionList = emptyList<TrafficSignsCollection>()
    private lateinit var myListener: SetOnCheckedChangeListener

    override fun getItemCount(): Int = collectionList.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
            return SampleListFragment.newInstance(collectionList[position].trafficSigns)
    }

    fun setData(collection: List<TrafficSignsCollection>){
        this.collectionList = collection
        notifyDataSetChanged()
    }

    fun setListener(myListener: SetOnCheckedChangeListener) {
        this.myListener = myListener
    }

}