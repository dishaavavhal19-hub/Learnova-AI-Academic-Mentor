package com.example.aiacademicmentor

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AdaptiveQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val difficulty: String
)

class AdaptiveActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var spinnerSubject: Spinner
    private lateinit var tvCurrentLevel: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var radioGroup: RadioGroup

    private lateinit var rbOption1: RadioButton
    private lateinit var rbOption2: RadioButton
    private lateinit var rbOption3: RadioButton
    private lateinit var rbOption4: RadioButton

    private lateinit var btnSubmit: Button
    private lateinit var tvFeedback: TextView

    private var difficulty = "Medium"
    private var selectedSubject = "DBMS"

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var totalAnswered = 0

    // ---------------- DBMS ----------------

    private val dbmsEasy = listOf(
        AdaptiveQuestion(
            "What does DBMS stand for?",
            listOf(
                "Database Management System",
                "Data Business Management System",
                "Database Machine System",
                "Data Management Software"
            ),
            0,
            "Easy"
        ),
        AdaptiveQuestion(
            "Which SQL command retrieves data?",
            listOf(
                "SELECT",
                "DELETE",
                "INSERT",
                "UPDATE"
            ),
            0,
            "Easy"
        )
    )

    private val dbmsMedium = listOf(
        AdaptiveQuestion(
            "Which key uniquely identifies a record?",
            listOf(
                "Foreign Key",
                "Primary Key",
                "Secondary Key",
                "Composite Key"
            ),
            1,
            "Medium"
        ),
        AdaptiveQuestion(
            "Which normal form removes partial dependency?",
            listOf(
                "1NF",
                "2NF",
                "3NF",
                "4NF"
            ),
            1,
            "Medium"
        )
    )

    private val dbmsHard = listOf(
        AdaptiveQuestion(
            "Which normal form removes transitive dependency?",
            listOf(
                "1NF",
                "2NF",
                "3NF",
                "4NF"
            ),
            2,
            "Hard"
        ),
        AdaptiveQuestion(
            "Which concept ensures that a transaction is completed fully or not at all?",
            listOf(
                "Consistency",
                "Atomicity",
                "Isolation",
                "Durability"
            ),
            1,
            "Hard"
        )
    )

    // ---------------- JAVA ----------------

    private val javaEasy = listOf(
        AdaptiveQuestion(
            "Which keyword is used to create a class in Java?",
            listOf(
                "class",
                "object",
                "define",
                "new"
            ),
            0,
            "Easy"
        ),
        AdaptiveQuestion(
            "Which method is the entry point of a Java program?",
            listOf(
                "start()",
                "main()",
                "run()",
                "execute()"
            ),
            1,
            "Easy"
        )
    )

    private val javaMedium = listOf(
        AdaptiveQuestion(
            "Which concept allows one class to acquire properties of another?",
            listOf(
                "Encapsulation",
                "Inheritance",
                "Abstraction",
                "Polymorphism"
            ),
            1,
            "Medium"
        ),
        AdaptiveQuestion(
            "Which keyword is used to inherit a class in Java?",
            listOf(
                "implements",
                "extends",
                "inherits",
                "super"
            ),
            1,
            "Medium"
        )
    )

    private val javaHard = listOf(
        AdaptiveQuestion(
            "Which mechanism allows the same method name with different parameters?",
            listOf(
                "Overriding",
                "Overloading",
                "Inheritance",
                "Abstraction"
            ),
            1,
            "Hard"
        ),
        AdaptiveQuestion(
            "Which exception occurs when dividing a number by zero?",
            listOf(
                "NullPointerException",
                "IOException",
                "ArithmeticException",
                "ClassNotFoundException"
            ),
            2,
            "Hard"
        )
    )

    // ---------------- KOTLIN ----------------

    private val kotlinEasy = listOf(
        AdaptiveQuestion(
            "Which keyword declares a variable that cannot be reassigned in Kotlin?",
            listOf(
                "var",
                "val",
                "let",
                "const"
            ),
            1,
            "Easy"
        ),
        AdaptiveQuestion(
            "Kotlin is mainly developed by which company?",
            listOf(
                "Google",
                "Microsoft",
                "JetBrains",
                "Apple"
            ),
            2,
            "Easy"
        )
    )

    private val kotlinMedium = listOf(
        AdaptiveQuestion(
            "Which keyword is used to define a function in Kotlin?",
            listOf(
                "function",
                "fun",
                "def",
                "method"
            ),
            1,
            "Medium"
        ),
        AdaptiveQuestion(
            "Which feature helps prevent null pointer exceptions in Kotlin?",
            listOf(
                "Null safety",
                "Inheritance",
                "Overloading",
                "Casting"
            ),
            0,
            "Medium"
        )
    )

    private val kotlinHard = listOf(
        AdaptiveQuestion(
            "Which operator safely accesses a nullable object in Kotlin?",
            listOf(
                "!!",
                "?.",
                "::",
                "->"
            ),
            1,
            "Hard"
        ),
        AdaptiveQuestion(
            "Which keyword is used to create a singleton object in Kotlin?",
            listOf(
                "single",
                "object",
                "singleton",
                "static"
            ),
            1,
            "Hard"
        )
    )

    // ---------------- COMPUTER NETWORKS ----------------

    private val networkEasy = listOf(
        AdaptiveQuestion(
            "What does LAN stand for?",
            listOf(
                "Local Area Network",
                "Large Area Network",
                "Long Area Network",
                "Local Access Node"
            ),
            0,
            "Easy"
        ),
        AdaptiveQuestion(
            "Which device connects different networks?",
            listOf(
                "Switch",
                "Router",
                "Hub",
                "Repeater"
            ),
            1,
            "Easy"
        )
    )

    private val networkMedium = listOf(
        AdaptiveQuestion(
            "Which protocol is used to transfer web pages?",
            listOf(
                "FTP",
                "HTTP",
                "SMTP",
                "TCP"
            ),
            1,
            "Medium"
        ),
        AdaptiveQuestion(
            "Which layer of OSI model is responsible for routing?",
            listOf(
                "Transport",
                "Network",
                "Session",
                "Physical"
            ),
            1,
            "Medium"
        )
    )

    private val networkHard = listOf(
        AdaptiveQuestion(
            "Which protocol provides reliable, connection-oriented communication?",
            listOf(
                "UDP",
                "IP",
                "TCP",
                "ARP"
            ),
            2,
            "Hard"
        ),
        AdaptiveQuestion(
            "Which OSI layer is responsible for end-to-end delivery?",
            listOf(
                "Network",
                "Transport",
                "Data Link",
                "Session"
            ),
            1,
            "Hard"
        )
    )

    // ---------------- OPERATING SYSTEMS ----------------

    private val osEasy = listOf(
        AdaptiveQuestion(
            "What is the main function of an operating system?",
            listOf(
                "Manage computer resources",
                "Create websites",
                "Design images",
                "Write documents"
            ),
            0,
            "Easy"
        ),
        AdaptiveQuestion(
            "Which is an example of an operating system?",
            listOf(
                "Windows",
                "Google",
                "Chrome",
                "Facebook"
            ),
            0,
            "Easy"
        )
    )

    private val osMedium = listOf(
        AdaptiveQuestion(
            "Which scheduling algorithm uses a time quantum?",
            listOf(
                "FCFS",
                "Round Robin",
                "SJF",
                "Priority"
            ),
            1,
            "Medium"
        ),
        AdaptiveQuestion(
            "Which memory management technique uses fixed-size blocks?",
            listOf(
                "Segmentation",
                "Paging",
                "Compaction",
                "Swapping"
            ),
            1,
            "Medium"
        )
    )

    private val osHard = listOf(
        AdaptiveQuestion(
            "Which condition is necessary for deadlock?",
            listOf(
                "Mutual exclusion",
                "Compilation",
                "Caching",
                "Paging"
            ),
            0,
            "Hard"
        ),
        AdaptiveQuestion(
            "Which algorithm is commonly used for deadlock avoidance?",
            listOf(
                "Banker's Algorithm",
                "FCFS",
                "Round Robin",
                "FIFO"
            ),
            0,
            "Hard"
        )
    )

    private lateinit var currentQuestions: List<AdaptiveQuestion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_adaptive)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        spinnerSubject = findViewById(R.id.spinnerSubject)

        tvCurrentLevel = findViewById(R.id.tvCurrentLevel)
        tvQuestion = findViewById(R.id.tvQuestion)
        radioGroup = findViewById(R.id.radioGroup)

        rbOption1 = findViewById(R.id.rbOption1)
        rbOption2 = findViewById(R.id.rbOption2)
        rbOption3 = findViewById(R.id.rbOption3)
        rbOption4 = findViewById(R.id.rbOption4)

        btnSubmit = findViewById(R.id.btnSubmit)
        tvFeedback = findViewById(R.id.tvFeedback)

        val back = findViewById<Button>(R.id.btnBack)

        setupSubjectSpinner()

        loadPreviousPerformance()

        btnSubmit.setOnClickListener {
            checkAnswer()
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun setupSubjectSpinner() {

        val subjects = arrayOf(
            "DBMS",
            "Java",
            "Kotlin",
            "Computer Networks",
            "Operating Systems"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            subjects
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerSubject.adapter = adapter

        spinnerSubject.setSelection(0)

        spinnerSubject.setOnItemSelectedListener(
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    selectedSubject = subjects[position]

                    if (::currentQuestions.isInitialized) {
                        setupQuestions()
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
        )
    }

    private fun loadPreviousPerformance() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            setupQuestions()
            return
        }

        db.collection("students")
            .document(userId)
            .collection("quizResults")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty()) {

                    difficulty = "Medium"

                } else {

                    var totalQuestions = 0
                    var totalCorrect = 0

                    for (document in documents) {

                        totalQuestions +=
                            document.getLong("totalQuestions")
                                ?.toInt() ?: 0

                        totalCorrect +=
                            document.getLong("score")
                                ?.toInt() ?: 0
                    }

                    val percentage =
                        if (totalQuestions > 0) {
                            (totalCorrect * 100) / totalQuestions
                        } else {
                            0
                        }

                    difficulty = when {

                        percentage < 50 ->
                            "Easy"

                        percentage < 75 ->
                            "Medium"

                        else ->
                            "Hard"
                    }
                }

                setupQuestions()
            }
            .addOnFailureListener {

                difficulty = "Medium"

                setupQuestions()
            }
    }

    private fun setupQuestions() {

        tvCurrentLevel.text =
            "Current Difficulty: $difficulty"

        currentQuestions = when (selectedSubject) {

            "DBMS" -> when (difficulty) {
                "Easy" -> dbmsEasy
                "Hard" -> dbmsHard
                else -> dbmsMedium
            }

            "Java" -> when (difficulty) {
                "Easy" -> javaEasy
                "Hard" -> javaHard
                else -> javaMedium
            }

            "Kotlin" -> when (difficulty) {
                "Easy" -> kotlinEasy
                "Hard" -> kotlinHard
                else -> kotlinMedium
            }

            "Computer Networks" -> when (difficulty) {
                "Easy" -> networkEasy
                "Hard" -> networkHard
                else -> networkMedium
            }

            "Operating Systems" -> when (difficulty) {
                "Easy" -> osEasy
                "Hard" -> osHard
                else -> osMedium
            }

            else -> dbmsMedium
        }

        currentQuestionIndex = 0
        correctAnswers = 0
        totalAnswered = 0

        radioGroup.visibility = View.VISIBLE
        btnSubmit.visibility = View.VISIBLE

        showQuestion()
    }

    private fun showQuestion() {

        if (currentQuestionIndex >= currentQuestions.size) {
            showFinalResult()
            return
        }

        val question =
            currentQuestions[currentQuestionIndex]

        tvQuestion.text =
            question.question

        rbOption1.text =
            question.options[0]

        rbOption2.text =
            question.options[1]

        rbOption3.text =
            question.options[2]

        rbOption4.text =
            question.options[3]

        radioGroup.clearCheck()

        btnSubmit.text =
            if (currentQuestionIndex ==
                currentQuestions.size - 1
            ) {
                "FINISH"
            } else {
                "SUBMIT ANSWER"
            }
    }

    private fun checkAnswer() {

        val selectedId =
            radioGroup.checkedRadioButtonId

        if (selectedId == -1) {

            Toast.makeText(
                this,
                "Please select an answer",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val selectedButton =
            findViewById<RadioButton>(selectedId)

        val selectedIndex =
            radioGroup.indexOfChild(selectedButton)

        val current =
            currentQuestions[currentQuestionIndex]

        totalAnswered++

        if (selectedIndex == current.correctAnswer) {

            correctAnswers++

            tvFeedback.text =
                "Correct! Good job."

        } else {

            tvFeedback.text =
                "Incorrect. Keep practicing."
        }

        currentQuestionIndex++

        if (currentQuestionIndex < currentQuestions.size) {

            showQuestion()

        } else {

            showFinalResult()
        }
    }

    private fun showFinalResult() {

        tvQuestion.text =
            "Adaptive Quiz Completed"

        radioGroup.visibility =
            View.GONE

        btnSubmit.visibility =
            View.GONE

        val percentage =
            if (totalAnswered > 0) {
                (correctAnswers * 100) / totalAnswered
            } else {
                0
            }

        val newDifficulty = when {

            percentage < 50 ->
                "Easy"

            percentage < 75 ->
                "Medium"

            else ->
                "Hard"
        }

        tvFeedback.text =
            "Score: $correctAnswers/$totalAnswered\n\n" +
                    "Percentage: $percentage%\n\n" +
                    "Next Recommended Difficulty:\n" +
                    newDifficulty

        saveAdaptiveResult(
            percentage,
            newDifficulty
        )
    }

    private fun saveAdaptiveResult(
        percentage: Int,
        nextDifficulty: String
    ) {

        val userId =
            auth.currentUser?.uid

        if (userId == null) {
            return
        }

        val result = hashMapOf(
            "subject" to selectedSubject,
            "difficulty" to difficulty,
            "percentage" to percentage,
            "nextDifficulty" to nextDifficulty,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("students")
            .document(userId)
            .collection("adaptiveResults")
            .add(result)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Adaptive result saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}