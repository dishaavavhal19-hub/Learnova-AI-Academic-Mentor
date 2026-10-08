package com.example.aiacademicmentor

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val showPasswordButton =
            findViewById<ImageButton>(R.id.btnShowPassword)

        var passwordVisible = false

        showPasswordButton.setOnClickListener {

            passwordVisible = !passwordVisible

            if (passwordVisible) {

                password.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()

                showPasswordButton.setImageResource(
                    R.drawable.ic_eye_off
                )

                showPasswordButton.contentDescription =
                    "Hide password"

            } else {

                password.transformationMethod =
                    PasswordTransformationMethod.getInstance()

                showPasswordButton.setImageResource(
                    R.drawable.ic_eye
                )

                showPasswordButton.contentDescription =
                    "Show password"
            }

            password.setSelection(password.text.length)
        }

        loginButton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            if (emailText.isEmpty()) {
                email.error = "Enter your email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (passwordText.isEmpty()) {
                password.error = "Enter your password"
                password.requestFocus()
                return@setOnClickListener
            }

            loginButton.isEnabled = false
            loginButton.text = "Logging in..."

            auth.signInWithEmailAndPassword(
                emailText,
                passwordText
            ).addOnCompleteListener { task ->

                loginButton.isEnabled = true
                loginButton.text = "Login ✨"

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            DashboardActivity::class.java
                        )
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}