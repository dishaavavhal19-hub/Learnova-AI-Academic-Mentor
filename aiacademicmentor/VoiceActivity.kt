package com.example.aiacademicmentor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class VoiceActivity : AppCompatActivity() {

    private lateinit var question: EditText
    private lateinit var status: TextView
    private lateinit var answer: TextView

    private val speechLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val data = result.data

                val results =
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                if (!results.isNullOrEmpty()) {

                    question.setText(results[0])

                    status.text =
                        "Question detected successfully"
                }
            }
        }

    private val microphonePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startSpeechRecognition()
            } else {

                Toast.makeText(
                    this,
                    "Microphone permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_voice)

        question =
            findViewById(R.id.etVoiceQuestion)

        status =
            findViewById(R.id.tvStatus)

        answer =
            findViewById(R.id.tvVoiceAnswer)

        val speakButton =
            findViewById<Button>(R.id.btnSpeak)

        val askAIButton =
            findViewById<Button>(R.id.btnAskAI)

        val backButton =
            findViewById<Button>(R.id.btnBack)

        speakButton.setOnClickListener {

            checkMicrophonePermission()
        }

        askAIButton.setOnClickListener {

            val questionText =
                question.text.toString().trim()

            if (questionText.isEmpty()) {

                question.error =
                    "Please speak or type a question"

                return@setOnClickListener
            }

            answer.text =
                generateDemoAnswer(questionText)

            status.text =
                "AI answer generated"
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun checkMicrophonePermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            startSpeechRecognition()

        } else {

            microphonePermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    private fun startSpeechRecognition() {

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak your academic question"
        )

        try {

            status.text =
                "Listening..."

            speechLauncher.launch(intent)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Speech recognition is not available",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun generateDemoAnswer(
        question: String
    ): String {

        return when {

            question.contains(
                "normalization",
                ignoreCase = true
            ) -> {

                "Normalization is a database design technique " +
                        "used to reduce data redundancy and improve " +
                        "data integrity. Common normal forms include " +
                        "1NF, 2NF and 3NF."
            }

            question.contains(
                "kotlin",
                ignoreCase = true
            ) -> {

                "Kotlin is a modern programming language " +
                        "developed by JetBrains and officially supported " +
                        "for Android development."
            }

            question.contains(
                "database",
                ignoreCase = true
            ) -> {

                "A database is an organized collection of data. " +
                        "It allows applications to store, retrieve and " +
                        "manage information efficiently."
            }

            question.contains(
                "java",
                ignoreCase = true
            ) -> {

                "Java is an object-oriented programming language " +
                        "widely used for software and Android development."
            }

            else -> {

                "You asked:\n\n$question\n\n" +
                        "This is the demo AI response. " +
                        "The real AI service will be connected later."
            }
        }
    }
}