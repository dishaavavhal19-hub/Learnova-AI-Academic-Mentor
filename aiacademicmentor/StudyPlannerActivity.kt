package com.example.aiacademicmentor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudyPlannerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_study_planner)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val subject =
            findViewById<EditText>(R.id.etSubject)

        val examDate =
            findViewById<EditText>(R.id.etExamDate)

        val studyHours =
            findViewById<EditText>(R.id.etStudyHours)

        val weakTopic =
            findViewById<EditText>(R.id.etWeakTopic)

        val generatePlan =
            findViewById<Button>(R.id.btnGeneratePlan)

        val studyPlan =
            findViewById<TextView>(R.id.tvStudyPlan)

        val back =
            findViewById<Button>(R.id.btnBack)

        generatePlan.setOnClickListener {

            val subjectText =
                subject.text.toString().trim()

            val examDateText =
                examDate.text.toString().trim()

            val studyHoursText =
                studyHours.text.toString().trim()

            val weakTopicText =
                weakTopic.text.toString().trim()

            if (subjectText.isEmpty()) {
                subject.error = "Enter subject"
                return@setOnClickListener
            }

            if (examDateText.isEmpty()) {
                examDate.error = "Enter exam date"
                return@setOnClickListener
            }

            if (studyHoursText.isEmpty()) {
                studyHours.error = "Enter study hours"
                return@setOnClickListener
            }

            val hours = studyHoursText.toIntOrNull()

            if (hours == null || hours <= 0) {
                studyHours.error = "Enter valid study hours"
                return@setOnClickListener
            }

            val plan = generateStudyPlan(
                subjectText,
                examDateText,
                hours,
                weakTopicText
            )

            studyPlan.text = plan

            saveStudyPlan(
                subjectText,
                examDateText,
                hours,
                weakTopicText,
                plan
            )
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun generateStudyPlan(
        subject: String,
        examDate: String,
        hours: Int,
        weakTopic: String
    ): String {

        val weakSection =
            if (weakTopic.isEmpty()) {
                "No specific weak topic entered."
            } else {
                "Priority weak topic: $weakTopic"
            }

        return """
            STUDY PLAN

            Subject: $subject
            Exam Date: $examDate
            Daily Study Time: $hours hour(s)

            $weakSection

            ─────────────────────

            DAY 1
            • Study $subject fundamentals
            • Review important definitions
            • Practice basic questions

            DAY 2
            • Study important concepts
            • Make short revision notes
            • Solve practice questions

            DAY 3
            • Focus on difficult topics
            • Practice previous questions
            • Review mistakes

            DAY 4
            • Revise $weakTopic
            • Solve medium-level questions
            • Review important formulas/concepts

            DAY 5
            • Complete full revision
            • Take a practice test
            • Review incorrect answers

            FINAL REVISION
            • Revise important topics
            • Focus on weak areas
            • Take a mock test before the exam

            Keep studying consistently every day!
        """.trimIndent()
    }

    private fun saveStudyPlan(
        subject: String,
        examDate: String,
        hours: Int,
        weakTopic: String,
        plan: String
    ) {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            return
        }

        val studyPlan = hashMapOf(
            "subject" to subject,
            "examDate" to examDate,
            "studyHoursPerDay" to hours,
            "weakTopic" to weakTopic,
            "plan" to plan,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("students")
            .document(userId)
            .collection("studyPlans")
            .add(studyPlan)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Study plan saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Could not save study plan",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}