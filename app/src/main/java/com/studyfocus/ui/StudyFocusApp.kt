package com.studyfocus.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.studyfocus.ui.navigation.Screen
import com.studyfocus.ui.navigation.bottomNavItems
import com.studyfocus.ui.screens.home.HomeScreen
import com.studyfocus.ui.screens.pomodoro.PomodoroScreen
import com.studyfocus.ui.screens.projects.ProjectDetailScreen
import com.studyfocus.ui.screens.projects.ProjectsScreen
import com.studyfocus.ui.screens.task.TaskCreateScreen
import com.studyfocus.ui.screens.today.TodayScreen
import com.studyfocus.ui.theme.Dimens

@Composable
fun StudyFocusApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main screens
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon
                                    else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    },
                    onNavigateToPomodoro = { taskId ->
                        navController.navigate(Screen.Pomodoro.createRoute(taskId))
                    },
                    onNavigateToCreateTask = {
                        navController.navigate(Screen.TaskCreate.createRoute())
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(Screen.TaskCreate.createRoute(taskId = taskId))
                    }
                )
            }

            // Today
            composable(Screen.Today.route) {
                TodayScreen(
                    onNavigateToPomodoro = { taskId ->
                        navController.navigate(Screen.Pomodoro.createRoute(taskId))
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(Screen.TaskCreate.createRoute(taskId = taskId))
                    }
                )
            }

            // Pomodoro
            composable(
                route = Screen.Pomodoro.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                PomodoroScreen(taskId = taskId)
            }

            // Projects
            composable(Screen.Projects.route) {
                ProjectsScreen(
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    },
                    onNavigateToPomodoro = { taskId ->
                        navController.navigate(Screen.Pomodoro.createRoute(taskId))
                    }
                )
            }

            // Project Detail
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: return@composable
                ProjectDetailScreen(
                    projectId = projectId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPomodoro = { taskId ->
                        navController.navigate(Screen.Pomodoro.createRoute(taskId))
                    },
                    onNavigateToCreateTask = {
                        navController.navigate(Screen.TaskCreate.createRoute(projectId))
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(Screen.TaskCreate.createRoute(taskId = taskId))
                    }
                )
            }

            // Task Create
            composable(
                route = Screen.TaskCreate.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    },
                    navArgument("taskId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId")
                val taskId = backStackEntry.arguments?.getLong("taskId")
                TaskCreateScreen(
                    projectId = if (projectId != null && projectId > 0) projectId else null,
                    taskId = if (taskId != null && taskId > 0) taskId else null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
