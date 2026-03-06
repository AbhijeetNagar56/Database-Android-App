package com.example.database

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

data class Adviser(
    val staffNumber: String,
    val fullName: String,
    val position: String,
    val department: String,
    val internalPhone: String,
    val email: String,
    val roomNumber: String
)

data class Hall(
    val name: String,
    val address: String,
    val telephone: String,
    val managerStaffNumber: String
)

data class Room(
    val roomNumber: String,
    val placeNumber: String,
    val monthlyRent: Double,
    val hallName: String? = null,
    val apartmentNumber: String? = null
)

data class Apartment(
    val apartmentNumber: String,
    val address: String,
    val numBedrooms: Int
)

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

data class Inspection(
    val staffName: String,
    val apartmentNumber: String,
    val date: String,
    val isSatisfactory: Boolean,
    val comments: String? = null
)

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

data class Course(
    val courseNumber: String,
    val courseTitle: String,
    val instructorName: String,
    val instructorPhone: String,
    val instructorEmail: String,
    val instructorRoom: String,
    val departmentName: String
)

data class NextOfKin(
    val bannerNumber: String,
    val name: String,
    val relationship: String,
    val street: String,
    val city: String,
    val postcode: String,
    val telephone: String
)
