package com.example.aiacademicmentor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MentorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor)

        val question = findViewById<EditText>(R.id.etQuestion)
        val askAI = findViewById<Button>(R.id.btnAskAI)
        val answer = findViewById<TextView>(R.id.tvAnswer)
        val back = findViewById<Button>(R.id.btnBack)

        askAI.setOnClickListener {

            val questionText = question.text.toString().trim()

            if (questionText.isEmpty()) {
                question.error = "Please enter a question"
                return@setOnClickListener
            }

            answer.text = "Thinking..."

            Thread {

                try {

                    val url = URL("http://172.16.235.224:8000/ask")
                    val connection = url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"
                    connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )
                    connection.connectTimeout = 10000
                    connection.readTimeout = 60000
                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("question", questionText)

                    val writer = OutputStreamWriter(connection.outputStream)
                    writer.write(json.toString())
                    writer.flush()
                    writer.close()

                    val reader = BufferedReader(
                        InputStreamReader(connection.inputStream)
                    )

                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    connection.disconnect()

                    val result = JSONObject(response.toString())
                    val aiAnswer = result.getString("answer")

                    runOnUiThread {
                        answer.text = aiAnswer

                        Toast.makeText(
                            this,
                            "Answer generated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {

                    runOnUiThread {
                        answer.text =
                            "Unable to get AI response.\n\n${e.message}"
                    }
                }
            }.start()
        }

        back.setOnClickListener {
            finish()
        }
    }
}