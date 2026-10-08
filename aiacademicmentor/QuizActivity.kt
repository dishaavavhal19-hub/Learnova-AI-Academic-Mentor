package com.example.aiacademicmentor

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var spinnerSubject: Spinner
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbOption1: RadioButton
    private lateinit var rbOption2: RadioButton
    private lateinit var rbOption3: RadioButton
    private lateinit var rbOption4: RadioButton
    private lateinit var btnNext: Button
    private lateinit var tvResult: TextView
    private lateinit var btnBack: Button

    private val subjects = arrayOf(
        "DBMS",
        "Java",
        "Kotlin",
        "Computer Networks",
        "Operating Systems"
    )

    private var currentSubject = "DBMS"
    private var currentQuestion = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        spinnerSubject = findViewById(R.id.spinnerSubject)
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvQuestion = findViewById(R.id.tvQuestion)
        radioGroup = findViewById(R.id.radioGroup)

        rbOption1 = findViewById(R.id.rbOption1)
        rbOption2 = findViewById(R.id.rbOption2)
        rbOption3 = findViewById(R.id.rbOption3)
        rbOption4 = findViewById(R.id.rbOption4)

        btnNext = findViewById(R.id.btnNext)
        tvResult = findViewById(R.id.tvResult)
        btnBack = findViewById(R.id.btnBack)

        // SUBJECT DROPDOWN
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            subjects
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerSubject.adapter = adapter

        spinnerSubject.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentSubject = subjects[position]
                    currentQuestion = 0
                    score = 0

                    tvResult.visibility = View.GONE
                    btnNext.isEnabled = true

                    loadQuestion()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        btnNext.setOnClickListener {
            checkAnswer()
        }

        btnBack.setOnClickListener {
            finish()
        }

        loadQuestion()
    }

    private fun loadQuestion() {

        val questionList = getQuestions(currentSubject)

        if (questionList.isEmpty()) return

        val question = questionList[currentQuestion]

        tvQuestionNumber.text =
            "Question ${currentQuestion + 1} of ${questionList.size}"

        tvQuestion.text = question[0]

        rbOption1.text = question[1]
        rbOption2.text = question[2]
        rbOption3.text = question[3]
        rbOption4.text = question[4]

        radioGroup.clearCheck()

        btnNext.text =
            if (currentQuestion == questionList.size - 1)
                "Finish Quiz"
            else
                "Next Question"
    }

    private fun checkAnswer() {

        val selectedId = radioGroup.checkedRadioButtonId

        if (selectedId == -1) {
            return
        }

        val selectedAnswer = when (selectedId) {
            R.id.rbOption1 -> 1
            R.id.rbOption2 -> 2
            R.id.rbOption3 -> 3
            R.id.rbOption4 -> 4
            else -> 0
        }

        val questionList = getQuestions(currentSubject)

        val correctAnswer = questionList[currentQuestion][5].toInt()

        if (selectedAnswer == correctAnswer) {
            score++
        }

        currentQuestion++

        if (currentQuestion < questionList.size) {
            loadQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {

        val total = getQuestions(currentSubject).size

        tvResult.text =
            "🎉 Quiz Completed!\n\n" +
                    "Subject: $currentSubject\n" +
                    "Score: $score / $total"

        tvResult.visibility = View.VISIBLE

        btnNext.text = "Quiz Completed"
        btnNext.isEnabled = false
    }

    private fun getQuestions(subject: String): List<Array<String>> {

        return when (subject) {

            "DBMS" -> listOf(
                arrayOf(
                    "Which key uniquely identifies a record?",
                    "Primary Key",
                    "Foreign Key",
                    "Candidate Key",
                    "Super Key",
                    "1"
                ),
                arrayOf(
                    "What does SQL stand for?",
                    "Structured Query Language",
                    "Simple Query Language",
                    "System Query Language",
                    "Sequential Query Language",
                    "1"
                ),
                arrayOf(
                    "Which normal form removes partial dependency?",
                    "1NF",
                    "2NF",
                    "3NF",
                    "BCNF",
                    "2"
                ),
                arrayOf(
                    "Which command retrieves data?",
                    "INSERT",
                    "UPDATE",
                    "SELECT",
                    "DELETE",
                    "3"
                ),
                arrayOf(
                    "Which is a DBMS?",
                    "MySQL",
                    "HTML",
                    "CSS",
                    "XML",
                    "1"
                )
            )

            "Java" -> listOf(
                arrayOf(
                    "Which keyword creates a class?",
                    "class",
                    "new",
                    "object",
                    "create",
                    "1"
                ),
                arrayOf(
                    "Which is the main method?",
                    "start()",
                    "main()",
                    "run()",
                    "execute()",
                    "2"
                ),
                arrayOf(
                    "Which keyword is used for inheritance?",
                    "this",
                    "super",
                    "extends",
                    "inherit",
                    "3"
                ),
                arrayOf(
                    "Which type stores true or false?",
                    "int",
                    "boolean",
                    "String",
                    "char",
                    "2"
                ),
                arrayOf(
                    "Java is mainly a ______ language.",
                    "Procedural",
                    "Object-oriented",
                    "Markup",
                    "Query",
                    "2"
                )
            )

            "Kotlin" -> listOf(
                arrayOf(
                    "Who developed Kotlin?",
                    "Google",
                    "Microsoft",
                    "JetBrains",
                    "Apple",
                    "3"
                ),
                arrayOf(
                    "Which keyword declares a changeable variable?",
                    "val",
                    "var",
                    "let",
                    "change",
                    "2"
                ),
                arrayOf(
                    "Which keyword declares an immutable variable?",
                    "var",
                    "let",
                    "val",
                    "const",
                    "3"
                ),
                arrayOf(
                    "Kotlin is officially supported for?",
                    "Android",
                    "DOS",
                    "BIOS",
                    "None",
                    "1"
                ),
                arrayOf(
                    "Which symbol is used for string templates?",
                    "$",
                    "#",
                    "@",
                    "&",
                    "1"
                )
            )

            "Computer Networks" -> listOf(
                arrayOf(
                    "What does IP stand for?",
                    "Internet Protocol",
                    "Internal Program",
                    "Internet Process",
                    "Information Protocol",
                    "1"
                ),
                arrayOf(
                    "Which device connects different networks?",
                    "Switch",
                    "Router",
                    "Keyboard",
                    "Monitor",
                    "2"
                ),
                arrayOf(
                    "Which protocol is used for web pages?",
                    "HTTP",
                    "FTP",
                    "SMTP",
                    "TCP",
                    "1"
                ),
                arrayOf(
                    "Which topology uses a central device?",
                    "Bus",
                    "Ring",
                    "Star",
                    "Mesh",
                    "3"
                ),
                arrayOf(
                    "What does LAN stand for?",
                    "Large Area Network",
                    "Local Area Network",
                    "Long Area Network",
                    "Linked Area Network",
                    "2"
                )
            )

            else -> listOf(
                arrayOf(
                    "Which is an operating system?",
                    "Windows",
                    "HTML",
                    "SQL",
                    "CSS",
                    "1"
                ),
                arrayOf(
                    "What manages computer resources?",
                    "Operating System",
                    "Browser",
                    "Compiler",
                    "Editor",
                    "1"
                ),
                arrayOf(
                    "Which is a process state?",
                    "Running",
                    "Printing",
                    "Drawing",
                    "Browsing",
                    "1"
                ),
                arrayOf(
                    "Which memory is closest to the CPU?",
                    "Hard Disk",
                    "Cache",
                    "DVD",
                    "USB",
                    "2"
                ),
                arrayOf(
                    "What does OS stand for?",
                    "Operating System",
                    "Open Software",
                    "Online Service",
                    "Output System",
                    "1"
                )
            )
        }
    }
}