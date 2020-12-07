package com.example.trafficsigns.ui.fragments.Detail

import android.graphics.Color
import android.graphics.Color.YELLOW
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.trafficsigns.R
import com.example.trafficsigns.data.MyProfileViewModel
import com.example.trafficsigns.data.TrafficSign
import com.example.trafficsigns.databinding.FragmentDetailBinding
import com.google.android.material.tabs.TabLayoutMediator


class DetailFragment : Fragment(){

    private lateinit var binding: FragmentDetailBinding
    private lateinit var trafficSign: TrafficSign
    private lateinit var mMyProfileViewModel: MyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        mMyProfileViewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = this.arguments
        if (bundle != null){
            trafficSign = bundle.getSerializable("currentItem") as TrafficSign
        }
        Glide
            .with(view)
            .load(trafficSign.image)
            .override(binding.trafficImage.width, binding.trafficImage.height)
            .into(binding.trafficImage)
        binding.nameTextView.text = trafficSign.name
        binding.groupTextView.text = trafficSign.group?.capitalize() + " signs"
        binding.descriptionTextView.text = trafficSign.description


        binding.starButton.setOnClickListener {
            updateMyTrafficSignList()
            binding.starButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
        binding.floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_detailFragment_to_mainScreenFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstanceBundle(trafficSign: TrafficSign): Bundle {
            val args = Bundle()
            args.putSerializable("currentItem", trafficSign )
            return args
        }
    }

    private fun updateMyTrafficSignList(){
        val myProfile = mMyProfileViewModel.myProfile
        myProfile.knownTrafficSigns?.add(trafficSign)
        mMyProfileViewModel.updateProfile(myProfile)
    }

}