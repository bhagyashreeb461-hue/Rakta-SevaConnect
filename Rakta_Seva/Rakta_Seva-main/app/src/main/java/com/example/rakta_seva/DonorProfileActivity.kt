package com.example.rakta_seva

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DonorProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        val donorName = intent.getStringExtra("DONOR_NAME") ?: "Aditya Sharma"
        val bloodGroup = intent.getStringExtra("BLOOD_GROUP") ?: "A+"
        val donorEmail = intent.getStringExtra("DONOR_EMAIL") ?: "donor@example.com"
        val donorPhone = intent.getStringExtra("DONOR_PHONE") ?: "+91 0000000000"

        findViewById<TextView>(R.id.tvName).text = donorName
        findViewById<TextView>(R.id.tvBloodGroup).text = bloodGroup
        findViewById<TextView>(R.id.tvInitial).text = donorName.firstOrNull()?.toString() ?: "D"

        // Check if contact is shared
        val isShared = DataManager.isContactShared(DataManager.currentUser?.email ?: "", donorEmail)
        
        if (isShared) {
            findViewById<TextView>(R.id.tvPhoneNumber).text = donorPhone
            findViewById<TextView>(R.id.tvPrivacyNote).text = "Donor accepted! Contact revealed."
        }

        val latestRequest = DataManager.getRequests().firstOrNull()
        if (latestRequest != null) {
            findViewById<TextView>(R.id.tvHospitalName).text = latestRequest.hospitalName
            findViewById<TextView>(R.id.tvHospitalAddress).text = latestRequest.address.ifEmpty { "Location not specified" }
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val btnSendRequest = findViewById<Button>(R.id.btnSendRequest)
        if (isShared) {
            btnSendRequest.text = "Request Accepted"
            btnSendRequest.isEnabled = false
            btnSendRequest.setBackgroundTintList(null)
            btnSendRequest.setBackgroundColor(getColor(R.color.success_green))
        }

        btnSendRequest.setOnClickListener {
            DataManager.sendDirectRequestToDonor(donorEmail, bloodGroup)
            Toast.makeText(this, "Request sent to $donorName!", Toast.LENGTH_LONG).show()
            
            btnSendRequest.text = "Request Sent"
            btnSendRequest.isEnabled = false
            btnSendRequest.setBackgroundTintList(null)
            btnSendRequest.setBackgroundColor(getColor(R.color.text_gray))
        }
    }
}