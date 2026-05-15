package com.example.rakta_seva

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnTabRequests = findViewById<MaterialButton>(R.id.btnTabRequests)
        val btnTabDonors = findViewById<MaterialButton>(R.id.btnTabDonors)
        val llRequestsSection = findViewById<LinearLayout>(R.id.llRequestsSection)
        val llDonorsSection = findViewById<LinearLayout>(R.id.llDonorsSection)

        setupRequestsList()
        setupDonorsList()

        btnTabRequests.setOnClickListener {
            switchTab(true, btnTabRequests, btnTabDonors, llRequestsSection, llDonorsSection)
        }

        btnTabDonors.setOnClickListener {
            switchTab(false, btnTabRequests, btnTabDonors, llRequestsSection, llDonorsSection)
        }

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            DataManager.logout()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    private fun switchTab(isRequests: Boolean, btnReq: MaterialButton, btnDon: MaterialButton, secReq: View, secDon: View) {
        if (isRequests) {
            btnReq.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
            btnReq.setTextColor(Color.WHITE)
            btnDon.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
            btnDon.setTextColor(Color.BLACK)
            secReq.visibility = View.VISIBLE
            secDon.visibility = View.GONE
        } else {
            btnDon.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
            btnDon.setTextColor(Color.WHITE)
            btnReq.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
            btnReq.setTextColor(Color.BLACK)
            secDon.visibility = View.VISIBLE
            secReq.visibility = View.GONE
        }
    }

    private fun setupRequestsList() {
        val listContainer = findViewById<LinearLayout>(R.id.llGlobalRequests)
        listContainer.removeAllViews()
        val requests = DataManager.getRequests()
        
        requests.forEach { request ->
            val card = layoutInflater.inflate(R.layout.item_request, listContainer, false)
            card.findViewById<TextView>(R.id.tvBloodGroup).text = request.bloodGroup
            card.findViewById<TextView>(R.id.tvUrgency).text = "● ${request.urgency.uppercase()}"
            card.findViewById<TextView>(R.id.tvHospitalName).text = request.hospitalName
            card.findViewById<TextView>(R.id.tvPatientInfo).text = "For: ${request.patientName} • ${request.units} units"
            
            val urgencyColor = when(request.urgency.lowercase()) {
                "critical" -> R.color.primary_red
                "high" -> R.color.urgency_high
                else -> R.color.text_gray
            }
            card.findViewById<TextView>(R.id.tvUrgency).setTextColor(ContextCompat.getColor(this, urgencyColor))
            listContainer.addView(card)
        }
    }

    private fun setupDonorsList() {
        val listContainer = findViewById<LinearLayout>(R.id.llDonorsList)
        listContainer.removeAllViews()
        val users = DataManager.getUsers().filter { !it.isAdmin }
        
        users.forEach { user ->
            val donorView = layoutInflater.inflate(R.layout.item_donor, listContainer, false)
            donorView.findViewById<TextView>(R.id.tvDonorName).text = user.fullName
            donorView.findViewById<TextView>(R.id.tvBloodGroup).text = user.bloodGroup
            donorView.findViewById<TextView>(R.id.tvDonorLocation).text = "Last donation: ${user.lastDonationDate}"
            donorView.findViewById<TextView>(R.id.tvInitial).text = user.fullName.first().toString()
            
            listContainer.addView(donorView)
        }
    }
}