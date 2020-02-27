package com.example.developertask

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var permission: Int = 0
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var repository: Repository


    @RequiresApi(VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = Repository(applicationContext)
        repository.setStatusBarColor(this, R.drawable.toolbar_bg)

        //Permissions
        requestPermission()

        //Initialize Ad and Views
        setPickersRange()
        initializeAd()


        //Click Listeners
        calculateButton.setOnClickListener(View.OnClickListener {
            if (permission > 0) {
                if (validation()) {

                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()

                    } else {
                        repository.showBMIResult(
                            heightPicker.value.toString(),
                            weightPicker.value.toString(),
                            name.text.toString()
                        )


                    }
                }


            } else {
                reRequestPermission()

            }


        })

        back.setOnClickListener(View.OnClickListener { super.onBackPressed() })


    }

    private fun setPickersRange() {

        weightPicker.minValue = 0
        weightPicker.maxValue = 500
        weightPicker.value = 60

        heightPicker.minValue = 0
        heightPicker.maxValue = 500
        heightPicker.value = 100

        genderPicker.minValue = 0
        genderPicker.maxValue = 1
        genderPicker.displayedValues = arrayOf("Male", "Female")

    }


    @RequiresApi(api = VERSION_CODES.M)
    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("asdfkzmkrmwf", "requestPermission: ")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                12
            )
        } else {
            permission++
        }
    }


    @RequiresApi(VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            12 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    permission++
                    Log.v("req", "granted")
                } else {
                    val beforeClickPermissionRat =
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (beforeClickPermissionRat) {
                        reRequestPermission()
                    } else {
                        goToSettings()
                        Log.e("rrequespermission", "requestPermission: ")
                    }
                }
                return
            }
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    fun goToSettings() {
        Log.e("vdsjuk", "onRequestPermissionsResult: ")
        val alertDialogBuilder =
            AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Permissions Required")
            .setMessage(
                "You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them."
            )
            .setPositiveButton(
                "Settings"
            ) { dialog, which ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> }
            .setCancelable(false)
            .create()
            .show()
    }

    fun reRequestPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("You need to give permission to access storage in order to work this feature.")
        builder.setNegativeButton(
            "CANCEL"
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setPositiveButton(
            "GIVE PERMISSION"
        ) { dialogInterface, i ->
            dialogInterface.dismiss()
            // Show permission request popup
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                12
            )
        }
        builder.show()
    }

    fun initializeAd() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(applicationContext)
        mInterstitialAd.adUnitId = getString(R.string.interstitialTestUnitId)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() { // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) { // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() { // Code to be executed when the ad is displayed.
            }

            override fun onAdClicked() { // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() { // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                repository.showBMIResult(
                    heightPicker.value.toString(),
                    weightPicker.value.toString(),
                    name.text.toString()
                )
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }


    }

    private fun validation(): Boolean {
        if (name.text.trim().isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter name",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (heightPicker.value == 0 || weightPicker.value == 0) {
            Toast.makeText(
                applicationContext,
                "Please select valid value for Weight and Height",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

}
