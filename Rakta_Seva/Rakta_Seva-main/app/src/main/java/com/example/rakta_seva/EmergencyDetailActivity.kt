package com.example.rakta_seva

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmergencyDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_detail)

        val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("REQUEST_DATA", BloodRequest::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("REQUEST_DATA") as? BloodRequest
        }
        val isMismatched = intent.getBooleanExtra("IS_MISMATCHED", false)

        if (request != null) {
            updateUI(request)
        }

        if (isMismatched) {
            findViewById<View>(R.id.cvWarning).visibility = View.VISIBLE
            val userBG = DataManager.currentUser?.bloodGroup ?: "AB+"
            val reqBG = request?.bloodGroup ?: "A+"
            findViewById<TextView>(R.id.tvWarningText).text = "This request is for $reqBG. Your group is $userBG."
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnAccept = findViewById<Button>(R.id.btnAccept)
        
        // If already accepted by me or others
        if (request?.status == "ACCEPTED") {
            btnAccept.text = "Accepted - Contact: ${request.contactNumber}"
            btnAccept.isEnabled = false
        }

        btnAccept.setOnClickListener {
            if (request != null) {
                DataManager.acceptRequest(request.id)
                Toast.makeText(this, "Request Accepted! Requester's contact shared.", Toast.LENGTH_LONG).show()
                
                // Refresh UI to show number
                btnAccept.text = "Contact: ${request.contactNumber}"
                btnAccept.isEnabled = false
                findViewById<TextView>(R.id.tvNotes).text = "Contact revealed! You can now call the hospital."
            }
        }

        findViewById<Button>(R.id.btnCantHelp).setOnClickListener {
            finish()
        }
    }

    private fun updateUI(request: BloodRequest) {
        findViewById<TextView>(R.id.tvBloodGroup).text = request.bloodGroup
        findViewById<TextView>(R.id.tvHospitalName).text = request.hospitalName
        findViewById<TextView>(R.id.tvHospitalAddress).text = request.address
        findViewById<TextView>(R.id.tvPatientName).text = request.patientName
        findViewById<TextView>(R.id.tvUrgency).text = "● ${request.urgency.uppercase()}"
        findViewById<TextView>(R.id.tvUnitsNeeded).text = "${request.units} units needed"
        findViewById<TextView>(R.id.tvNotes).text = request.notes.ifEmpty { "No specific notes provided." }
    }
}