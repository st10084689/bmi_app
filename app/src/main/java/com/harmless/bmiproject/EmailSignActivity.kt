package com.harmless.bmiproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.harmless.bmiproject.databinding.ActivityEmailSignBinding
import com.harmless.bmiproject.fragments.SignInFragment


private lateinit var binding: ActivityEmailSignBinding
class EmailSignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        //initialize a fragment in the frame layout
        val signInFragment = SignInFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.signIn_container, signInFragment)
            .commit()

    }


}