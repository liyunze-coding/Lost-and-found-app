package com.deakin.lostandfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deakin.lostandfound.data.DatabaseHelper
import com.deakin.lostandfound.model.Item
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale

class AddLostItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_lost_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = DatabaseHelper(this, null)

        val radioGroup = findViewById<RadioGroup>(R.id.RGroup)

        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedText = selectedRadioButton.text.toString()

            val nameEditText = findViewById<EditText>(R.id.nameEditText)
            val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
            val descriptionEditText = findViewById<TextInputEditText>(R.id.descriptionEditText)
            val dateEditText = findViewById<EditText>(R.id.dateEditText)
            val locationEditText = findViewById<EditText>(R.id.locationEditText)

            // process date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val parsedDate = dateFormat.parse(dateEditText.text.toString())!!

            val itemObj = Item(
                name = nameEditText.text.toString(),
                lostOrFound = selectedText,
                phone = phoneEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                date = parsedDate,
                location = locationEditText.text.toString()
            )

            val result = db.insertItem(itemObj)

            if (result > -1) {
                Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}