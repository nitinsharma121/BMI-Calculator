package com.example.developertask

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.text.DecimalFormat


open class Repository(val context: Context) {


    private lateinit var level: String
    private var value: Float = 0.0f


    //Calculate BMI
    fun showBMIResult(heightStr: String, weightStr: String, name: String) {
        value = calculateBMI(
            heightStr,
            weightStr
        )

        level = displayBMI(value)
        context.startActivity(
            Intent(
                context,
                ResultActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("value", value.toString()).putExtra("level", level).putExtra(
                    "name",
                    name
                )
        )
    }


    private fun calculateBMI(heightStr: String, weightStr: String): Float {
        val heightValue = heightStr.toFloat() / 100
        val weightValue = weightStr.toFloat()
        val bmi = weightValue / (heightValue * heightValue)
        return DecimalFormat("##.##").format(bmi).toFloat()

    }


    fun displayBMI(bmi: Float): String {
        var bmiLabel: String
        if (java.lang.Float.compare(bmi, 15f) <= 0) {
            bmiLabel = context.getString(R.string.very_severely_underweight)
        } else if (java.lang.Float.compare(bmi, 15f) > 0 && java.lang.Float.compare(
                bmi,
                16f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.severely_underweight)
        } else if (java.lang.Float.compare(bmi, 16f) > 0 && java.lang.Float.compare(
                bmi,
                18.5f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.underweight)
        } else if (java.lang.Float.compare(bmi, 18.5f) > 0 && java.lang.Float.compare(
                bmi,
                25f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.normal)
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.overweight)
        } else if (java.lang.Float.compare(bmi, 30f) > 0 && java.lang.Float.compare(
                bmi,
                35f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.obese_class_i)
        } else if (java.lang.Float.compare(bmi, 35f) > 0 && java.lang.Float.compare(
                bmi,
                40f
            ) <= 0
        ) {
            bmiLabel = context.getString(R.string.obese_class_ii)
        } else {
            bmiLabel = context.getString(R.string.obese_class_iii)
        }
        Log.e("kac", "V " + bmiLabel)
        return bmiLabel
    }


    //Share SCreenshoots
    open fun getAndShareScreenshot(view: View) {

        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        store(returnedBitmap, "BMI.png")
    }

    open fun store(bm: Bitmap, fileName: String?) {
        val dirPath: String =
            Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Screenshots"
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dirPath, fileName)
        try {

            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            Log.e("exception", "klls")

            shareImage(file)
        } catch (e: Exception) {
            Log.e("exception", e.message)
            e.printStackTrace()
        }
    }

    open fun shareImage(file: File) {
        val uri: Uri = Uri.fromFile(file)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        try {
            context.startActivity(Intent.createChooser(intent, "Share Screenshot").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    open fun rateUs() {
        val uri = Uri.parse("market://details?id=" + context.getPackageName())
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor(view: Activity, cardBg: Int) {
        view.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        view.window.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
        view.window.navigationBarColor =
            ContextCompat.getColor(context, android.R.color.transparent)
        view.window.setBackgroundDrawableResource(cardBg)
    }

}