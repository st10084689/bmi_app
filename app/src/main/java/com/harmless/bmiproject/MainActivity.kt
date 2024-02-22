package com.harmless.bmiproject

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.harmless.bmiproject.databinding.ActivityMainBinding
import java.lang.Math.pow


class MainActivity : AppCompatActivity() {
    companion object{
        val TAG = "MainActivity"
    }
    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var binding : ActivityMainBinding

    //declaring the edit text values
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var ageEditText: EditText

    //declaring the bmi text view
    private lateinit var bmiTextView: TextView

    private lateinit var submitBtn:CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        //for the progress bar
//        val bmiProgressBar = binding.bmiProgressBar
//        val bmiValue = 68
//        val progress = (bmiValue/30)*100
//        bmiProgressBar.progress = progress

        //initialising the add
        var adRequest = AdRequest.Builder().build()


        lateinit var selectedUnit: String
         // the edittext bmi values

        heightEditText = binding.heightEditText
        weightEditText = binding.weightEditText
        ageEditText = binding.ageEditText

        bmiTextView = binding.bmiScale


        //loading an advertisement
        MobileAds.initialize(this){}
       val bannerAdView = binding.adView
        InterstitialAd.load(this,"ca-app-pub-5672195872456028/2394904449", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        bannerAdView.loadAd(adRequest)

        bannerAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad: ${adError}")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.d(TAG, "onAdImpression: $")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }//method end

        val spinner: Spinner = binding.unitSpinner

        ArrayAdapter.createFromResource(
            this,
            R.array.units,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnit = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        val bmiOutput = binding.bmiScaleNumber
        //setting up the submit button
         submitBtn = binding.submitBtn
        submitBtn.setOnClickListener {


            val valid = validation()
            if(valid){
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
             var bmi =  calcBmi(weightEditText.text.toString().toInt(), selectedUnit, ageEditText.text.toString().toInt(),heightEditText.text.toString().toInt())
                 bmiOutput.text = roundToTwoDecimals(bmi) + " BMI"
                bmiValue(bmi)
                setArrowPosition(bmi)

            }
            else{

            }

        }

    }

    fun roundToTwoDecimals(number: Double): String {
        return "%.1f".format(number)
    }

    private fun calcBmi(weight:Int, unit:String, age:Int, height:Int):Double{
        var bmi = 0.0
        when(unit){
            "Metric" ->{
                Log.d(TAG, "calcBmi: height $height")
                Log.d(TAG, "calcBmi: weight $weight")
                bmi = weight / pow(height.toDouble() / 100, 2.0)
            }
            "Imperial" ->{
                Log.d(TAG, "calcBmi: height $height")
                Log.d(TAG, "calcBmi: weight $weight")
                 bmi = (weight * 703) / (height * height).toDouble()
            }
        }

        return bmi
    }

    private fun validation(): Boolean {
        var isValid = true
        val heightText = binding.heightText
        val ageText = binding.ageText
        val weightText = binding.weightText

       val isDarkMode =  isDarkModeOn()

        if (heightEditText.text == null || heightEditText.text.isEmpty()) {
            isValid = false
            val color = getColor(R.color.top_obese)
            heightText.setTextColor(color)
        } else {
            if(isDarkMode){
                heightText.setTextColor(getColor(R.color.white))
            }
            else {
                heightText.setTextColor(getColor(R.color.black))
            }
        }

        if (ageEditText.text == null || ageEditText.text.isEmpty()) {
            isValid = false
            val color = getColor(R.color.top_obese)
            ageText.setTextColor(color)
        } else {
            if(isDarkMode) {
                ageText.setTextColor(getColor(R.color.white))
            }
            else {
                ageText.setTextColor(getColor(R.color.black))
            }
        }

        if (weightEditText.text == null || weightEditText.text.isEmpty()) {
            isValid = false
            val color = getColor(R.color.top_obese)
            weightText.setTextColor(color)
        } else {
            if(isDarkMode) {
                weightText.setTextColor(getColor(R.color.white))
            }
            else {
                weightText.setTextColor(getColor(R.color.black))
            }
        }

        return isValid
    }

    private fun bmiValue(bmi: Double) {
        val relativeLayout = binding.topRelative
        val gradientDrawable = relativeLayout.background as GradientDrawable
        val bottomGradientRel = binding.bottomRelGradient
        val bottomGradient = bottomGradientRel.background as GradientDrawable

        if (bmi >= 40.0) { // Obesity class III at the top
            bmiTextView.text = "Obesity class III"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            relativeLayout.invalidate()

            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_obese),
                ContextCompat.getColor(this, R.color.white)
            )

            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))

        } else if (35.0 <= bmi && bmi <= 39.9) {
            bmiTextView.text = "Obesity class II"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_obese),
                ContextCompat.getColor(this, R.color.white)
            )

            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (30.0 <= bmi && bmi <= 34.9) {  // And here
            bmiTextView.text = "Obesity class I"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_obese),
                ContextCompat.getColor(this, R.color.white)
            )

            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (25.0 <= bmi && bmi <= 29.9) {
            bmiTextView.text = "Pre-Obesity"

                        gradientDrawable.colors = intArrayOf(
                        ContextCompat.getColor(this, R.color.top_preobese),
                ContextCompat.getColor(this, R.color.bottom_preobese)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_preobese),
                ContextCompat.getColor(this, R.color.white)
            )

            val colorResourceId: Int = R.color.top_preobese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))

        } else if (18.5 <= bmi && bmi <= 24.9) {
            bmiTextView.text = "Normal"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_normal),
                ContextCompat.getColor(this, R.color.bottom_normal)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_normal),
                ContextCompat.getColor(this, R.color.white)
            )

            val colorResourceId: Int = R.color.top_normal
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (bmi < 18.5) {
            bmiTextView.text = "Underweight"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_underweight),
                ContextCompat.getColor(this, R.color.bottom_underweight)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_underweight),
                ContextCompat.getColor(this, R.color.white)
            )
            val colorResourceId: Int = R.color.top_underweight
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else {
            bmiTextView.text = ""
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            bottomGradient.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.bottom_obese),
                ContextCompat.getColor(this, R.color.white)
            )
        }
    }

//    private fun positiongraphArrow(bmiValue:Double){
//        val relativeLayout = binding.bmiGraph
//        var widthWithMargins: Int = 500
//        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//
//            override fun onGlobalLayout() {
//                // The width of the RelativeLayout after accounting for the margins
//                widthWithMargins = relativeLayout.width
//                // Remove the listener to avoid multiple calls
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    relativeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                } else {
//                    relativeLayout.viewTreeObserver.removeGlobalOnLayoutListener(this)
//                }
//                // Log the width or use it as needed
//                Log.d("RelativeLayoutWidth", "Width with margins: $widthWithMargins")
//            }
//        })
//
//        val maxArrow = 40
//        val minArrow = 0
//
//       var percentage = (bmiValue/ maxArrow) *100
//        Log.d(TAG, "positiongraphArrow: $percentage")
//        Log.d(TAG, "positiongraphArrow: width $widthWithMargins")
//        var positionValue = widthWithMargins * percentage
//        Log.d(TAG, "positiongraphArrow: positionvalue $positionValue")
//        val bmiArrow = binding.arrow
//        val params = bmiArrow.layoutParams as ViewGroup.MarginLayoutParams
//
//
//        params.setMargins(positionValue.toInt(),  0,  0,  0)
//        Log.d(TAG, "positiongraphArrow: percentage is $percentage")
//    }

    private fun setArrowPosition(bmi: Double) {
        val relativeLayout = binding.bmiGraph
        val arrowImageView = binding.arrow

        val bmiGraphWidth: Int = relativeLayout.width

        // Calculate the percentage of BMI relative to the maximum value (45)
        val percentage = (bmi / 45) * 100

        // Calculate the position to set the arrow based on the percentage
        val quarterWay = (bmiGraphWidth / 4).toFloat()
        val arrowPosition = when {
            bmi <= 18.5 -> (quarterWay * (bmi / 18.5)).toInt() // Less than or equal to 18.5
            bmi <= 25 -> (quarterWay + (quarterWay * ((bmi - 18.5) / (25 - 18.5)))).toInt() // Between 18.6 and 25
            bmi <= 30 -> (2 * quarterWay + (quarterWay * ((bmi - 25) / (30 - 25)))).toInt() // Between 25.1 and 30
            bmi <= 45 -> (3 * quarterWay + (quarterWay * ((bmi - 30) / (45 - 30)))).toInt() // Between 30.1 and 45
            else -> bmiGraphWidth // Greater than 45, stand at the full way point
        }

        // Set the margin of the arrow ImageView to move it along the BMI graph
        val params = arrowImageView.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(
            arrowPosition - arrowImageView.width / 2,
            0,
            0,
            0
        ) // Centering the arrow
        arrowImageView.layoutParams = params
    }
    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}