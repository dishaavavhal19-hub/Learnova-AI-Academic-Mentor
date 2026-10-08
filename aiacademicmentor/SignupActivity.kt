package com.example.aiacademicmentor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)

        val createAccount =
            findViewById<Button>(R.id.btnCreateAccount)

        createAccount.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()

            if (nameText.isEmpty()) {
                name.error = "Enter your name"
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                email.error = "Enter email"
                return@setOnClickListener
            }

            if (passwordText.length < 6) {

                password.error =
                    "Password must contain at least 6 characters"

                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(
                emailText,
                passwordText
            ).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val userId =
                        auth.currentUser?.uid

                    if (userId != null) {

                        val student = hashMapOf(
                            "name" to nameText,
                            "email" to emailText,
                            "uid" to userId
                        )

                        db.collection("students")
                            .document(userId)
                            .set(student)
                    }

                    Toast.makeText(
                        this,
                        "Account created successfully",
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
                        task.exception?.message
                            ?: "Account creation failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}