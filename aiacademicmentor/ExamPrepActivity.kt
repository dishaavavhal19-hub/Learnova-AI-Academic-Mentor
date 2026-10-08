package com.example.aiacademicmentor

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ExamPrepActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvExamStatus: TextView
    private lateinit var tvPriorityTopics: TextView
    private lateinit var tvRevisionPlan: TextView
    private lateinit var tvMockTest: TextView
    private lateinit var tvOverall: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exam_prep)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvExamStatus =
            findViewById(R.id.tvExamStatus)

        tvPriorityTopics =
            findViewById(R.id.tvPriorityTopics)

        tvRevisionPlan =
            findViewById(R.id.tvRevisionPlan)

        tvMockTest =
            findViewById(R.id.tvMockTest)

        tvOverall =
            findViewById(R.id.tvOverall)

        val analyze =
            findViewById<Button>(R.id.btnAnalyze)

        val back =
            findViewById<Button>(R.id.btnBack)

        analyze.setOnClickListener {
            analyzePreparation()
        }

        back.setOnClickListener {
            finish()
        }

        analyzePreparation()
    }

    private fun analyzePreparation() {

        val userId =
            auth.currentUser?.uid

        if (userId == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        loadExam(userId)
    }

    private fun loadExam(userId: String) {

        db.collection("students")
            .document(userId)
            .collection("exams")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty()) {

                    tvExamStatus.text =
                        "Exam Status\n\nNo exam has been added yet."

                    loadPerformance(userId, 0)

                    return@addOnSuccessListener
                }

                val latestExam =
                    documents.documents.maxByOrNull {

                        it.getLong(
                            "timestamp"
                        ) ?: 0L
                    }

                if (latestExam == null) {

                    loadPerformance(userId, 0)

                    return@addOnSuccessListener
                }

                val subject =
                    latestExam.getString("subject")
                        ?: "Exam"

                val examTime =
                    latestExam.getLong(
                        "examTimeMillis"
                    ) ?: 0L

                val remaining =
                    examTime - System.currentTimeMillis()

                val days =
                    if (remaining > 0) {
                        TimeUnit.MILLISECONDS.toDays(
                            remaining
                        )
                    } else {
                        0
                    }

                tvExamStatus.text =
                    if (remaining > 0) {

                        "Exam Status\n\n" +
                                "$subject\n" +
                                "$days days remaining"

                    } else {

                        "Exam Status\n\n" +
                                "$subject\n" +
                                "Exam date has arrived or passed."
                    }

                loadPerformance(
                    userId,
                    days
                )
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Could not load exam data",
                    Toast.LENGTH_SHORT
                ).show()

                loadPerformance(
                    userId,
                    0
                )
            }
    }

    private fun loadPerformance(
        userId: String,
        daysRemaining: Long
    ) {

        db.collection("students")
            .document(userId)
            .collection("quizResults")
            .get()
            .addOnSuccessListener { documents ->

                var totalQuestions = 0
                var totalCorrect = 0

                val weakTopics =
                    mutableMapOf<String, Int>()

                for (document in documents) {

                    totalQuestions +=
                        document.getLong(
                            "totalQuestions"
                        )?.toInt() ?: 0

                    totalCorrect +=
                        document.getLong(
                            "score"
                        )?.toInt() ?: 0

                    val topics =
                        document.get("weakTopics")
                                as? List<*>

                    topics?.forEach { topic ->

                        val name =
                            topic.toString()

                        weakTopics[name] =
                            (weakTopics[name] ?: 0) + 1
                    }
                }

                val percentage =
                    if (totalQuestions > 0) {

                        (totalCorrect * 100) /
                                totalQuestions

                    } else {
                        0
                    }

                val priorityTopics =
                    weakTopics.entries
                        .sortedByDescending {
                            it.value
                        }
                        .take(5)
                        .map {
                            it.key
                        }

                displayPreparation(
                    percentage,
                    daysRemaining,
                    priorityTopics
                )
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Could not load performance",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayPreparation(
        percentage: Int,
        daysRemaining: Long,
        priorityTopics: List<String>
    ) {

        tvOverall.text =
            "Overall Performance\n\n" +
                    "Score: $percentage%"

        if (priorityTopics.isEmpty()) {

            tvPriorityTopics.text =
                "High-Priority Topics\n\n" +
                        "No weak topics detected yet.\n" +
                        "Complete more quizzes for better recommendations."

        } else {

            tvPriorityTopics.text =
                "High-Priority Topics\n\n" +
                        priorityTopics.joinToString(
                            separator = "\n"
                        ) {
                            "• $it"
                        }
        }

        val revisionMessage = when {

            daysRemaining <= 0 -> {

                "Focus on final revision.\n\n" +
                        "1. Review important concepts.\n" +
                        "2. Practice weak topics.\n" +
                        "3. Take a final mock test."
            }

            daysRemaining <= 3 -> {

                "Intensive revision recommended.\n\n" +
                        "1. Focus mainly on weak topics.\n" +
                        "2. Review important definitions.\n" +
                        "3. Take at least one mock test."
            }

            daysRemaining <= 7 -> {

                "Daily revision recommended.\n\n" +
                        "1. Study weak topics first.\n" +
                        "2. Practice medium and hard questions.\n" +
                        "3. Take a mock test before the exam."
            }

            else -> {

                "Create a regular study routine.\n\n" +
                        "1. Study weak topics.\n" +
                        "2. Practice quizzes regularly.\n" +
                        "3. Revise strong topics weekly.\n" +
                        "4. Complete mock tests."
            }
        }

        tvRevisionPlan.text =
            "Revision Plan\n\n" +
                    revisionMessage

        val mockTestMessage = when {

            percentage < 50 -> {

                "Mock Test Recommendation\n\n" +
                        "First revise your weak topics, " +
                        "then attempt an easy-level mock test."
            }

            percentage < 75 -> {

                "Mock Test Recommendation\n\n" +
                        "Attempt a medium-level mock test " +
                        "and focus on reducing mistakes."
            }

            else -> {

                "Mock Test Recommendation\n\n" +
                        "Attempt a hard-level mock test " +
                        "to challenge your preparation."
            }
        }

        tvMockTest.text =
            mockTestMessage

        savePreparationResult(
            percentage,
            daysRemaining,
            priorityTopics
        )
    }

    private fun savePreparationResult(
        percentage: Int,
        daysRemaining: Long,
        priorityTopics: List<String>
    ) {

        val userId =
            auth.currentUser?.uid
                ?: return

        val result = hashMapOf(
            "percentage" to percentage,
            "daysRemaining" to daysRemaining,
            "priorityTopics" to priorityTopics,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("students")
            .document(userId)
            .collection("examPreparation")
            .add(result)
    }
}