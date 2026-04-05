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
    @SerialName("mobile_phone") val mobilePhone: String? = null,
    val email: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerialName("student_category") val category: String? = null,
    val nationality: String? = null,
    @SerialName("special_needs") val specialNeeds: String? = null,
    @SerialName("additional_comments") val comments: String? = null,
    val status: String = "",
    val major: String? = null,
    val minor: String? = null,
    @SerialName("adviser_id") val adviserId: String? = null,
    @SerialName("course_id") val courseId: String? = null
)

@Serializable
data class Adviser(
    @SerialName("adviser_id") val adviserId: String = "",
    @SerialName("full_name") val fullName: String = "",
    val position: String? = null,
    @SerialName("department_name") val departmentName: String? = null,
    @SerialName("internal_phone") val internalPhone: String? = null,
    val email: String? = null,
    @SerialName("room_number") val roomNumber: String? = null
)

@Serializable
data class Hall(
    @SerialName("hall_id") val hallId: String = "",
    @SerialName("hall_name") val hallName: String = "",
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    val telephone: String? = null,
    @SerialName("manager_staff_id") val managerStaffId: String? = null
)

@Serializable
data class Room(
    @SerialName("place_number") val placeNumber: String = "",
    @SerialName("hall_id") val hallId: String? = null,
    @SerialName("room_number") val roomNumber: String = "",
    @SerialName("monthly_rent") val monthlyRent: Double = 0.0,
    @SerialName("apartment_id") val apartmentId: String? = null
)

@Serializable
data class Apartment(
    @SerialName("apartment_id") val apartmentId: String = "",
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    @SerialName("num_bedrooms") val numBedrooms: Int? = null
)

@Serializable
data class Lease(
    @SerialName("lease_id") val leaseId: String = "",
    @SerialName("banner_number") val bannerNumber: String = "",
    @SerialName("place_number") val placeNumber: String? = null,
    @SerialName("lease_duration_semesters") val duration: Int? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val address: String? = null,
    @SerialName("room_number") val roomNumber: String? = null
)

@Serializable
data class Invoice(
    @SerialName("invoice_id") val invoiceId: String = "",
    @SerialName("lease_id") val leaseId: String = "",
    val semester: Int? = null,
    @SerialName("payment_due") val paymentDue: Double? = null,
    @SerialName("banner_number") val bannerNumber: String? = null,
    @SerialName("place_number") val placeNumber: String? = null,
    @SerialName("room_number") val roomNumber: String? = null,
    val address: String? = null,
    @SerialName("date_paid") val datePaid: String? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    @SerialName("first_reminder_date") val firstReminderDate: String? = null,
    @SerialName("second_reminder_date") val secondReminderDate: String? = null
)

@Serializable
data class Inspection(
    @SerialName("inspection_id") val inspectionId: String = "",
    @SerialName("apartment_id") val apartmentId: String? = null,
    @SerialName("staff_id") val staffId: String? = null,
    @SerialName("inspection_date") val date: String? = null,
    val satisfactory: String? = "Yes",
    val comments: String? = null
)

@Serializable
data class Staff(
    @SerialName("staff_id") val staffId: String = "",
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
    @SerialName("course_id") val courseId: String = "",
    @SerialName("course_title") val courseTitle: String = "",
    @SerialName("course_year") val courseYear: Int? = null,
    @SerialName("instructor_name") val instructorName: String? = null,
    @SerialName("instructor_phone") val instructorPhone: String? = null,
    @SerialName("instructor_email") val instructorEmail: String? = null,
    @SerialName("instructor_room") val instructorRoom: String? = null,
    @SerialName("department_name") val departmentName: String? = null
)

@Serializable
data class NextOfKin(
    @SerialName("kin_id") val kinId: String = "",
    @SerialName("banner_number") val bannerNumber: String = "",
    val name: String? = null,
    val relationship: String? = null,
    val street: String? = null,
    val city: String? = null,
    val postcode: String? = null,
    val phone: String? = null
)

@Serializable
data class Place(
    @SerialName("place_number") val placeNumber: String = "",
    @SerialName("place_type") val placeType: String? = null
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
