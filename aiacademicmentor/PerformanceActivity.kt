package com.example.aiacademicmentor

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerformanceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvOverallScore: TextView
    private lateinit var tvAttempts: TextView
    private lateinit var tvTotalQuestions: TextView
    private lateinit var tvCorrect: TextView
    private lateinit var tvWrong: TextView
    private lateinit var tvStrongTopics: TextView
    private lateinit var tvWeakTopics: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_performance)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvOverallScore =
            findViewById(R.id.tvOverallScore)

        tvAttempts =
            findViewById(R.id.tvAttempts)

        tvTotalQuestions =
            findViewById(R.id.tvTotalQuestions)

        tvCorrect =
            findViewById(R.id.tvCorrect)

        tvWrong =
            findViewById(R.id.tvWrong)

        tvStrongTopics =
            findViewById(R.id.tvStrongTopics)

        tvWeakTopics =
            findViewById(R.id.tvWeakTopics)

        val refresh =
            findViewById<Button>(R.id.btnRefresh)

        val back =
            findViewById<Button>(R.id.btnBack)

        loadPerformance()

        refresh.setOnClickListener {
            loadPerformance()
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun loadPerformance() {

        val userId = auth.currentUser?.uid

        if (userId == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        db.collection("students")
            .document(userId)
            .collection("quizResults")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    tvOverallScore.text =
                        "Overall Score: 0%"

                    tvAttempts.text =
                        "Quiz Attempts: 0"

                    tvTotalQuestions.text =
                        "Total Questions: 0"

                    tvCorrect.text =
                        "Correct Answers: 0"

                    tvWrong.text =
                        "Wrong Answers: 0"

                    tvStrongTopics.text =
                        "Strong Topics:\nNo quiz completed yet"

                    tvWeakTopics.text =
                        "Weak Topics:\nNo weak topics detected"

                    return@addOnSuccessListener
                }

                var totalQuestions = 0
                var totalCorrect = 0

                val weakTopicCount =
                    mutableMapOf<String, Int>()

                val strongTopicCount =
                    mutableMapOf<String, Int>()

                for (document in documents) {

                    val questions =
                        document.getLong("totalQuestions")
                            ?.toInt() ?: 0

                    val score =
                        document.getLong("score")
                            ?.toInt() ?: 0

                    totalQuestions += questions
                    totalCorrect += score

                    val weakTopics =
                        document.get("weakTopics")
                                as? List<*>

                    weakTopics?.forEach { topic ->

                        val topicName =
                            topic.toString()

                        weakTopicCount[topicName] =
                            (weakTopicCount[topicName] ?: 0) + 1
                    }
                }

                val totalWrong =
                    totalQuestions - totalCorrect

                val overallPercentage =
                    if (totalQuestions > 0) {
                        (totalCorrect * 100) /
                                totalQuestions
                    } else {
                        0
                    }

                for (topic in weakTopicCount.keys) {

                    val weakCount =
                        weakTopicCount[topic] ?: 0

                    if (weakCount == 0) {

                        strongTopicCount[topic] =
                            1
                    }
                }

                val weakTopics =
                    weakTopicCount
                        .entries
                        .sortedByDescending { it.value }
                        .take(5)
                        .map { it.key }

                val strongTopics =
                    if (overallPercentage >= 70) {
                        listOf(
                            "Good overall performance",
                            "Consistent quiz performance"
                        )
                    } else {
                        listOf(
                            "Topics with correct answers will improve"
                        )
                    }

                tvOverallScore.text =
                    "Overall Score: $overallPercentage%"

                tvAttempts.text =
                    "Quiz Attempts: ${documents.size()}"

                tvTotalQuestions.text =
                    "Total Questions: $totalQuestions"

                tvCorrect.text =
                    "Correct Answers: $totalCorrect"

                tvWrong.text =
                    "Wrong Answers: $totalWrong"

                tvStrongTopics.text =
                    "Strong Topics:\n" +
                            strongTopics.joinToString("\n") {
                                "✓ $it"
                            }

                tvWeakTopics.text =
                    if (weakTopics.isEmpty()) {
                        "Weak Topics:\n✓ None detected"
                    } else {
                        "Weak Topics:\n" +
                                weakTopics.joinToString("\n") {
                                    "⚠ $it"
                                }
                    }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}