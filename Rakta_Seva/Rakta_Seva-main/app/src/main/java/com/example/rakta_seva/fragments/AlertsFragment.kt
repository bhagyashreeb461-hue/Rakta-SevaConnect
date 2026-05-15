package com.example.rakta_seva.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rakta_seva.BloodRequest
import com.example.rakta_seva.DataManager
import com.example.rakta_seva.EmergencyDetailActivity
import com.example.rakta_seva.R
import com.google.android.material.button.MaterialButton

class AlertsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)

        val btnMyBG = view.findViewById<MaterialButton>(R.id.btnFilterMyBG)
        val btnAll = view.findViewById<MaterialButton>(R.id.btnFilterAll)
        
        // Initial setup for the logged in user's blood group chip in UI
        view.findViewById<TextView>(R.id.tvUserBloodGroupChip).text = 
            DataManager.currentUser?.bloodGroup ?: "AB+"

        // Add some dummy initial data if none exists
        if (DataManager.getRequests().isEmpty()) {
            DataManager.addRequest(BloodRequest("1", "City General Hospital", "O+", "2", "Critical", "Mr. Verma", "8792812645", "MG Road, Bangalore", "Road accident victim", "admin@raktaseva.in"))
            DataManager.addRequest(BloodRequest("2", "TEST Hospital", "A+", "2", "High", "TEST Patient", "1234567890", "BLR TEST", "TEST request", "donor@example.com"))
        }

        btnMyBG.setOnClickListener {
            updateFilters(true, btnMyBG, btnAll, view)
        }

        btnAll.setOnClickListener {
            updateFilters(false, btnMyBG, btnAll, view)
        }

        // Initial load
        updateFilters(false, btnMyBG, btnAll, view)

        return view
    }

    private fun updateFilters(isMyBG: Boolean, btnMyBG: MaterialButton, btnAll: MaterialButton, root: View) {
        if (isMyBG) {
            btnMyBG.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
            btnMyBG.setTextColor(Color.WHITE)
            btnAll.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.divider_gray)))
            btnAll.setTextColor(Color.BLACK)
        } else {
            btnAll.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
            btnAll.setTextColor(Color.WHITE)
            btnMyBG.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.divider_gray)))
            btnMyBG.setTextColor(Color.BLACK)
        }

        val listContainer = root.findViewById<LinearLayout>(R.id.llAlertsList)
        val emptyState = root.findViewById<View>(R.id.llNoAlerts)
        listContainer.removeAllViews()

        val allRequests = DataManager.getRequests()
        val userBG = DataManager.currentUser?.bloodGroup ?: "AB+"
        
        val filtered = if (isMyBG) allRequests.filter { it.bloodGroup == userBG } else allRequests

        if (filtered.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            listContainer.visibility = View.VISIBLE
            filtered.forEach { request ->
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
                card.findViewById<TextView>(R.id.tvUrgency).setTextColor(ContextCompat.getColor(requireContext(), urgencyColor))

                card.setOnClickListener {
                    val intent = Intent(requireContext(), EmergencyDetailActivity::class.java)
                    intent.putExtra("IS_MISMATCHED", request.bloodGroup != userBG)
                    intent.putExtra("REQUEST_DATA", request)
                    startActivity(intent)
                }
                listContainer.addView(card)
            }
        }
    }
}