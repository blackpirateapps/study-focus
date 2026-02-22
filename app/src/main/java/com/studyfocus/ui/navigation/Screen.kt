package com.studyfocus.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Today : Screen(
        route = "today",
        title = "Today",
        selectedIcon = Icons.Filled.Today,
        unselectedIcon = Icons.Outlined.Today
    )
    data object Pomodoro : Screen(
        route = "pomodoro?taskId={taskId}",
        title = "Pomodoro",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    ) {
        fun createRoute(taskId: Long = 0) = "pomodoro?taskId=$taskId"
    }
    data object Projects : Screen(
        route = "projects",
        title = "Projects",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    )
    data object ProjectDetail : Screen(
        route = "project/{projectId}",
        title = "Project",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    ) {
        fun createRoute(projectId: Long) = "project/$projectId"
    }
    data object TaskCreate : Screen(
        route = "task/create?projectId={projectId}&taskId={taskId}",
        title = "New Task",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add
    ) {
        fun createRoute(projectId: Long? = null, taskId: Long? = null) = 
            "task/create?projectId=${projectId ?: 0}&taskId=${taskId ?: 0}"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Today,
    Screen.Pomodoro,
    Screen.Projects
)
