package com.trafficsigns

import android.animation.AnimatorSet
import android.animation.ObjectAnimator.ofFloat
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.trafficsigns.data.*
import com.trafficsigns.databinding.SplashScreenBinding
import com.trafficsigns.ui.constants.Data
import com.trafficsigns.ui.constants.General
import com.trafficsigns.ui.constants.SharedPreference
import com.trafficsigns.ui.constants.ToastMessage
import com.trafficsigns.ui.fragments.network.TrafficSignMemoryCache
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule


const val PREFS_NAME = "MyPrefsFile"
const val SLASH_TAG = "Splash"

class SplashActivity: AppCompatActivity() {

    private lateinit var binding: SplashScreenBinding
    private var myData = ""
    private var trafficSigns: HashMap<String, ArrayList<TrafficSign>> = HashMap()
    private lateinit var mTrafficSignsCollectionViewModel: TrafficSignsCollectionViewModel
    private lateinit var mMyProfileViewModel: MyProfileViewModel
    private lateinit var internetErrorToast: Toast
    private lateinit var firstTimeSH: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen)
        mTrafficSignsCollectionViewModel = ViewModelProvider(this).get(
            TrafficSignsCollectionViewModel::class.java
        )
        mMyProfileViewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        internetErrorToast = Toast.makeText(this, ToastMessage.INTERNET, Toast.LENGTH_LONG)
        startDownloadProcess()
        animateLogo()

        firstTimeSH = getSharedPreferences(PREFS_NAME, 0)
        if (firstTimeSH.getBoolean(SharedPreference.FIRST_TIME_USE_KEY, true)) {
            Log.d(SLASH_TAG, "First time")
            createNullProfile()
            firstTimeSH.edit().putBoolean(SharedPreference.FIRST_TIME_USE_KEY, false).apply()
        }


    }

    private fun animateLogo() {
        val fromUp = ofFloat(binding.stopImageview, "translationY", -1500f, 50f).apply {
            duration = 2000
        }
        val toUp = ofFloat(binding.stopImageview, "translationY", 50f, -200f).apply {
            duration = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }
        val fromLeft = ofFloat(binding.warningImageview, "translationX", -1500f, 50f).apply {
            duration = 2000
        }
        val toLeft = ofFloat(binding.warningImageview, "translationX", 50f, -200f).apply {
            duration = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }
        val fromRight = ofFloat(binding.roundaboutImageview, "translationX", 1500f, -50f).apply {
            duration = 2000
        }
        val toRight = ofFloat(binding.roundaboutImageview, "translationX", -50f, 200f).apply {
            duration = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }
        val rotateStop = ofFloat(binding.stopImageview, "rotation", 0f, 360f).apply {
            duration = 700
            repeatCount = Animation.INFINITE
        }
        val rotateWarning = rotateStop.clone()
        rotateWarning.target = binding.warningImageview

        val rotateRound = rotateStop.clone()
        rotateRound.target = binding.roundaboutImageview

        AnimatorSet().apply {
            play(fromUp).with(rotateStop)
            play(fromLeft).with(rotateWarning)
            play(fromRight).with(rotateRound)
            play(toUp).after(fromUp)
            play(toLeft).after(fromLeft)
            play(toRight).after(fromRight)
            start()
        }
    }

    private fun startDownloadProcess() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                downloadDataBlocking()
            }
        }
    }

    private fun downloadDataBlocking(){
        val client = OkHttpClient()
        val request = Request.Builder().url(Data.URL).build()
        return client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                myData = response.body()?.string() ?: ""
                parseJson()
                goToMain()
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(SLASH_TAG, e.toString())
                internetErrorToast.show()
                startDownloadProcess()
            }
        })
    }

    private fun parseJson() {
        val gson = Gson()
        val type = object : TypeToken<MutableMap<String, TrafficSign>>() {}.type
        val list: MutableMap<String, TrafficSign> = gson.fromJson(myData, type)
        val classifierLabels = FileUtil.loadLabels(this, General.CLASSIFICATION_LABELS_FILE_NAME)
        val cache = TrafficSignMemoryCache.instance
        list.forEach {
            it.value.id = it.key
            if (classifierLabels.contains(it.key)){
                cache.cacheTrafficSign(it.value)
            }
            if (trafficSigns[it.value.group] != null) {
                trafficSigns[it.value.group]!!.add(it.value)
            }
            else {
                it.value.group?.let { it1 -> trafficSigns.put(it1, arrayListOf(it.value)) }
            }
        }
        trafficSigns.forEach {
            Log.d(SLASH_TAG, "${it.key} -- ${it.value}")
            mTrafficSignsCollectionViewModel.addTrafficSignsCollection(
                TrafficSignsCollection(it.key, it.value)
            )
        }

    }

    private fun createNullProfile() {
        val profile = MyProfile(0)
        mMyProfileViewModel.addMyProfile(profile)

    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        Timer().schedule(3000) {
            startActivity(intent)
            overridePendingTransition(R.anim.fade_out, R.anim.splash_anim)
            finish()
        }
    }
}
