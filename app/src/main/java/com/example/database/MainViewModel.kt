package com.example.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    private val baseUrl = "https://university-database.onrender.com"

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(300, TimeUnit.SECONDS)
                readTimeout(300, TimeUnit.SECONDS)
                writeTimeout(300, TimeUnit.SECONDS)
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpCookies)
        install(HttpTimeout) {
            requestTimeoutMillis = 300000 
            connectTimeoutMillis = 300000
            socketTimeoutMillis = 300000
        }
    }

    private val _queryResults = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val queryResults: StateFlow<List<Map<String, String>>> = _queryResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady
    
    private val _isBackendAwake = MutableStateFlow(false)
    val isBackendAwake: StateFlow<Boolean> = _isBackendAwake

    private val _detailResult = MutableStateFlow<Map<String, String>?>(null)
    val detailResult: StateFlow<Map<String, String>?> = _detailResult

    // State flows for all entities
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _advisers = MutableStateFlow<List<Adviser>>(emptyList())
    val advisers: StateFlow<List<Adviser>> = _advisers

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _staff = MutableStateFlow<List<Staff>>(emptyList())
    val staff: StateFlow<List<Staff>> = _staff

    private val _halls = MutableStateFlow<List<Hall>>(emptyList())
    val halls: StateFlow<List<Hall>> = _halls

    private val _hallRooms = MutableStateFlow<List<Room>>(emptyList())
    val hallRooms: StateFlow<List<Room>> = _hallRooms

    private val _apartments = MutableStateFlow<List<Apartment>>(emptyList())
    val apartments: StateFlow<List<Apartment>> = _apartments

    private val _apartmentRooms = MutableStateFlow<List<Room>>(emptyList())
    val apartmentRooms: StateFlow<List<Room>> = _apartmentRooms

    private val _leases = MutableStateFlow<List<Lease>>(emptyList())
    val leases: StateFlow<List<Lease>> = _leases

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    private val _inspections = MutableStateFlow<List<Inspection>>(emptyList())
    val inspections: StateFlow<List<Inspection>> = _inspections

    private val _kin = MutableStateFlow<List<NextOfKin>>(emptyList())
    val kin: StateFlow<List<NextOfKin>> = _kin

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    init {
        pingServer()
    }

    fun pingServer() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                client.get("$baseUrl/ping") {
                    timeout {
                        requestTimeoutMillis = 300000
                        socketTimeoutMillis = 300000
                    }
                }
                _isBackendAwake.value = true
            } catch (e: Exception) {
                _error.value = "Server is taking too long to start. Please retry."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val loginResponse = client.post("$baseUrl/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(username, password))
                }

                if (loginResponse.status == HttpStatusCode.OK) {
                    _isReady.value = true
                    onResult(true)
                } else {
                    _error.value = "Invalid username or password"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.localizedMessage}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchReport(endpoint: String) {
        if (!_isReady.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _queryResults.value = emptyList()
            try {
                val response: JsonElement = client.get(baseUrl + endpoint).body()
                
                if (response is JsonArray) {
                    val list = response.map { element ->
                        element.jsonObject.mapValues { it.value.jsonPrimitive.contentOrNull ?: "null" }
                    }
                    _queryResults.value = list
                } else if (response is JsonObject) {
                    val map = response.mapValues { it.value.jsonPrimitive.contentOrNull ?: "null" }
                    _queryResults.value = listOf(map)
                }
            } catch (e: Exception) {
                _error.value = "Report Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchEntityDetail(tableName: String, id: String) {
        if (!_isReady.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _detailResult.value = null
            try {
                val response: JsonElement = client.get("$baseUrl/$tableName/$id").body()
                if (response is JsonObject) {
                    _detailResult.value = response.mapValues { it.value.jsonPrimitive.contentOrNull ?: "null" }
                } else if (response is JsonArray && response.isNotEmpty()) {
                    _detailResult.value = response[0].jsonObject.mapValues { it.value.jsonPrimitive.contentOrNull ?: "null" }
                }
            } catch (e: Exception) {
                _error.value = "Detail Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // On-demand fetch methods
    fun fetchStudents() = fetchData<List<Student>>("/students") { _students.value = it }
    fun fetchAdvisers() = fetchData<List<Adviser>>("/advisers") { _advisers.value = it }
    fun fetchCourses() = fetchData<List<Course>>("/courses") { _courses.value = it }
    fun fetchStaff() = fetchData<List<Staff>>("/staff") { _staff.value = it }
    fun fetchHalls() = fetchData<List<Hall>>("/halls") { _halls.value = it }
    fun fetchHallRooms() = fetchData<List<Room>>("/hallrooms") { _hallRooms.value = it }
    fun fetchApartments() = fetchData<List<Apartment>>("/apartments") { _apartments.value = it }
    fun fetchApartmentRooms() = fetchData<List<Room>>("/apartmentrooms") { _apartmentRooms.value = it }
    fun fetchLeases() = fetchData<List<Lease>>("/leases") { _leases.value = it }
    fun fetchInvoices() = fetchData<List<Invoice>>("/invoices") { _invoices.value = it }
    fun fetchInspections() = fetchData<List<Inspection>>("/inspections") { _inspections.value = it }
    fun fetchKin() = fetchData<List<NextOfKin>>("/kin") { _kin.value = it }
    fun fetchPlaces() = fetchData<List<Place>>("/places") { _places.value = it }

    private inline fun <reified T> fetchData(endpoint: String, crossinline onSuccess: (T) -> Unit) {
        if (!_isReady.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = client.get(baseUrl + endpoint)
                if (response.status == HttpStatusCode.OK) {
                    onSuccess(response.body())
                } else {
                    _error.value = "Server error: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun executeQuery(queryText: String) {
        if (!_isReady.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _queryResults.value = emptyList()
            
            try {
                val response: JsonElement = client.post("$baseUrl/query") {
                    contentType(ContentType.Application.Json)
                    setBody(QueryRequest(query = queryText))
                }.body()

                if (response is JsonObject && response.containsKey("error")) {
                    _error.value = response["error"]?.jsonPrimitive?.content
                } else if (response is JsonArray) {
                    val list = response.map { element ->
                        element.jsonObject.mapValues { it.value.jsonPrimitive.contentOrNull ?: "null" }
                    }
                    _queryResults.value = list
                }
            } catch (e: Exception) {
                _error.value = "Query Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
