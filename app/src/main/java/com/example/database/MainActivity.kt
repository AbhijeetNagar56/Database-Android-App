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
import java.util.Locale

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val tableName: String? = null) {
    object Login : Screen("login", "Login", Icons.Default.Lock)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Tables : Screen("tables", "Tables", Icons.Default.TableChart)
    
    object Students : Screen("students", "Students", Icons.Default.Person, "students")
    object Halls : Screen("halls", "Halls", Icons.Default.LocationCity, "halls")
    object Staff : Screen("staff", "Staff", Icons.Default.Group, "staff")
    object Advisers : Screen("advisers", "Advisers", Icons.Default.SupportAgent, "advisers")
    object Courses : Screen("courses", "Courses", Icons.Default.School, "courses")
    object HallRooms : Screen("hallrooms", "Hall Rooms", Icons.Default.MeetingRoom, "hallrooms")
    object Apartments : Screen("apartments", "Apartments", Icons.Default.Apartment, "apartments")
    object ApartmentRooms : Screen("apartmentrooms", "Apartment Rooms", Icons.Default.Bed, "apartmentrooms")
    object Leases : Screen("leases", "Leases", Icons.Default.Description, "leases")
    object Invoices : Screen("invoices", "Invoices", Icons.Default.Receipt, "invoices")
    object Inspections : Screen("inspections", "Inspections", Icons.AutoMirrored.Filled.FactCheck, "inspections")
    object Kin : Screen("kin", "Next of Kin", Icons.Default.FamilyRestroom, "kin")
    object Places : Screen("places", "Places", Icons.Default.Place, "places")
    
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

    val bottomMenuItems = listOf(
        Screen.Dashboard,
        Screen.Tables,
        Screen.Reports,
        Screen.RandomQuery
    )

    val tableScreens = listOf(
        Screen.Students, Screen.Halls, Screen.Staff, Screen.Advisers, Screen.Courses,
        Screen.HallRooms, Screen.Apartments, Screen.ApartmentRooms, Screen.Leases,
        Screen.Invoices, Screen.Inspections, Screen.Kin, Screen.Places
    )

    Scaffold(
        topBar = {
            val showMainTopBar = currentRoute != null && 
                                currentRoute != Screen.Login.route &&
                                !currentRoute.startsWith("detail/") && 
                                !currentRoute.startsWith("report_detail")
            
            if (showMainTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        val allScreens = listOf(Screen.Login, Screen.Dashboard, Screen.Tables, Screen.Reports, Screen.RandomQuery) + tableScreens
                        val title = allScreens.find { it.route == currentRoute }?.title ?: "Details"
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
                               !currentRoute.startsWith("detail/") && 
                               !currentRoute.startsWith("report_detail")
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomMenuItems.forEach { item ->
                        val isSelected = currentRoute == item.route || 
                                        (item == Screen.Tables && tableScreens.any { it.route == currentRoute })
                        
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
            
            tableScreens.forEach { screen ->
                composable(screen.route) {
                    TableDataListScreen(screen, viewModel, navController)
                }
            }

            composable(
                "detail/{tableName}/{id}",
                arguments = listOf(
                    navArgument("tableName") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val tableName = backStackEntry.arguments?.getString("tableName") ?: ""
                val id = backStackEntry.arguments?.getString("id") ?: ""
                EntityDetailScreen(tableName, id, viewModel, onBack = { navController.popBackStack() })
            }

            composable(Screen.Reports.route) { ReportsScreen(navController) }
            composable(Screen.RandomQuery.route) { RandomQueryScreen(viewModel) }
            
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
fun TableDataListScreen(screen: Screen, viewModel: MainViewModel, navController: NavController) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val dataFlow = when(screen) {
        Screen.Students -> viewModel.students
        Screen.Halls -> viewModel.halls
        Screen.Staff -> viewModel.staff
        Screen.Advisers -> viewModel.advisers
        Screen.Courses -> viewModel.courses
        Screen.HallRooms -> viewModel.hallRooms
        Screen.Apartments -> viewModel.apartments
        Screen.ApartmentRooms -> viewModel.apartmentRooms
        Screen.Leases -> viewModel.leases
        Screen.Invoices -> viewModel.invoices
        Screen.Inspections -> viewModel.inspections
        Screen.Kin -> viewModel.kin
        Screen.Places -> viewModel.places
        else -> null
    }

    LaunchedEffect(Unit) {
        when(screen) {
            Screen.Students -> viewModel.fetchStudents()
            Screen.Halls -> viewModel.fetchHalls()
            Screen.Staff -> viewModel.fetchStaff()
            Screen.Advisers -> viewModel.fetchAdvisers()
            Screen.Courses -> viewModel.fetchCourses()
            Screen.HallRooms -> viewModel.fetchHallRooms()
            Screen.Apartments -> viewModel.fetchApartments()
            Screen.ApartmentRooms -> viewModel.fetchApartmentRooms()
            Screen.Leases -> viewModel.fetchLeases()
            Screen.Invoices -> viewModel.fetchInvoices()
            Screen.Inspections -> viewModel.fetchInspections()
            Screen.Kin -> viewModel.fetchKin()
            Screen.Places -> viewModel.fetchPlaces()
            else -> {}
        }
    }

    val listData by dataFlow?.collectAsState() ?: remember { mutableStateOf(emptyList<Any>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && listData.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listData) { item ->
                    val (title, sub, id) = when(item) {
                        is Student -> Triple("${item.firstName} ${item.lastName}", "Banner: ${item.bannerNumber}", item.bannerNumber)
                        is Hall -> Triple(item.hallName, "ID: ${item.hallId}", item.hallId)
                        is Staff -> Triple("${item.firstName} ${item.lastName}", "Staff ID: ${item.staffId}", item.staffId)
                        is Adviser -> Triple(item.fullName, "ID: ${item.adviserId}", item.adviserId)
                        is Course -> Triple(item.courseTitle, "Code: ${item.courseId}", item.courseId)
                        is Room -> Triple("Room ${item.roomNumber}", "Place: ${item.placeNumber}", item.placeNumber)
                        is Apartment -> Triple("Apartment ID: ${item.apartmentId}", "Bedrooms: ${item.numBedrooms}", item.apartmentId)
                        is Lease -> Triple("Lease: ${item.leaseId}", "Student: ${item.bannerNumber}", item.leaseId)
                        is Invoice -> Triple("Invoice: ${item.invoiceId}", "Lease: ${item.leaseId}", item.invoiceId)
                        is Inspection -> Triple("Inspection: ${item.inspectionId}", "Date: ${item.date}", item.inspectionId)
                        is NextOfKin -> Triple(item.name ?: "Unknown", "Kin ID: ${item.kinId}", item.kinId)
                        is Place -> Triple("Place: ${item.placeNumber}", "Type: ${item.placeType}", item.placeNumber)
                        else -> Triple("Unknown", "", "")
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                navController.navigate("detail/${screen.tableName}/$id")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(title.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(sub, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EntityDetailScreen(tableName: String, id: String, viewModel: MainViewModel, onBack: () -> Unit) {
    val detail by viewModel.detailResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(tableName, id) {
        viewModel.fetchEntityDetail(tableName, id)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("${tableName.capitalize()} Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.surfaceVariant)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(error!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (detail != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            detail!!.forEach { (key, value) ->
                                val label = key.replace("_", " ").split(" ").joinToString(" ") { 
                                    it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else it }
                                }
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
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
        Screen.Students, Screen.Halls, Screen.Staff, Screen.Advisers, Screen.Courses,
        Screen.HallRooms, Screen.Apartments, Screen.ApartmentRooms, Screen.Leases,
        Screen.Invoices, Screen.Inspections, Screen.Kin, Screen.Places
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
        "Senior Staff List" to Icons.Default.Face,
        "Total Rent Paid" to Icons.Default.AttachMoney,
        "Hall Rent Statistics" to Icons.Default.BarChart,
        "Hall Place Capacity" to Icons.Default.Numbers
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
        val endpoint = when (reportTitle) {
            "Hall Managers Report" -> "/reports/hall-managers"
            "Student Lease Agreements" -> "/reports/students-leases"
            "Summer Semester Leases" -> "/reports/summer-leases"
            "Unpaid Invoices List" -> "/reports/unpaid-invoices"
            "Apartment Inspections" -> "/reports/unsatisfactory-inspections"
            "Hall Occupancy" -> "/reports/hall-students/1" // Example hallId
            "Waiting List Details" -> "/reports/waiting-list"
            "Student Category Stats" -> "/reports/student-category-count"
            "Next-of-Kin Missing" -> "/reports/students-without-kin"
            "Student Adviser Info" -> "/reports/student-adviser/1" // Example bannerNumber
            "Senior Staff List" -> "/reports/senior-staff"
            "Total Rent Paid" -> "/reports/student-rent/1" // Example bannerNumber
            "Hall Rent Statistics" -> "/reports/hall-rent-stats"
            "Hall Place Capacity" -> "/reports/hall-place-count"
            else -> null
        }
        
        if (endpoint != null) {
            viewModel.fetchReport(endpoint)
        } else {
            viewModel.executeQuery("SELECT * FROM Students LIMIT 10")
        }
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
                DetailRow("Phone", student.mobilePhone ?: "")
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
