package com.harmless.bmiproject

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.harmless.bmiproject.databinding.ActivityMainBinding
import com.harmless.bmiproject.models.TrackModel
import java.lang.Math.pow
import java.util.Date


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        val TAG = "MainActivity"
    }
    private var mInterstitialAd: InterstitialAd? = null

    private var TrackerInterstitialAd: InterstitialAd? = null

    private val doubleBackToExitPressedOnce = false

    private lateinit var binding : ActivityMainBinding

    //declaring the edit text values
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var ageEditText: EditText

    //declaring the bmi text view
    private lateinit var bmiTextView: TextView
    private lateinit var submitBtn:CardView
    private lateinit var drawerLayout:DrawerLayout

    //the navigation view
    private lateinit var headerImage:ImageView
    private lateinit var  headerTextView:TextView

    private  var i :Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(){
        initNavagationView()
        isDarkModeOn()

        //determining whether the arrow should show
        if(binding.bmiScaleNumber.text.isNotEmpty()){
            binding.arrow.visibility = View.VISIBLE
        }

        binding.sliderBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //initialising the ad
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
        bannerAdView.loadAd(adRequest)
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

        InterstitialAd.load(this,"ca-app-pub-5672195872456028/2599305617", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                TrackerInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                TrackerInterstitialAd = interstitialAd


            }


        })

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

                when(selectedUnit){
                    "Imperial"->{
                        heightEditText.hint = "eg. 4.5 feet"
                        weightEditText.hint = "eg. 150 pounds"
                    }
                    "Metric"->{
                        heightEditText.hint = "eg. 163 cm"
                        weightEditText.hint = "eg. 60 kgs"
                    }
                }
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
             var bmi =  calcBmi(weightEditText.text.toString().toDouble(), selectedUnit, heightEditText.text.toString().toDouble())
                 bmiOutput.text = roundToTwoDecimals(bmi) + " BMI"
                bmiValue(bmi)
                if(binding.bmiScaleNumber.text.isNotEmpty()){
                    binding.arrow.visibility = View.VISIBLE
                    setArrowPosition(bmi)
                }

                writeToDatabase(weightEditText.text.toString(), bmi,ageEditText.text.toString(), selectedUnit)
            }
            else{

            }

        }

    }

    fun roundToTwoDecimals(number: Double): String {
        return "%.1f".format(number)
    }

    private fun calcBmi(weight:Double, unit:String, height:Double):Double{
        var bmi = 0.0
        when(unit){
            "Metric" ->{
                Log.d(TAG, "calcBmi: height $height")
                Log.d(TAG, "calcBmi: weight $weight")
                bmi = weight / pow(height.toDouble() / 100, 2.0)
                Log.d(TAG, "calcBmi: Metric")
            }
            "Imperial" ->{
                Log.d(TAG, "calcBmi: height $height")
                Log.d(TAG, "calcBmi: weight $weight")
                val heightInches = height * 12
                 bmi = (weight / pow(heightInches, 2.0)) * 703
                Log.d(TAG, "calcBmi: Metric")
            }
        }

        return bmi
    }//method ends

    private fun validation(): Boolean {//method to validation the values
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
    }//method end

    private fun bmiValue(bmi: Double) {//method to move the arrow base on where the bmi value is
        val relativeLayout = binding.topRelative
        val gradientDrawable = relativeLayout.background as GradientDrawable
        val bottomGradientRel = binding.bottomRelGradient
        val bottomGradient = bottomGradientRel.background as GradientDrawable

        if (bmi >= 40.0) {
            bmiTextView.text = "Obesity III"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            relativeLayout.invalidate()
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.white)
                )
            }


            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))

        } else if (35.0 <= bmi && bmi <= 39.9) {
            bmiTextView.text = "Obesity II"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (30.0 <= bmi && bmi <= 34.9) {  // And here
            bmiTextView.text = "Obesity I"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_obese),
                ContextCompat.getColor(this, R.color.bottom_obese)
            )
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_obese),
                    ContextCompat.getColor(this, R.color.white)
                )
            }
            val colorResourceId: Int = R.color.top_obese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (25.0 <= bmi && bmi <= 29.9) {
            bmiTextView.text = "Pre-Obesity"

                        gradientDrawable.colors = intArrayOf(
                        ContextCompat.getColor(this, R.color.top_preobese),
                ContextCompat.getColor(this, R.color.bottom_preobese)
            )
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_preobese),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_preobese),
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            val colorResourceId: Int = R.color.top_preobese
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))

        } else if (18.5 <= bmi && bmi <= 24.9) {
            bmiTextView.text = "Normal"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_normal),
                ContextCompat.getColor(this, R.color.bottom_normal)
            )
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_normal),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_normal),
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            val colorResourceId: Int = R.color.top_normal
            submitBtn.setCardBackgroundColor(ContextCompat.getColor(this, colorResourceId))
        } else if (bmi < 18.5) {
            bmiTextView.text = "Underweight"
            gradientDrawable.colors = intArrayOf(
                ContextCompat.getColor(this, R.color.top_underweight),
                ContextCompat.getColor(this, R.color.bottom_underweight)
            )
            if(isDarkModeOn()){
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_underweight),
                    ContextCompat.getColor(this, R.color.background_grey)
                )
            }
            else{
                bottomGradient.colors = intArrayOf(
                    ContextCompat.getColor(this, R.color.bottom_underweight),
                    ContextCompat.getColor(this, R.color.white)
                )
            }
            val colorResourceId: Int = R.color.bottom_underweight
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
    }//method end


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

    private fun initNavagationView(){

         drawerLayout = binding.navDrawer
        val navigationDrawer = binding.navView
        navigationDrawer.setNavigationItemSelectedListener(this)

        val headerView = navigationDrawer.inflateHeaderView(R.layout.navigation_header)
         headerImage = headerView.findViewById<ImageView>(R.id.imageView)
         headerTextView = headerView.findViewById<TextView>(R.id.nav_account_name)

        headerImage.setImageResource(R.drawable.baseline_account_circle_24)
        //getting the user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null || user.email.isNullOrEmpty()) {
            headerTextView.text = "Guest"
        } else {
            headerTextView.text = user.email
        }

    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun writeToDatabase(weight: String, bmi:Double,age:String, selectedUnit: String){
        val database = Firebase.database
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val email = user?.uid
        if(email!!.isNotEmpty()){
            val dbReference = database.getReference("bmi").child(uid!!)

            //getting the current date
            val currentDate: Date = Date()


            val bmi = TrackModel(weight, bmi, currentDate ,age,email,selectedUnit)

            //pushes to database
            dbReference.push().setValue(bmi)
        }
        else{

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> {

                val user = FirebaseAuth.getInstance().currentUser
                if (user == null || user.email.isNullOrEmpty()) {
                    Toast.makeText(this@MainActivity, "Login to Track your progress", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, TrackActivity::class.java)
                    startActivity(intent)
                    TrackerInterstitialAd?.show(this)
                }




                return true
            }
            R.id.nav_log_out->{

                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                Toast.makeText(this, "Signed Out", Toast.LENGTH_LONG).show()
                val toSignInActivity = Intent(this, SignInActivity::class.java)
                startActivity(toSignInActivity)

            }
        }
        return false
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        if (i == 1) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.i++
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ i = 0 }, 2000) // Reset the counter after 2 seconds

        // If you want to cancel the exit operation if user doesn't press back within 2 seconds,
        // you can uncomment the following lines.
        // Handler().postDelayed({
        //     doubleBackToExitPressedOnce = false
        // }, 2000)
    }
}