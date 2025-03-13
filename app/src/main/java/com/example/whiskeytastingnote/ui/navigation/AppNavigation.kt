// app/src/main/java/com/example/whiskeytastingnote/ui/navigation/AppNavigation.kt
package com.example.whiskeytastingnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whiskeytastingnote.ui.detail.NoteDetailScreen
import com.example.whiskeytastingnote.ui.home.HomeScreen
import com.example.whiskeytastingnote.ui.note.NoteEditorScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NoteEditor : Screen("note_editor?noteId={noteId}") {
        fun createRoute(noteId: Long? = null): String =
            if (noteId != null) "note_editor?noteId=$noteId" else "note_editor"
    }
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long): String = "note_detail/$noteId"
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onNewNoteClick = {
                    navController.navigate(Screen.NoteEditor.createRoute())
                }
            )
        }

        // Note editor screen
        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val noteIdArg = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val noteId = if (noteIdArg > 0) noteIdArg else null

            NoteEditorScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Note detail screen
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L

            NoteDetailScreen(
                noteId = noteId,
                onNavigateUp = {
                    navController.popBackStack()
                },
                onEditClick = { id ->
                    navController.navigate(Screen.NoteEditor.createRoute(id))
                },
                onDeleteConfirm = {
                    navController.popBackStack()
                }
            )
        }
    }
}