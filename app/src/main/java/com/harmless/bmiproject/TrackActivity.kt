package com.harmless.bmiproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harmless.bmiproject.adapters.BmiGraphRecyclerViewAdapter
import com.harmless.bmiproject.adapters.TrackRecylerViewAdapter
import com.harmless.bmiproject.databinding.ActivityTrackBinding
import com.harmless.bmiproject.models.TrackModel


class TrackActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        private val TAG = "TrackActivity"
    }

    private lateinit var binding: ActivityTrackBinding

    private lateinit var trackRecycler:RecyclerView
    private lateinit var bmiGraph: RecyclerView

    private lateinit var drawerLayout: DrawerLayout
    private val  bmiEntries = mutableListOf<TrackModel>()

    private var TrackerInterstitialAd: InterstitialAd? = null


    //geting db
    private lateinit var bmiDb: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        InterstitialAd.load(
            this,
            "ca-app-pub-5672195872456028/6295842193",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    TrackerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    TrackerInterstitialAd = interstitialAd
                }
            }
        )
        init()
    }

    override fun onBackPressed() {
        val toMainActivity = Intent(applicationContext, MainActivity::class.java)
        startActivity(toMainActivity)
        TrackerInterstitialAd?.show(this)
    }

    private fun init(){

        initNavagationView()
        val drawerLayout = binding.drawerLayout

        var adRequest = AdRequest.Builder().build()

        MobileAds.initialize(this){}

        val bannerAdView = binding.adView
        val middleBanner = binding.adMiddleView

        bannerAdView.loadAd(adRequest)
        middleBanner.loadAd(adRequest)

        bannerAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
            }
            override fun onAdClosed() {
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.d(MainActivity.TAG, "onAdFailedToLoad: ${adError}")
            }
            override fun onAdImpression() {
            }
            override fun onAdLoaded() {
            }
            override fun onAdOpened() {
            }
        }


        binding.sliderBtn.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //init the recycler views
        trackRecycler = binding.bmiRecycler
        trackRecycler.layoutManager = LinearLayoutManager(this)

        bmiGraph = binding.bmiGraph
        bmiGraph.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)


        readDatabase()
    }

    private fun readDatabase() {
        val bmiDb = FirebaseDatabase.getInstance().reference

        // Getting the current user
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        bmiDb.child("bmi").child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bmiEntries.clear()

                for (bmiSnapshot in snapshot.children) {
                    val bmi = bmiSnapshot.getValue(TrackModel::class.java)
                    Log.d(TAG, "onDataChange: $bmi")
                    bmi?.let { bmiEntries.add(it) }
                }
                bmiEntries.sortByDescending { it.date }
                // Initialize RecyclerViews after retrieving data

                if(bmiEntries.isEmpty()){
                    binding.nullShow.visibility = View.VISIBLE
                }

                initTrackRecyclerView()
                initBmiGraphRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: $error")
            }
        })
    }

    private fun initTrackRecyclerView() {
        val adapter = TrackRecylerViewAdapter(this@TrackActivity)
        trackRecycler.adapter = adapter
        adapter.setData(bmiEntries)
    }

    private fun initBmiGraphRecyclerView() {
        val graphAdapter = BmiGraphRecyclerViewAdapter(this@TrackActivity)
        bmiGraph.adapter = graphAdapter
        graphAdapter.setData(bmiEntries)
    }

    private fun initNavagationView(){

        val navigationDrawer = binding.navView
        navigationDrawer.setNavigationItemSelectedListener(this)

        val headerView = navigationDrawer.inflateHeaderView(R.layout.navigation_header)
        val headerImage = headerView.findViewById<ImageView>(R.id.imageView)
        val headerTextView = headerView.findViewById<TextView>(R.id.nav_account_name)

        headerImage.setImageResource(R.drawable.baseline_account_circle_24)
        //getting the user
        val user = FirebaseAuth.getInstance().currentUser
        headerTextView.text = user?.email
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                TrackerInterstitialAd?.show(this)
                return true
            }
        }
        return false
    }

}
