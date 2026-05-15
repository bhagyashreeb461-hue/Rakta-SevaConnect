package com.example.rakta_seva

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.UUID

data class User(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val bloodGroup: String,
    val location: String = "Not set",
    val isAdmin: Boolean = false,
    val lastDonationDate: String = "Never",
    val isAvailable: Boolean = true,
    val lastLogin: Long = System.currentTimeMillis()
) : Serializable

data class BloodRequest(
    val id: String,
    val hospitalName: String,
    val bloodGroup: String,
    val units: String,
    val urgency: String,
    val patientName: String,
    val contactNumber: String,
    val address: String,
    val notes: String,
    val requesterEmail: String,
    val status: String = "PENDING",
    val donorEmail: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class Donor(
    val name: String,
    val bloodGroup: String,
    val distance: String,
    val phoneNumber: String,
    val rating: String = "4.8"
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val forUserEmail: String,
    val type: String = "INFO",
    val status: String = "PENDING",
    val senderEmail: String? = null,
    val requestId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

object DataManager {
    private const val PREFS_NAME = "rakta_seva_prefs"
    private const val KEY_USERS = "users"
    private const val KEY_REQUESTS = "requests"
    private const val KEY_NOTIFS = "notifications"
    private const val KEY_CONNECTIONS = "connections"
    private const val KEY_CURRENT_USER = "current_user"

    private val users = mutableListOf<User>()
    private val requests = mutableListOf<BloodRequest>()
    private val notifications = mutableMapOf<String, MutableList<Notification>>()
    private val directRequestConnections = mutableSetOf<Pair<String, String>>()
    
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    var currentUser: User? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadData()
        
        if (users.isEmpty()) {
            users.add(User("Administrator", "admin@raktaseva.in", "+91 0000000000", "admin123", "O+", "Bangalore", true))
            users.add(User("Bhagyashree C", "bhagyashreeb461@gmail.com", "9113014547", "password", "AB+", "Bangalore", false, "90+ days ago", true))
            users.add(User("Rachana Test", "rachana_test@example.com", "9876543210", "password", "O+", "Bangalore", false, "Never", true))
            saveData()
        }
    }

    private fun loadData() {
        prefs.getString(KEY_USERS, null)?.let {
            val type = object : TypeToken<MutableList<User>>() {}.type
            users.clear()
            users.addAll(gson.fromJson(it, type))
        }

        prefs.getString(KEY_REQUESTS, null)?.let {
            val type = object : TypeToken<MutableList<BloodRequest>>() {}.type
            requests.clear()
            requests.addAll(gson.fromJson(it, type))
        }

        prefs.getString(KEY_NOTIFS, null)?.let {
            val type = object : TypeToken<MutableMap<String, MutableList<Notification>>>() {}.type
            notifications.clear()
            notifications.putAll(gson.fromJson(it, type))
        }

        prefs.getString(KEY_CONNECTIONS, null)?.let {
            val type = object : TypeToken<MutableSet<Pair<String, String>>>() {}.type
            directRequestConnections.clear()
            directRequestConnections.addAll(gson.fromJson(it, type))
        }

        prefs.getString(KEY_CURRENT_USER, null)?.let {
            currentUser = gson.fromJson(it, User::class.java)
        }
    }

    private fun saveData() {
        prefs.edit().apply {
            putString(KEY_USERS, gson.toJson(users))
            putString(KEY_REQUESTS, gson.toJson(requests))
            putString(KEY_NOTIFS, gson.toJson(notifications))
            putString(KEY_CONNECTIONS, gson.toJson(directRequestConnections))
            putString(KEY_CURRENT_USER, gson.toJson(currentUser))
            apply()
        }
    }

    fun registerUser(user: User): Boolean {
        if (users.any { it.email.equals(user.email, ignoreCase = true) }) return false
        users.add(user)
        saveData()
        return true
    }

    fun login(email: String, password: String): User? {
        val userIndex = users.indexOfFirst { it.email.equals(email, ignoreCase = true) && it.password == password }
        if (userIndex != -1) {
            val updatedUser = users[userIndex].copy(lastLogin = System.currentTimeMillis())
            users[userIndex] = updatedUser
            currentUser = updatedUser
            saveData()
            return updatedUser
        }
        return null
    }

    fun logout() {
        currentUser = null
        saveData()
    }

    fun updateAvailability(available: Boolean) {
        val email = currentUser?.email ?: return
        val userIndex = users.indexOfFirst { it.email == email }
        if (userIndex != -1) {
            val updatedUser = users[userIndex].copy(isAvailable = available)
            users[userIndex] = updatedUser
            currentUser = updatedUser
            saveData()
        }
    }

    fun getUsers(): List<User> = users

    fun addRequest(request: BloodRequest) {
        requests.add(0, request)
        saveData()
    }

    fun getRequests(): List<BloodRequest> = requests

    fun acceptRequest(requestId: String) {
        val requestIndex = requests.indexOfFirst { it.id == requestId }
        if (requestIndex != -1) {
            val updatedRequest = requests[requestIndex].copy(
                status = "ACCEPTED",
                donorEmail = currentUser?.email
            )
            requests[requestIndex] = updatedRequest
            
            addNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    title = "Donor Found!",
                    message = "${currentUser?.fullName} accepted your request for ${updatedRequest.bloodGroup}.",
                    forUserEmail = updatedRequest.requesterEmail,
                    requestId = updatedRequest.id
                )
            )
            saveData()
        }
    }

    fun getMyResponses(): List<BloodRequest> {
        return requests.filter { it.donorEmail == currentUser?.email }
    }

    fun addNotification(notification: Notification) {
        val email = notification.forUserEmail.lowercase()
        if (!notifications.containsKey(email)) {
            notifications[email] = mutableListOf()
        }
        notifications[email]?.add(0, notification)
        saveData()
    }

    fun getNotifications(): List<Notification> {
        val email = currentUser?.email?.lowercase() ?: return emptyList()
        return notifications[email] ?: emptyList()
    }

    fun updateNotificationStatus(notificationId: String, status: String) {
        val email = currentUser?.email?.lowercase() ?: return
        val list = notifications[email] ?: return
        val index = list.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val notif = list[index]
            list[index] = notif.copy(status = status)
            
            if (status == "ACCEPTED" && notif.type == "DIRECT_REQUEST" && notif.senderEmail != null) {
                // Ensure connections are lowercased for comparison
                directRequestConnections.add(Pair(notif.senderEmail.lowercase(), notif.forUserEmail.lowercase()))
                
                addNotification(
                    Notification(
                        id = UUID.randomUUID().toString(),
                        title = "Request Accepted!",
                        message = "${currentUser?.fullName} accepted your direct request. Contact info is now visible.",
                        forUserEmail = notif.senderEmail
                    )
                )
            }
            saveData()
        }
    }

    fun isContactShared(user1: String, user2: String): Boolean {
        val u1 = user1.lowercase()
        val u2 = user2.lowercase()
        return directRequestConnections.contains(Pair(u1, u2)) || 
               directRequestConnections.contains(Pair(u2, u1)) ||
               requests.any { (it.requesterEmail.lowercase() == u1 && it.donorEmail?.lowercase() == u2 && it.status == "ACCEPTED") ||
                             (it.requesterEmail.lowercase() == u2 && it.donorEmail?.lowercase() == u1 && it.status == "ACCEPTED") }
    }

    fun sendDirectRequestToDonor(targetDonorEmail: String, bloodGroup: String) {
        addNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                title = "Direct Donation Request!",
                message = "${currentUser?.fullName} is personally requesting you to donate ${bloodGroup}.",
                forUserEmail = targetDonorEmail,
                type = "DIRECT_REQUEST",
                senderEmail = currentUser?.email
            )
        )
    }

    fun getDummyDonors(bloodGroup: String): List<Donor> {
        return listOf(
            Donor("Aditya Sharma", bloodGroup, "1.2 km", "+91 9876543210"),
            Donor("Vikram Rathore", bloodGroup, "2.5 km", "+91 8765432109")
        )
    }
}