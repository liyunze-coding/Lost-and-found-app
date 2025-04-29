package com.deakin.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deakin.lostandfound.data.DatabaseHelper

class ItemInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val phone = intent.getStringExtra("phone")
        val id = intent.getStringExtra("id")

        findViewById<TextView>(R.id.title).text = title
        findViewById<TextView>(R.id.date).text = "Date: ${date}"
        findViewById<TextView>(R.id.description).text = "Description: ${description}"
        findViewById<TextView>(R.id.location).text = "Location: ${location}"
        findViewById<TextView>(R.id.phone).text = "Phone number: ${phone}"

        val backToListButton = findViewById<TextView>(R.id.backToList)
        backToListButton.setOnClickListener {
            val intent = Intent(this, ViewItemsActivity::class.java)
            startActivity(intent)
        }

        val removeButton = findViewById<TextView>(R.id.removeButton)
        val dbHelper = DatabaseHelper(this, null)
        removeButton.setOnClickListener {
            var result = false
            try {
                val rowId = id?.toInt()

                if (rowId != null)  {
                    result = dbHelper.deleteItem(rowId)
                }
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (result) {
                    Toast.makeText(this, "Item removed successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to remove item.", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this, ViewItemsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}