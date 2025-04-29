package com.deakin.lostandfound

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deakin.lostandfound.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale

class ViewItemsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_items)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Find the LinearLayout inside ScrollView
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

        // Database
        val dbHelper = DatabaseHelper(this, null)
        val items = dbHelper.getItems()

        for (item in items) {
            // parse date
            val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val date = parser.parse(item.date.toString())!!

            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = formatter.format(date)

            val relativeTime = DateUtils.getRelativeTimeSpanString(
                item.date.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )

            // Final string
            val dateText = "$formattedDate ($relativeTime)"

            val itemButton = Button(this).apply {
                text = "${item.lostOrFound} ${item.name}"

                textSize = 18f
                setPadding(16, 16, 16, 16)
            }

            // pass information to Intent
            val itemInfoIntent = Intent(this, ItemInfoActivity::class.java)

            Log.d("ViewItemsActivity", "Item ID: ${item.id}")

            itemInfoIntent.putExtra("title", "${item.lostOrFound} ${item.name}")
            itemInfoIntent.putExtra("date", dateText)
            itemInfoIntent.putExtra("description", item.description)
            itemInfoIntent.putExtra("location", item.location)
            itemInfoIntent.putExtra("phone", item.phone)
            itemInfoIntent.putExtra("id", item.id.toString())

            itemButton.setOnClickListener {
                startActivity(itemInfoIntent)
            }

            linearLayout.addView(itemButton)

            val backToHomeButton = findViewById<Button>(R.id.backToHome)
            backToHomeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}