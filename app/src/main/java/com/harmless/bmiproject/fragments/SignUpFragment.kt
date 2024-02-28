package com.harmless.bmiproject.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.harmless.bmiproject.MainActivity
import com.harmless.bmiproject.R
import com.harmless.bmiproject.databinding.FragmentSignInBinding
import com.harmless.bmiproject.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var sAuth: FirebaseAuth

    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEditText:EditText
    private lateinit var confirmPasswordEditText:EditText

    companion object{
        private val TAG = "SignUpFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        init()
        return binding.root

    }

    private fun init(){

        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        usernameEditText = binding.usernameEditText
        confirmPasswordEditText = binding.confirmPasswordEditText

        sAuth = FirebaseAuth.getInstance()

        binding.registerTxt.setOnClickListener {
            val signInFragment = SignInFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.signIn_container, signInFragment)
                .addToBackStack(null)
                .commit()
        }

        val signUpButton = binding.submitBtn
        signUpButton.setOnClickListener {
            if(validations()){
                SignUpUser()
            }
        }




    }


    private fun validations():Boolean{
        var isValid = true
        if(usernameEditText.text.isEmpty()){
            isValid = false
        }
        if(emailEditText.text.isEmpty()){
            isValid = false
        }
        if(passwordEditText.text.isEmpty()){
            isValid = false
        }
        if(confirmPasswordEditText.text.isEmpty()){
            isValid = false

        }
        else{

            if(confirmPasswordEditText.text.toString() != passwordEditText.text.toString()){
                isValid = false
                confirmPasswordEditText.setText("")
                Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_SHORT).show()


            }
        }

        return isValid
    }

    fun SignUpUser() {
        Log.d(TAG, "SignUpUser: Working")
        try {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            sAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
                    Log.d(TAG, "onComplete: create")
                    if (task.isSuccessful) {
                        Log.d(TAG, "is successful")
                        val toMainView = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(toMainView)

                    } else {
                        Toast.makeText(requireActivity(), "Login failed", Toast.LENGTH_LONG)
                            .show()
                    }
                })

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "error", Toast.LENGTH_LONG).show()
        }
    }

    }