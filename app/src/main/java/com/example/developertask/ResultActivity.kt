package com.example.developertask

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.adView


class ResultActivity : AppCompatActivity() {

    private lateinit var repository: Repository
    private lateinit var view: View
    private var name: String?=""
    private var level:String? =""
    private  var value: String=""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        repository=Repository(applicationContext)
        repository.setStatusBarColor(this, R.drawable.toolbar_bg)


        //Strict mode
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //Initialize ad
        initAd()

        //Get data
        getIntents()


        //Click Listeners
        share.setOnClickListener(View.OnClickListener {
            repository.getAndShareScreenshot(view)

        })
        rate.setOnClickListener(View.OnClickListener {
            repository.rateUs()

        })

        backResult.setOnClickListener(View.OnClickListener {finish()
        })

    }

    private fun initAd() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        view = window.decorView.rootView
        Log.e("View","mcadnc$view")

    }

    private fun getIntents() {
        val i = intent
        if (i.hasExtra("value")) {
            value= i.extras?.getString("value").toString()
            val ss1 = SpannableString(value.substring(0,value.indexOf(".")))
            ss1.setSpan(RelativeSizeSpan(6f), 0, ss1.length,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set size
            val ss2 = SpannableString(value.substring(value.indexOf("."),value.length))
            ss2.setSpan(RelativeSizeSpan(3f), 0, ss2.length,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // set size
            calculationNumber.text = TextUtils.concat(ss1,ss2)
        }
        if(i.hasExtra("level")){

            level=i.extras?.getString("level")
            name=i.extras?.getString("name")

            calculationText.text = "Hello $name. You are $level"




        }

    }


}
