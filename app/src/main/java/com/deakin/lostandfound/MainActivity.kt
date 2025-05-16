package com.deakin.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val createNewAdvertButton = findViewById<Button>(R.id.create_advert)
        val showItemsButton = findViewById<Button>(R.id.show_items)
        val showOnMapButton = findViewById<Button>(R.id.show_on_map)

        createNewAdvertButton.setOnClickListener {
            val addItemIntent = Intent(this, AddLostItemActivity::class.java)
            startActivity(addItemIntent)
        }

        showItemsButton.setOnClickListener {
            val viewItemsIntent = Intent(this, ViewItemsActivity::class.java)
            startActivity(viewItemsIntent)
        }

        showOnMapButton.setOnClickListener {
            val showOnMapIntent = Intent(this, ShowOnMapActivity::class.java)
            startActivity(showOnMapIntent)
        }
    }
}