package com.egorpoprotskiy.note.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.egorpoprotskiy.note.home.HomeDestination
import com.egorpoprotskiy.note.home.HomeScreen
import com.egorpoprotskiy.note.note.NoteDetailsDestination
import com.egorpoprotskiy.note.note.NoteDetailsScreen
import com.egorpoprotskiy.note.note.NoteEditDestination
import com.egorpoprotskiy.note.note.NoteEditScreen
import com.egorpoprotskiy.note.note.NoteEntryDestination
import com.egorpoprotskiy.note.note.NoteEntryScreen

@Composable
fun NoteNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNoteEntry = {navController.navigate(NoteEntryDestination.route)},
                navigateToNoteUpdate = {navController.navigate("${NoteDetailsDestination.route}/${it}")}
            )
        }
        composable(route = NoteEntryDestination.route) {
            NoteEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = NoteDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDetailsDestination.noteIdArg) {
                type = NavType.IntType
            })
        ) {
            NoteDetailsScreen(
                navigateToEditNote = { navController.navigate("${NoteEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = NoteEditDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteEditDestination.noteIdArg) {
                type = NavType.IntType
            })
        ) {
            NoteEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
