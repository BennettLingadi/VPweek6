package com.example.vpweek6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { CampusApp() } }
    }
}

private data class TopLevel(val label: String, val route: Any)

@Composable
fun CampusApp() {
    val navController = rememberNavController()
    val vm: CampusViewModel = viewModel()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val tabs = listOf(
        TopLevel("Dashboard", DashboardRoute),
        TopLevel("Communities", CommunitiesRoute)
    )
    val dest = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = dest?.hasRoute(DashboardRoute::class) == true ||
            dest?.hasRoute(CommunitiesRoute::class) == true

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (showBottomBar) NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = dest?.hierarchy?.any { it.hasRoute(tab.route::class) } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { inner ->
        NavHost(navController, startDestination = DashboardRoute) {
            composable<DashboardRoute> {
                DashboardScreen(vm, Modifier.padding(inner))
            }
            composable<CommunitiesRoute> {
                CommunitiesScreen(
                    vm = vm, modifier = Modifier.padding(inner),
                    onOpen = { navController.navigate(ClubProjectsRoute(it)) },
                    onBlocked = {
                        scope.launch {
                            snackbar.currentSnackbarData?.dismiss()
                            snackbar.showSnackbar("Harap bergabung ke komunitas terlebih dahulu!", "OK")
                        }
                    }
                )
            }
            composable<ClubProjectsRoute> { entry ->
                val route = entry.toRoute<ClubProjectsRoute>()
                ClubProjectsScreen(
                    vm, route.communityId,
                    onBack = { navController.popBackStack() },
                    onOpenProject = { navController.navigate(ProjectDetailRoute(it)) }
                )
            }
            composable<ProjectDetailRoute> { entry ->
                val route = entry.toRoute<ProjectDetailRoute>()
                ProjectDetailScreen(vm, route.projectId, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CampusAppPreview() {
    MaterialTheme { CampusApp() }
}
 