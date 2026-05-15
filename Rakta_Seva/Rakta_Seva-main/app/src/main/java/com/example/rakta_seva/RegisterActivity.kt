package com.example.rakta_seva

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {
    private var selectedBloodGroup: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val glBloodGroups = findViewById<GridLayout>(R.id.glBloodGroups)
        val btnJoinDonor = findViewById<Button>(R.id.btnJoinDonor)
        val btnUseLocation = findViewById<Button>(R.id.btnUseLocation)
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etLastDonation = findViewById<EditText>(R.id.etLastDonation)

        for (i in 0 until glBloodGroups.childCount) {
            val child = glBloodGroups.getChildAt(i)
            if (child is MaterialButton) {
                child.setOnClickListener {
                    resetBloodGroupButtons(glBloodGroups)
                    child.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
                    child.setTextColor(Color.WHITE)
                    selectedBloodGroup = child.text.toString()
                }
            }
        }

        btnUseLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            } else {
                fetchLocation()
            }
        }

        btnJoinDonor.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val lastDonation = etLastDonation.text.toString().trim().ifEmpty { "Never" }

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || selectedBloodGroup.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Success Criteria: Automatically set availability
            // To ensure new donors are visible for testing, we'll set isAvailable to true by default
            val newUser = User(
                fullName = name,
                email = email,
                phone = phone,
                password = password,
                bloodGroup = selectedBloodGroup,
                location = address,
                lastDonationDate = lastDonation,
                isAvailable = true // Default to true so they show up in Donors list
            )

            if (DataManager.registerUser(newUser)) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                DataManager.login(email, password)
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLocation() {
        LocationUtils.getCurrentLocationName(this) { location ->
            findViewById<EditText>(R.id.etAddress).setText(location)
            Toast.makeText(this, "Location detected: $location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }

    private fun resetBloodGroupButtons(gl: GridLayout) {
        for (i in 0 until gl.childCount) {
            val child = gl.getChildAt(i)
            if (child is MaterialButton) {
                child.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                child.setTextColor(Color.BLACK)
                child.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.divider_gray)))
            }
        }
    }
}