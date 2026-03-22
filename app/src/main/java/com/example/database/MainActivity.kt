package com.example.database

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Lock)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Tables : Screen("tables", "Tables", Icons.Default.TableChart)
    object Students : Screen("students", "Students", Icons.Default.Person)
    object Halls : Screen("halls", "Halls", Icons.Default.LocationCity)
    object Staff : Screen("staff", "Staff", Icons.Default.Group)
    object Advisers : Screen("advisers", "Advisers", Icons.Default.SupportAgent)
    object Courses : Screen("courses", "Courses", Icons.Default.School)
    object Reports : Screen("reports", "Reports", Icons.AutoMirrored.Filled.List)
    object RandomQuery : Screen("random_query", "Query", Icons.Default.Terminal)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentAppTheme {
                MainContainer()
            }
        }
    }
}

@Composable
fun StudentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            onPrimary = Color.White,
            secondary = Color(0xFF26A69A),
            primaryContainer = Color(0xFFBBDEFB),
            onPrimaryContainer = Color(0xFF0D47A1),
            surfaceVariant = Color(0xFFF5F5F5)
        ),
        typography = Typography(),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: MainViewModel = viewModel()
    val isReady by viewModel.isReady.collectAsState()

    val bottomMenuItems = listOf(
        Screen.Dashboard,
        Screen.Tables,
        Screen.Reports,
        Screen.RandomQuery
    )

    val allScreens = listOf(
        Screen.Login, Screen.Dashboard, Screen.Tables, Screen.Students, Screen.Halls,
        Screen.Staff, Screen.Advisers, Screen.Courses, Screen.Reports, Screen.RandomQuery
    )

    Scaffold(
        topBar = {
            val showMainTopBar = currentRoute != null && 
                                currentRoute != Screen.Login.route &&
                                !currentRoute.startsWith("student_detail") && 
                                !currentRoute.startsWith("report_detail")
            
            if (showMainTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        val title = allScreens.find { it.route == currentRoute }?.title 
                            ?: "Details"
                        Text(title, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        val isBottomDest = bottomMenuItems.any { it.route == currentRoute }
                        if (!isBottomDest) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            val showBottomBar = currentRoute != null && 
                               currentRoute != Screen.Login.route &&
                               !currentRoute.startsWith("student_detail") && 
                               !currentRoute.startsWith("report_detail")
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomMenuItems.forEach { item ->
                        val isSelected = currentRoute == item.route || 
                                        (item == Screen.Tables && listOf("students", "halls", "staff", "advisers", "courses").contains(currentRoute))
                        
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(viewModel, onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) { DashboardScreen(navController, viewModel) }
            composable(Screen.Tables.route) { TablesScreen(navController) }
            composable(Screen.Students.route) { StudentsScreen(navController, viewModel) }
            composable(Screen.Halls.route) { HallsScreen(navController, viewModel) }
            composable(Screen.Staff.route) { StaffScreen(navController, viewModel) }
            composable(Screen.Advisers.route) { AdvisersScreen(viewModel) }
            composable(Screen.Courses.route) { CoursesScreen(viewModel) }
            composable(Screen.Reports.route) { ReportsScreen(navController) }
            composable(Screen.RandomQuery.route) { RandomQueryScreen(viewModel) }
            
            composable(
                "student_detail/{bannerNumber}",
                arguments = listOf(navArgument("bannerNumber") { type = NavType.StringType })
            ) { backStackEntry ->
                val banner = backStackEntry.arguments?.getString("bannerNumber")
                val students by viewModel.students.collectAsState()
                val student = students.find { it.bannerNumber == banner }
                student?.let {
                    StudentDetailScreen(it, onBack = { navController.popBackStack() })
                }
            }

            composable(
                "report_detail/{reportTitle}",
                arguments = listOf(navArgument("reportTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedTitle = backStackEntry.arguments?.getString("reportTitle") ?: ""
                val title = Uri.decode(encodedTitle)
                ReportDetailScreen(title, viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: MainViewModel, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isBackendAwake by viewModel.isBackendAwake.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.HomeWork,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "University Database",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!isBackendAwake) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Starting the server...", style = MaterialTheme.typography.bodyMedium)
                if (error != null) {
                    Text(error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    Button(onClick = { viewModel.pingServer() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Retry Connection")
                    }
                }
            } else {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.login(username, password) { if (it) onLoginSuccess() } },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Login")
                }
                
                AnimatedVisibility(visible = error != null) {
                    Text(
                        text = error ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TablesScreen(navController: NavController) {
    val tableItems = listOf(
        Screen.Students,
        Screen.Halls,
        Screen.Staff,
        Screen.Advisers,
        Screen.Courses
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Database Tables", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(tableItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(item.route) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val students by viewModel.students.collectAsState()
    val halls by viewModel.halls.collectAsState()
    val staff by viewModel.staff.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStudents()
        viewModel.fetchHalls()
        viewModel.fetchStaff()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Students", if(students.isEmpty()) "-" else students.size.toString(), Icons.Default.People, Modifier.weight(1f))
                StatCard("Halls", if(halls.isEmpty()) "-" else halls.size.toString(), Icons.Default.Business, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Staff", if(staff.isEmpty()) "-" else staff.size.toString(), Icons.Default.Group, Modifier.weight(1f))
                StatCard("Waiting", if(students.isEmpty()) "-" else students.count { it.status == "waiting" }.toString(), Icons.Default.HourglassEmpty, Modifier.weight(1f))
            }
        }

        item {
            Text("Quick Access", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ActionItem("View All Students", Icons.Default.Person) { navController.navigate(Screen.Students.route) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ActionItem("Unpaid Invoices Report", Icons.AutoMirrored.Filled.ReceiptLong) { navController.navigate("report_detail/${Uri.encode("Unpaid Invoices List")}") }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ActionItem("Occupancy Report", Icons.Default.BarChart) { navController.navigate("report_detail/${Uri.encode("Hall Occupancy")}") }
                }
            }
        }
    }
}

@Composable
fun StudentsScreen(navController: NavController, viewModel: MainViewModel) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) { viewModel.fetchStudents() }

    val filteredStudents = students.filter { 
        it.firstName.contains(searchQuery, ignoreCase = true) || 
        it.lastName.contains(searchQuery, ignoreCase = true) ||
        it.bannerNumber.contains(searchQuery)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search Students...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = CircleShape,
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        if (isLoading && students.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredStudents) { student ->
                    StudentListItem(student) { navController.navigate("student_detail/${student.bannerNumber}") }
                }
            }
        }
    }
}

@Composable
fun HallsScreen(navController: NavController, viewModel: MainViewModel) {
    val halls by viewModel.halls.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchHalls() }

    if (isLoading && halls.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(halls) { hall ->
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(hall.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(hall.address ?: "", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            InfoLabel("Phone", hall.telephone ?: "")
                            Spacer(modifier = Modifier.width(24.dp))
                            InfoLabel("Room", hall.managerStaffNumber ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaffScreen(navController: NavController, viewModel: MainViewModel) {
    val staff by viewModel.staff.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchStaff() }

    if (isLoading && staff.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(staff) { member ->
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("${member.firstName} ${member.lastName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(member.position ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Text(member.location ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvisersScreen(viewModel: MainViewModel) {
    val advisers by viewModel.advisers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchAdvisers() }

    if (isLoading && advisers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(advisers) { adviser ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(adviser.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(adviser.position ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Text(adviser.department ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            InfoLabel("Phone", adviser.internalPhone ?: "")
                            Spacer(modifier = Modifier.width(24.dp))
                            InfoLabel("Room", adviser.roomNumber ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoursesScreen(viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchCourses() }

    if (isLoading && courses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(courses) { course ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(course.courseTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(course.courseNumber, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Instructor: ${course.instructorName ?: ""}", style = MaterialTheme.typography.bodyMedium)
                        Text("Dept: ${course.departmentName ?: ""}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsScreen(navController: NavController) {
    val reports = listOf(
        "Hall Managers Report" to Icons.Default.SupervisorAccount,
        "Student Lease Agreements" to Icons.Default.Description,
        "Summer Semester Leases" to Icons.Default.WbSunny,
        "Unpaid Invoices List" to Icons.AutoMirrored.Filled.ReceiptLong,
        "Apartment Inspections" to Icons.AutoMirrored.Filled.FactCheck,
        "Hall Occupancy" to Icons.Default.Home,
        "Waiting List Details" to Icons.Default.FormatListNumbered,
        "Student Category Stats" to Icons.Default.PieChart,
        "Next-of-Kin Missing" to Icons.Default.NoAccounts,
        "Student Adviser Info" to Icons.Default.SupportAgent,
        "Senior Staff List" to Icons.Default.Face
    )

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(reports) { (title, icon) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("report_detail/${Uri.encode(title)}") },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun ReportDetailScreen(reportTitle: String, viewModel: MainViewModel, onBack: () -> Unit) {
    val results by viewModel.queryResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(reportTitle) {
        val query = when (reportTitle) {
            "Hall Managers Report" -> "SELECT * FROM Staff WHERE position LIKE '%Manager%'"
            "Student Lease Agreements" -> "SELECT * FROM Leases"
            "Summer Semester Leases" -> "SELECT * FROM Leases WHERE includes_summer = 1"
            "Unpaid Invoices List" -> "SELECT * FROM Invoices WHERE date_paid IS NULL"
            "Apartment Inspections" -> "SELECT * FROM Inspections"
            "Hall Occupancy" -> "SELECT hall_name, COUNT(*) as Occupants FROM Leases GROUP BY hall_name"
            "Waiting List Details" -> "SELECT * FROM Students WHERE status = 'waiting'"
            "Student Category Stats" -> "SELECT category, COUNT(*) as Count FROM Students GROUP BY category"
            "Next-of-Kin Missing" -> "SELECT banner_number, first_name, last_name FROM Students WHERE banner_number NOT IN (SELECT banner_number FROM Kin)"
            "Student Adviser Info" -> "SELECT s.first_name, s.last_name, a.full_name as Adviser FROM Students s JOIN Advisers a ON s.adviser_staff_number = a.staff_number"
            "Senior Staff List" -> "SELECT * FROM Staff WHERE position LIKE '%Senior%'"
            else -> "SELECT * FROM Students LIMIT 10"
        }
        viewModel.executeQuery(query)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text(reportTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error!!, color = Color.Red, modifier = Modifier.padding(16.dp)) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(results) { row ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            row.forEach { (key, value) ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("$key: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                                    Text(value, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(2f))
                                }
                                if (row.keys.last() != key) { HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RandomQueryScreen(viewModel: MainViewModel) {
    var queryText by remember { mutableStateOf("") }
    val results by viewModel.queryResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = queryText, onValueChange = { queryText = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Enter SQL Query") }, placeholder = { Text("e.g. SELECT * FROM Students") }, maxLines = 4)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.executeQuery(queryText) }, modifier = Modifier.fillMaxWidth(), enabled = queryText.isNotBlank() && !isLoading) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp) else Text("Execute Query")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (error != null) { Text(text = error!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium) }
        Text("Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (results.isEmpty() && !isLoading && error == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No data to display", color = Color.Gray) } }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results) { row ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            row.forEach { (key, value) ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("$key: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                                    Text(value, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(2f))
                                }
                                if (row.keys.last() != key) { HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun ActionItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun InfoLabel(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StudentListItem(student: Student, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Text(student.firstName.take(1) + student.lastName.take(1), color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${student.firstName} ${student.lastName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(student.bannerNumber, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            StatusBadge(student.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = if (status == "placed") Color(0xFF4CAF50) else Color(0xFFFF9800)
    Surface(color = color.copy(alpha = 0.1f), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, color)) {
        Text(status.uppercase(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StudentDetailScreen(student: Student, onBack: () -> Unit) {
    Scaffold(topBar = { @OptIn(ExperimentalMaterial3Api::class) TopAppBar(title = { Text("Profile Details") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Text(student.firstName.take(1) + student.lastName.take(1), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("${student.firstName} ${student.lastName}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(student.bannerNumber, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            }
            DetailCard("General Information") {
                DetailRow("Email", student.email ?: "")
                DetailRow("Phone", student.mobileNumber ?: "")
                DetailRow("Gender", student.gender ?: "")
                DetailRow("Nationality", student.nationality ?: "")
            }
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard("Academic Info") {
                DetailRow("Major", student.major ?: "")
                DetailRow("Category", student.category ?: "")
                DetailRow("Status", student.status)
            }
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard("Address") { Text("${student.street ?: ""}\n${student.city ?: ""}\n${student.postcode ?: ""}", style = MaterialTheme.typography.bodyLarge) }
        }
    }
}

@Composable
fun DetailCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = Color.Gray)
        Text(value, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium)
    }
}
