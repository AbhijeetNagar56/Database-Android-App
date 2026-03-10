package com.example.database

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val bannerNumber: String,
    val firstName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val postcode: String,
    val mobileNumber: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String,
    val category: String, // e.g., first-year undergraduate
    val nationality: String,
    val specialNeeds: String? = null,
    val comments: String? = null,
    val status: String, // placed/waiting
    val major: String,
    val minor: String? = null,
    val adviserStaffNumber: String? = null
)

@Serializable
data class Adviser(
    val staffNumber: String,
    val fullName: String,
    val position: String,
    val department: String,
    val internalPhone: String,
    val email: String,
    val roomNumber: String
)

@Serializable
data class Hall(
    val name: String,
    val address: String,
    val telephone: String,
    val managerStaffNumber: String
)

@Serializable
data class Room(
    val roomNumber: String,
    val placeNumber: String,
    val monthlyRent: Double,
    val hallName: String? = null,
    val apartmentNumber: String? = null
)

@Serializable
data class Apartment(
    val apartmentNumber: String,
    val address: String,
    val numBedrooms: Int
)

@Serializable
data class Lease(
    val leaseNumber: String,
    val duration: Int, // semesters
    val bannerNumber: String,
    val placeNumber: String,
    val roomNumber: String,
    val address: String,
    val enterDate: String,
    val leaveDate: String? = null,
    val includesSummer: Boolean = false
)

@Serializable
data class Invoice(
    val invoiceNumber: String,
    val leaseNumber: String,
    val semester: Int,
    val paymentDue: Double,
    val datePaid: String? = null,
    val paymentMethod: String? = null,
    val firstReminderSent: String? = null,
    val secondReminderSent: String? = null
)

@Serializable
data class Inspection(
    val staffName: String,
    val apartmentNumber: String,
    val date: String,
    val isSatisfactory: Boolean,
    val comments: String? = null
)

@Serializable
data class Staff(
    val staffNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val street: String,
    val city: String,
    val postcode: String,
    val dateOfBirth: String,
    val gender: String,
    val position: String,
    val location: String
)

@Serializable
data class Course(
    val courseNumber: String,
    val courseTitle: String,
    val instructorName: String,
    val instructorPhone: String,
    val instructorEmail: String,
    val instructorRoom: String,
    val departmentName: String
)

@Serializable
data class NextOfKin(
    val bannerNumber: String,
    val name: String,
    val relationship: String,
    val street: String,
    val city: String,
    val postcode: String,
    val telephone: String
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
