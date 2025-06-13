package com.egorpoprotskiy.note.note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.note.AppViewModelProvider
import com.egorpoprotskiy.note.NoteTopAppBar
import com.egorpoprotskiy.note.R
import com.egorpoprotskiy.note.data.Note
import com.egorpoprotskiy.note.navigation.NavigationDestination
import com.egorpoprotskiy.note.ui.theme.NoteTheme
import kotlinx.coroutines.launch

object NoteEntryDestination : NavigationDestination {
    override val route = "note_entry"
    override val titleRes = R.string.note_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: NoteEntryViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    //6.5 создайте val именованный объект coroutineScope с rememberCoroutineScope()составной функцией.
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            NoteTopAppBar(
                title = stringResource(NoteEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        NoteEntryBody(
            itemUiState = viewModel.noteUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                //6.6 Вызов coroutineScope, созданного в п6.5.
                coroutineScope.launch {
                    viewModel.saveItem()
                    //6.7 Возврат на предыдущий экран после нажатия кнопки "save"
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
                .fillMaxWidth()
        )
    }
}

@Composable
fun NoteEntryBody(
    itemUiState: NoteUiState,
    onItemValueChange: (NoteDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        NoteInputForm(
            itemDetails = itemUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth(),
        )
        //Кнопка сохранить
        Button(
            onClick = onSaveClick,
            //Кнопка активна, если isEntryValid = true(все поля не пустые)
            enabled = itemUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun NoteInputForm(
    itemDetails: NoteDetails,
    modifier: Modifier = Modifier,
    onValueChange: (NoteDetails) -> Unit = {},
    enabled: Boolean = true
) {
    val colorResList = listOf(
        R.color.orange,
        R.color.blue,
        R.color.green,
        R.color.pink,
        R.color.white
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        val maxChars = 30
        OutlinedTextField(
            value = itemDetails.heading,
            //Позволяет ввести в заголовок не более 30 символов.
            onValueChange = {
                if (it.length <= maxChars) {
                onValueChange(itemDetails.copy(heading = it))
                }
                            },
            label = { Text(stringResource(R.string.name_note)) },
            //Цвет поля ввода в зависимости от фокуса.
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.description,
            onValueChange = { onValueChange(itemDetails.copy(description = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.description_note)) },
            //Цвет поля ввода в зависимости от фокуса.
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//            ),

//            leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            //Запись в одну строку.
//            singleLine = true
            maxLines = 10 // или любое нужное число строк
        )

        Spacer(modifier = Modifier.weight(1f)) //толкает все поля вниз.
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                15.dp,
                //Чтобы цвета были посередине экрана.
                Alignment.CenterHorizontally)
        ) {
            colorResList.forEach { colorResId ->
                val color = colorResource(id = colorResId)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (itemDetails.color == colorResId) 3.dp else 1.dp,
                            color = if (itemDetails.color == colorResId) Color.Black else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable(enabled = enabled) {
                            onValueChange(itemDetails.copy(color = colorResId))
                        }
                )
            }
        }

//        if (enabled) {
//            Text(
//                text = stringResource(R.string.required_fields),
//                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
//            )
//        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    NoteTheme {
        NoteEntryBody(itemUiState = NoteUiState(
            NoteDetails(
                heading = "Имя", description = "описание", color = 5
            )
        ), onItemValueChange = {}, onSaveClick = {})
    }
}