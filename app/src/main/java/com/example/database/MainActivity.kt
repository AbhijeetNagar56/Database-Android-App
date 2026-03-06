package com.example.database

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Students : Screen("students", "Students", Icons.Default.Person)
    object Halls : Screen("halls", "Halls", Icons.Default.LocationCity)
    object Staff : Screen("staff", "Staff", Icons.Default.Group)
    object Reports : Screen("reports", "Reports", Icons.AutoMirrored.Filled.List)
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
            secondary = Color(0xFF26A69A),
            primaryContainer = Color(0xFFE3F2FD)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val menuItems = listOf(
        Screen.Dashboard,
        Screen.Students,
        Screen.Halls,
        Screen.Staff,
        Screen.Reports
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Accommodation Office",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            menuItems.find { it.route == currentRoute }?.title ?: "Accommodation Office"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Dashboard.route) { DashboardScreen(navController) }
                composable(Screen.Students.route) { StudentsScreen(navController) }
                composable(Screen.Halls.route) { HallsScreen(navController) }
                composable(Screen.Staff.route) { StaffScreen(navController) }
                composable(Screen.Reports.route) { ReportsScreen(navController) }
                
                // Detailed student view
                composable(
                    "student_detail/{bannerNumber}",
                    arguments = listOf(navArgument("bannerNumber") { type = NavType.StringType })
                ) { backStackEntry ->
                    val banner = backStackEntry.arguments?.getString("bannerNumber")
                    // In a real app, fetch this from a ViewModel
                    StudentDetailScreen(banner ?: "", onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Welcome to the Admin Portal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Placed", "150", Modifier.weight(1f))
            StatCard("Waiting", "45", Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Quick Links", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                QuickLinkItem("Unpaid Invoices", Icons.Default.Warning) {
                    // Navigate to a filtered report
                }
            }
            item {
                QuickLinkItem("Pending Inspections", Icons.Default.Build) {
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickLinkItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) // Using available icon
        }
    }
}

@Composable
fun StudentsScreen(navController: NavController) {
    // Simple list for now, will connect to backend
    Text("Student Management List here", modifier = Modifier.padding(16.dp))
}

@Composable
fun HallsScreen(navController: NavController) {
    Text("Halls of Residence List here", modifier = Modifier.padding(16.dp))
}

@Composable
fun StaffScreen(navController: NavController) {
    Text("Staff Management here", modifier = Modifier.padding(16.dp))
}

@Composable
fun ReportsScreen(navController: NavController) {
    val reports = listOf(
        "Hall Managers Report",
        "Student Leases",
        "Summer Semester Leases",
        "Total Rent by Student",
        "Unpaid Invoices",
        "Unsatisfactory Inspections",
        "Students in Hall",
        "Waiting List",
        "Student Categories",
        "Missing Next-of-Kin",
        "Student Advisers",
        "Hall Rent Stats",
        "Hall Capacities",
        "Senior Staff"
    )

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(reports) { report ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { /* Navigate to specific report */ },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(report, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun StudentDetailScreen(banner: String, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text("Details for $banner", style = MaterialTheme.typography.headlineMedium)
    }
}
