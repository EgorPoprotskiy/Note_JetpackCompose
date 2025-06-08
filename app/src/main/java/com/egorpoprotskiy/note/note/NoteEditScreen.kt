package com.egorpoprotskiy.note.note

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.note.AppViewModelProvider
import com.egorpoprotskiy.note.NoteTopAppBar
import com.egorpoprotskiy.note.R
import com.egorpoprotskiy.note.navigation.NavigationDestination
import com.egorpoprotskiy.note.ui.theme.NoteTheme
import kotlinx.coroutines.launch


object NoteEditDestination : NavigationDestination {
    override val route = "note_edit"
    override val titleRes = R.string.edit_note_title
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen (
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.factory)
    ) {
        //12.8 Создайте val с именем coroutineScopeи установите его в rememberCoroutineScope().
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                NoteTopAppBar(
                    title = stringResource(NoteEditDestination.titleRes),
                    canNavigateBack = true,
                    navigateUp = onNavigateUp
                )
            },
            modifier = modifier
        ) { innerPadding ->
            NoteEntryBody(
                itemUiState = viewModel.noteUiState,
                //12.6 Установите onItemValueChange значение аргумента. -> ItemEditViewModel
                onItemValueChange = viewModel::updateUiState,
                //12.9 обновите onSaveClickаргумент функции, чтобы запустить сопрограмму в coroutineScope.
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.updateItem()
                        navigateBack()
                    }
                },
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding()
                    )
                    .verticalScroll(rememberScrollState())
            )
        }
    }



    @Preview(showBackground = true)
    @Composable
    fun NoteEditScreenPreview() {
        NoteTheme {
            NoteEditScreen(navigateBack = { /*Do nothing*/ }, onNavigateUp = { /*Do nothing*/ })
        }
    }