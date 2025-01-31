package com.bangkit.wellpredict.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bangkit.wellpredict.data.ResultState
import com.bangkit.wellpredict.data.model.User
import com.bangkit.wellpredict.databinding.ActivityLoginBinding
import com.bangkit.wellpredict.ui.ViewModelFactory
import com.bangkit.wellpredict.utils.AuthHelper


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        AuthHelper.observeLoginInputChanges(binding.btnLogin)
        setupLoginClickListener()
        registerOnClickHandler()
    }

    private fun setupLoginClickListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailInput.text.toString().trim()
            val password = binding.etPasswordInput.text.toString().trim()

            if (AuthHelper.isValidEmail(email) && AuthHelper.isValidPassword(password)) {
                viewModel.login(email, password).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                        }
                        is ResultState.Success -> {
                            val accessToken = result.data.data?.accesToken.toString()
                            val refreshToken = result.data.data?.refreshToken.toString()
                            viewModel.saveSession(User(email, accessToken, refreshToken))
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        }
                        is ResultState.Error -> {
                            val error = result.error.toString()
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun registerOnClickHandler(){
        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}