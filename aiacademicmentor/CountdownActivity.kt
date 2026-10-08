package com.example.aiacademicmentor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class CountdownActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvExamSubject: TextView
    private lateinit var tvDays: TextView
    private lateinit var tvHours: TextView
    private lateinit var tvMinutes: TextView
    private lateinit var tvSeconds: TextView

    private var examTimeMillis: Long = 0L

    private val handler = android.os.Handler(
        android.os.Looper.getMainLooper()
    )

    private val countdownRunnable = object : Runnable {

        override fun run() {

            updateCountdown()

            handler.postDelayed(
                this,
                1000
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_countdown)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val subject =
            findViewById<EditText>(R.id.etExamSubject)

        val examDate =
            findViewById<EditText>(R.id.etExamDate)

        val saveExam =
            findViewById<Button>(R.id.btnSaveExam)

        tvExamSubject =
            findViewById(R.id.tvExamSubject)

        tvDays =
            findViewById(R.id.tvDays)

        tvHours =
            findViewById(R.id.tvHours)

        tvMinutes =
            findViewById(R.id.tvMinutes)

        tvSeconds =
            findViewById(R.id.tvSeconds)

        val back =
            findViewById<Button>(R.id.btnBack)

        saveExam.setOnClickListener {

            val subjectText =
                subject.text.toString().trim()

            val dateText =
                examDate.text.toString().trim()

            if (subjectText.isEmpty()) {
                subject.error = "Enter exam subject"
                return@setOnClickListener
            }

            if (dateText.isEmpty()) {
                examDate.error = "Enter exam date"
                return@setOnClickListener
            }

            val dateFormat =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            dateFormat.isLenient = false

            try {

                val date =
                    dateFormat.parse(dateText)

                if (date == null) {
                    examDate.error = "Invalid date"
                    return@setOnClickListener
                }

                examTimeMillis =
                    date.time + TimeUnit.HOURS.toMillis(23) +
                            TimeUnit.MINUTES.toMillis(59) +
                            TimeUnit.SECONDS.toMillis(59)

                tvExamSubject.text =
                    "$subjectText Exam"

                handler.removeCallbacks(
                    countdownRunnable
                )

                handler.post(
                    countdownRunnable
                )

                saveExam(
                    subjectText,
                    dateText,
                    examTimeMillis
                )

            } catch (e: Exception) {

                examDate.error =
                    "Use format DD/MM/YYYY"
            }
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun updateCountdown() {

        if (examTimeMillis == 0L) {
            return
        }

        val remaining =
            examTimeMillis - System.currentTimeMillis()

        if (remaining <= 0) {

            tvDays.text = "EXAM DAY"
            tvHours.text = "0 HOURS"
            tvMinutes.text = "0 MINUTES"
            tvSeconds.text = "0 SECONDS"

            handler.removeCallbacks(
                countdownRunnable
            )

            return
        }

        val days =
            TimeUnit.MILLISECONDS.toDays(
                remaining
            )

        val hours =
            TimeUnit.MILLISECONDS.toHours(
                remaining
            ) % 24

        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(
                remaining
            ) % 60

        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(
                remaining
            ) % 60

        tvDays.text =
            "$days DAYS"

        tvHours.text =
            "$hours HOURS"

        tvMinutes.text =
            "$minutes MINUTES"

        tvSeconds.text =
            "$seconds SECONDS"
    }

    private fun saveExam(
        subject: String,
        date: String,
        examTime: Long
    ) {

        val userId =
            auth.currentUser?.uid

        if (userId == null) {
            return
        }

        val exam = hashMapOf(
            "subject" to subject,
            "examDate" to date,
            "examTimeMillis" to examTime,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("students")
            .document(userId)
            .collection("exams")
            .add(exam)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Exam saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Could not save exam",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroy() {

        handler.removeCallbacks(
            countdownRunnable
        )

        super.onDestroy()
    }
}