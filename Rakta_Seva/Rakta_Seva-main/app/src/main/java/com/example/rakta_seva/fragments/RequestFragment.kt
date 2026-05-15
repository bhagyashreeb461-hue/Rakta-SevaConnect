package com.example.rakta_seva.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rakta_seva.BloodRequest
import com.example.rakta_seva.DataManager
import com.example.rakta_seva.LocationUtils
import com.example.rakta_seva.R
import com.google.android.material.button.MaterialButton
import java.util.UUID

class RequestFragment : Fragment() {

    private var selectedBloodGroup: String? = "O-"
    private var selectedUrgency: String = "Critical"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(requireView())
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        val glBloodGroups = view.findViewById<GridLayout>(R.id.glBloodGroups)
        val btnDispatch = view.findViewById<Button>(R.id.btnDispatch)
        val btnUseLocation = view.findViewById<Button>(R.id.btnUseLocation)

        // Handle Blood Group Selection
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

        // Handle Urgency Selection
        val btnCritical = view.findViewById<MaterialButton>(R.id.btnUrgencyCritical)
        val btnHigh = view.findViewById<MaterialButton>(R.id.btnUrgencyHigh)
        val btnNormal = view.findViewById<MaterialButton>(R.id.btnUrgencyNormal)

        btnCritical.setOnClickListener { selectUrgency(btnCritical, btnHigh, btnNormal, "Critical") }
        btnHigh.setOnClickListener { selectUrgency(btnHigh, btnCritical, btnNormal, "High") }
        btnNormal.setOnClickListener { selectUrgency(btnNormal, btnCritical, btnHigh, "Normal") }

        btnUseLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                fetchLocation(view)
            }
        }

        btnDispatch.setOnClickListener {
            val hospital = view.findViewById<EditText>(R.id.etHospitalName).text.toString()
            val patient = view.findViewById<EditText>(R.id.etPatientName).text.toString()
            val units = view.findViewById<EditText>(R.id.etUnits).text.toString()
            val contact = view.findViewById<EditText>(R.id.etContactNumber).text.toString()
            val address = view.findViewById<EditText>(R.id.etAddress).text.toString()
            val notes = view.findViewById<EditText>(R.id.etNotes).text.toString()

            if (hospital.isEmpty() || patient.isEmpty() || units.isEmpty() || contact.isEmpty()) {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            } else {
                val newRequest = BloodRequest(
                    id = UUID.randomUUID().toString(),
                    hospitalName = hospital,
                    bloodGroup = selectedBloodGroup ?: "O+",
                    units = units,
                    urgency = selectedUrgency,
                    patientName = patient,
                    contactNumber = contact,
                    address = address,
                    notes = notes,
                    requesterEmail = DataManager.currentUser?.email ?: "donor@example.com"
                )
                
                DataManager.addRequest(newRequest)
                
                Toast.makeText(context, "Emergency Alert Dispatched Successfully!", Toast.LENGTH_LONG).show()
                
                // Clear fields
                view.findViewById<EditText>(R.id.etHospitalName).text.clear()
                view.findViewById<EditText>(R.id.etPatientName).text.clear()
                view.findViewById<EditText>(R.id.etUnits).text.clear()
                view.findViewById<EditText>(R.id.etNotes).text.clear()
                view.findViewById<EditText>(R.id.etAddress).text.clear()
                view.findViewById<EditText>(R.id.etContactNumber).text.clear()
            }
        }

        return view
    }

    private fun fetchLocation(view: View) {
        LocationUtils.getCurrentLocationName(requireContext()) { location ->
            view.findViewById<EditText>(R.id.etAddress).setText(location)
            Toast.makeText(context, "Location updated: $location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetBloodGroupButtons(gl: GridLayout) {
        for (i in 0 until gl.childCount) {
            val child = gl.getChildAt(i)
            if (child is MaterialButton) {
                child.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                child.setTextColor(Color.BLACK)
                child.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.divider_gray)))
            }
        }
    }

    private fun selectUrgency(selected: MaterialButton, other1: MaterialButton, other2: MaterialButton, urgency: String) {
        selectedUrgency = urgency
        
        selected.setBackgroundTintList(ColorStateList.valueOf(
            if (urgency == "Critical") ContextCompat.getColor(requireContext(), R.color.primary_red)
            else if (urgency == "High") ContextCompat.getColor(requireContext(), R.color.urgency_high)
            else Color.BLACK
        ))
        selected.setTextColor(Color.WHITE)

        listOf(other1, other2).forEach {
            it.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
            it.setTextColor(if (it.id == R.id.btnUrgencyHigh) ContextCompat.getColor(requireContext(), R.color.urgency_high) else Color.BLACK)
            it.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.divider_gray)))
        }
    }
}