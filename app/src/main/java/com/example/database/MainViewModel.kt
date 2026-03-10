package com.example.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

class MainViewModel : ViewModel() {
    private val baseUrl = "https://university-database.onrender.com"

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val _queryResults = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val queryResults: StateFlow<List<Map<String, String>>> = _queryResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // State flows for main entities
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _staff = MutableStateFlow<List<Staff>>(emptyList())
    val staff: StateFlow<List<Staff>> = _staff

    private val _halls = MutableStateFlow<List<Hall>>(emptyList())
    val halls: StateFlow<List<Hall>> = _halls

    fun fetchStudents() {
        if (_students.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _students.value = client.get("$baseUrl/students").body()
            } catch (e: Exception) {
                _error.value = "Students Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchStaff() {
        if (_staff.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _staff.value = client.get("$baseUrl/staff").body()
            } catch (e: Exception) {
                _error.value = "Staff Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchHalls() {
        if (_halls.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _halls.value = client.get("$baseUrl/halls").body()
            } catch (e: Exception) {
                _error.value = "Halls Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun executeQuery(queryText: String) {
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
