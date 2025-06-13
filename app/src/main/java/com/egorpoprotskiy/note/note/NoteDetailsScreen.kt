package com.egorpoprotskiy.note.note

import android.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.note.AppViewModelProvider
import com.egorpoprotskiy.note.NoteTopAppBar
import com.egorpoprotskiy.note.R
import com.egorpoprotskiy.note.navigation.NavigationDestination
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.note.data.Note
import com.egorpoprotskiy.note.ui.theme.NoteTheme
import kotlinx.coroutines.launch

//5.1
object NoteDetailsDestination : NavigationDestination {
    override val route = "note_details"
    override val titleRes = R.string.item_detail_title
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    navigateToEditNote: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            NoteTopAppBar(
                title = stringResource(NoteDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }, floatingActionButton = {
            Row {
                //Кнопка-иконка для удаления.
//                FloatingActionButton(
//                    onClick = {
//                        coroutineScope.launch {
//                            viewModel.deleteNote()
//                            navigateBack()
//                        }
//                    },
//                    shape = androidx.compose.material.MaterialTheme.shapes.medium,
//                    modifier = Modifier.padding(
//                        start = WindowInsets.safeDrawing.asPaddingValues()
//                            .calculateEndPadding(LocalLayoutDirection.current)
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = stringResource(R.string.delete)
//                    )
//                }

                FloatingActionButton(
                    onClick = { navigateToEditNote(uiState.value.noteDetails.id) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_note_title)
                    )
                }


            }
        }, modifier = modifier
    ) { innerPadding ->
        NoteDetailsBody(
            noteDetailsUiState = uiState.value,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteNote()
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

@Composable
private fun NoteDetailsBody(
    noteDetailsUiState: NoteDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            NoteDetails(
                note = noteDetailsUiState.noteDetails.toItem(),
                modifier = Modifier.fillMaxWidth()
            )
//        Button(
//            onClick = onSellItem,
//            modifier = Modifier.fillMaxWidth(),
//            shape = MaterialTheme.shapes.small,
//            //13 Исправлено!!!(кнопка "sell" теперь не активна, если товаров 0)
////            enabled = true
//            enabled = !itemDetailsUiState.outOfStock
//        ) {
//            Text(stringResource(R.string.sell))
//        }
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun NoteDetails(
    note: Note,
    modifier: Modifier = Modifier
) {
    val cardColor = colorResource(id = note.color)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            NoteDetailsRow(
                noteDetail = note.heading,
                backgroundColor = contentColorFor(cardColor),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            Divider(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            NoteDetailsRow(
                noteDetail = note.description,
                backgroundColor = contentColorFor(cardColor),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
}

@Composable
private fun NoteDetailsRow(
    noteDetail: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val textColor = contentColorFor(backgroundColor)
    Row(modifier = modifier) {
//        Text(stringResource(labelResID))
//        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = noteDetail,
            color = textColor,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        })
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsBodyPreview() {
    NoteTheme {
        NoteDetailsBody(
            NoteDetailsUiState(
                outOfStock = true,
                noteDetails = NoteDetails(1, "Pen", "$100", 10)
            ),
//            onSellItem = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsScreenPreview() {
    NoteTheme {
        NoteDetailsScreen(
            navigateToEditNote = {},
            navigateBack = {},
        )
    }
}