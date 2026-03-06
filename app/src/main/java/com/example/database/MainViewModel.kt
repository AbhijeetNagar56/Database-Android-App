package com.example.database

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    // These would be fetched from your backend in a real scenario
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _halls = MutableStateFlow<List<Hall>>(emptyList())
    val halls: StateFlow<List<Hall>> = _halls

    private val _staff = MutableStateFlow<List<Staff>>(emptyList())
    val staff: StateFlow<List<Staff>> = _staff

    private val _leases = MutableStateFlow<List<Lease>>(emptyList())
    val leases: StateFlow<List<Lease>> = _leases

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    init {
        // Load initial mock data
        loadMockData()
    }

    private fun loadMockData() {
        _students.value = listOf(
            Student("B001", "John", "Doe", "123 Main St", "London", "SW1", "0771234567", "john@example.com", "2000-01-01", "M", "1st Year", "British", status = "placed", major = "CS"),
            Student("B002", "Jane", "Smith", "456 High St", "Manchester", "M1", "0777654321", "jane@example.com", "1999-05-15", "F", "Postgraduate", "Spanish", status = "waiting", major = "Physics")
        )
        
        _halls.value = listOf(
            Hall("West Hall", "University Rd", "0121123456", "Staff001"),
            Hall("East Hall", "Park Lane", "0121654321", "Staff002")
        )

        _staff.value = listOf(
            Staff("Staff001", "Alice", "Manager", "alice@univ.com", "1 Manager Ave", "London", "SW2", "1960-10-10", "F", "Hall Manager", "West Hall"),
            Staff("Staff002", "Bob", "Assistant", "bob@univ.com", "2 Admin Ln", "London", "SW3", "1955-12-12", "M", "Administrative Assistant", "East Hall")
        )
    }

    // Example Report Logic (f) - Unsatisfactory inspections
    fun getUnsatisfactoryInspections(inspections: List<Inspection>): List<Inspection> {
        return inspections.filter { !it.isSatisfactory }
    }

    // Example Report Logic (h) - Waiting List
    fun getWaitingList(): List<Student> {
        return _students.value.filter { it.status == "waiting" }
    }
}
