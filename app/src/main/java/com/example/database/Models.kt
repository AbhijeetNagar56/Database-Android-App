package com.example.database

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Student(
    @SerialName("banner_number") val bannerNumber: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    @SerialName("mobile_number") val mobileNumber: String? = null,
    val email: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    val category: String? = null,
    val nationality: String? = null,
    @SerialName("special_needs") val specialNeeds: String? = null,
    val comments: String? = null,
    val status: String = "",
    val major: String? = null,
    val minor: String? = null,
    @SerialName("adviser_staff_number") val adviserStaffNumber: String? = null
)

@Serializable
data class Adviser(
    @SerialName("staff_number") val staffNumber: String = "",
    @SerialName("full_name") val fullName: String = "",
    val position: String? = null,
    val department: String? = null,
    @SerialName("internal_phone") val internalPhone: String? = null,
    val email: String? = null,
    @SerialName("room_number") val roomNumber: String? = null
)

@Serializable
data class Hall(
    val name: String = "",
    val address: String? = null,
    val telephone: String? = null,
    @SerialName("manager_staff_number") val managerStaffNumber: String? = null
)

@Serializable
data class Room(
    @SerialName("room_number") val roomNumber: String = "",
    @SerialName("place_number") val placeNumber: String = "",
    @SerialName("monthly_rent") val monthlyRent: Double = 0.0,
    @SerialName("hall_name") val hallName: String? = null,
    @SerialName("apartment_number") val apartmentNumber: String? = null
)

@Serializable
data class Apartment(
    @SerialName("apartment_number") val apartmentNumber: String = "",
    val address: String? = null,
    @SerialName("num_bedrooms") val numBedrooms: Int? = null
)

@Serializable
data class Lease(
    @SerialName("lease_number") val leaseNumber: String = "",
    val duration: Int? = null,
    @SerialName("banner_number") val bannerNumber: String = "",
    @SerialName("place_number") val placeNumber: String? = null,
    @SerialName("room_number") val roomNumber: String? = null,
    val address: String? = null,
    @SerialName("enter_date") val enterDate: String? = null,
    @SerialName("leave_date") val leaveDate: String? = null,
    @SerialName("includes_summer") val includesSummer: Int = 0 
)

@Serializable
data class Invoice(
    @SerialName("invoice_number") val invoiceNumber: String = "",
    @SerialName("lease_number") val leaseNumber: String = "",
    val semester: Int? = null,
    @SerialName("payment_due") val paymentDue: Double? = null,
    @SerialName("date_paid") val datePaid: String? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    @SerialName("first_reminder_sent") val firstReminderSent: String? = null,
    @SerialName("second_reminder_sent") val secondReminderSent: String? = null
)

@Serializable
data class Inspection(
    @SerialName("staff_name") val staffName: String? = null,
    @SerialName("apartment_number") val apartmentNumber: String? = null,
    val date: String? = null,
    @SerialName("is_satisfactory") val isSatisfactory: Int = 1,
    val comments: String? = null
)

@Serializable
data class Staff(
    @SerialName("staff_number") val staffNumber: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val email: String? = null,
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    val position: String? = null,
    val location: String? = null
)

@Serializable
data class Course(
    @SerialName("course_number") val courseNumber: String = "",
    @SerialName("course_title") val courseTitle: String = "",
    @SerialName("instructor_name") val instructorName: String? = null,
    @SerialName("instructor_phone") val instructorPhone: String? = null,
    @SerialName("instructor_email") val instructorEmail: String? = null,
    @SerialName("instructor_room") val instructorRoom: String? = null,
    @SerialName("department_name") val departmentName: String? = null
)

@Serializable
data class NextOfKin(
    @SerialName("banner_number") val bannerNumber: String = "",
    val name: String? = null,
    val relationship: String? = null,
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    val telephone: String? = null
)

@Serializable
data class QueryRequest(
    val query: String
)

@Serializable
data class QueryResponse(
    val results: List<Map<String, String>> = emptyList(),
    val error: String? = null
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class PingResponse(
    val status: String,
    val timestamp: String
)
