package com.example.aiacademicmentor

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        val mentorButton = findViewById<LinearLayout>(R.id.btnMentor)
        val quizButton = findViewById<LinearLayout>(R.id.btnQuiz)
        val plannerButton = findViewById<LinearLayout>(R.id.btnPlanner)
        val countdownButton = findViewById<LinearLayout>(R.id.btnCountdown)
        val performanceButton = findViewById<LinearLayout>(R.id.btnPerformance)
        val voiceButton = findViewById<LinearLayout>(R.id.btnVoice)
        val adaptiveButton = findViewById<LinearLayout>(R.id.btnAdaptive)
        val examButton = findViewById<LinearLayout>(R.id.btnExam)
        val logoutButton = findViewById<TextView>(R.id.btnLogout)

        mentorButton.setOnClickListener {
            startActivity(Intent(this, MentorActivity::class.java))
        }

        quizButton.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        plannerButton.setOnClickListener {
            startActivity(Intent(this, StudyPlannerActivity::class.java))
        }

        countdownButton.setOnClickListener {
            Toast.makeText(this, "Countdown button clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CountdownActivity::class.java))
        }

        performanceButton.setOnClickListener {
            startActivity(Intent(this, PerformanceActivity::class.java))
        }

        voiceButton.setOnClickListener {
            startActivity(Intent(this, VoiceActivity::class.java))
        }

        adaptiveButton.setOnClickListener {
            startActivity(Intent(this, AdaptiveActivity::class.java))
        }

        examButton.setOnClickListener {
            startActivity(Intent(this, ExamPrepActivity::class.java))
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}