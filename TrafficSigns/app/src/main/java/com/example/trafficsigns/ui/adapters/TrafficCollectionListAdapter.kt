package com.example.trafficsigns.ui.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trafficsigns.data.TrafficSignsCollection
import com.example.trafficsigns.ui.fragments.List.ARG_OBJECT
import com.example.trafficsigns.ui.fragments.List.SampleListFragment

class TrafficCollectionListAdapter(fragment: Fragment, collectionList: List<TrafficSignsCollection>) : FragmentStateAdapter(fragment) {
    private val collectionList = collectionList

    override fun getItemCount(): Int = collectionList.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return SampleListFragment.newInstance(collectionList[position].trafficSigns)
    }

}