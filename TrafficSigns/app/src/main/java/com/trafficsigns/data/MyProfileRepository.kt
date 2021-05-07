package com.trafficsigns.data

import androidx.lifecycle.LiveData

class MyProfileRepository(private val myProfileDao: MyProfileDao) {

    val getMyProfile: LiveData<MyProfile> =  myProfileDao.getProfile()


    suspend fun addProfile(profile: MyProfile){
        myProfileDao.addProfile(profile)
    }

    suspend fun updateProfile(profile: MyProfile) {
        myProfileDao.updateProfile(profile)
    }
}