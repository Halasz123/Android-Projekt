package com.trafficsigns.data.database.repository

import androidx.lifecycle.LiveData
import com.trafficsigns.data.database.dao.MyProfileDao
import com.trafficsigns.data.dataclass.MyProfile

class MyProfileRepository(private val myProfileDao: MyProfileDao) {

    val getMyProfile: LiveData<MyProfile> =  myProfileDao.getProfile()


    suspend fun addProfile(profile: MyProfile){
        myProfileDao.addProfile(profile)
    }

    suspend fun updateProfile(profile: MyProfile) {
        myProfileDao.updateProfile(profile)
    }
}