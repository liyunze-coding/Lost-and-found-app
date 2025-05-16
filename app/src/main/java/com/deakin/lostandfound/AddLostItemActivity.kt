package com.deakin.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.http.UrlRequest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.BuildConfig
import com.deakin.lostandfound.data.DatabaseHelper
import com.deakin.lostandfound.model.Item
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale


class AddLostItemActivity : AppCompatActivity() {
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedPlace: Place? = null
    private lateinit var locationEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_lost_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Places.initialize(applicationContext, com.deakin.lostandfound.BuildConfig.MAPS_API_KEY)

        val db = DatabaseHelper(this, null)
        locationEditText = findViewById<EditText>(R.id.locationEditText)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val getCurrentLocationButton = findViewById<Button>(R.id.getCurrentLocationButton)

        getCurrentLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val placeName = address.getAddressLine(0)

                                Log.i("CurrentLocation", "Name: $placeName, Lat: $latitude, Lng: $longitude")

                                // Create a fake Place to reuse existing logic
                                selectedPlace = Place.builder()
                                    .setName(placeName)
                                    .setLatLng(LatLng(latitude, longitude))
                                    .build()

                                locationEditText.setText(placeName)

                                Toast.makeText(this, "Location set to: ${placeName}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            }
        }

        val radioGroup = findViewById<RadioGroup>(R.id.RGroup)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val descriptionEditText = findViewById<TextInputEditText>(R.id.descriptionEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)


        locationEditText.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)

            this.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        saveButton.setOnClickListener {
            try {
                val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedText = selectedRadioButton.text.toString()

                val nameEditText = findViewById<EditText>(R.id.nameEditText)

                if (nameEditText.text.isNullOrBlank()) {
                    nameEditText.error = "Title cannot be empty!"
                    nameEditText.requestFocus()
                    return@setOnClickListener
                }

                val lat = selectedPlace!!.latLng?.latitude ?: 0.0
                val lng = selectedPlace!!.latLng?.longitude ?: 0.0

                // process date
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                var parsedDate = dateFormat.parse(dateEditText.text.toString())!!

                val itemObj = Item(
                    name = nameEditText.text.toString(),
                    lostOrFound = selectedText,
                    phone = phoneEditText.text.toString(),
                    description = descriptionEditText.text.toString(),
                    date = parsedDate,
                    location = selectedPlace!!.name ?: "Unknown Location",
                    latitude = lat,
                    longitude = lng
                )

                Log.d("PlaceSelected", "${itemObj}")

                val result = db.insertItem(itemObj)

                if (result > -1) {
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    selectedPlace = place
                    locationEditText.setText(place.name)
                    Log.i("PlaceSelected", "Place: ${place.name}, ${place.latLng}")
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("PlaceSelectionError", "An error occurred: ${status.statusMessage}")
                }
            }
        }
    }
}