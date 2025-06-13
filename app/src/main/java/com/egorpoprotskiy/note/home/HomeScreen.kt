package com.egorpoprotskiy.note.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.note.AppViewModelProvider
import com.egorpoprotskiy.note.NoteTopAppBar
import com.egorpoprotskiy.note.R
import com.egorpoprotskiy.note.data.Note
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.egorpoprotskiy.note.navigation.NavigationDestination
import com.egorpoprotskiy.note.ui.theme.NoteTheme
import kotlinx.coroutines.delay

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

//4.1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToNoteEntry: () -> Unit,
    navigateToNoteUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            NoteTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNoteEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.note_entry_title)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            noteList = homeUiState.noteList,
            onNoteClick = navigateToNoteUpdate,
            onSwipeDelete = { note -> viewModel.deleteNote(note) },
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    noteList: List<Note>,
    onNoteClick: (Int) -> Unit,
    onSwipeDelete: (Note) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (noteList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_note_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            NoteList(
                noteList = noteList,
                onNoteClick = { onNoteClick(it.id) },
                onSwipeDelete = onSwipeDelete,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NoteList(
    noteList: List<Note>,
    onNoteClick: (Note) -> Unit,
    onSwipeDelete: (Note) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = noteList, key = { it.id }) { item ->
            var visible by remember { mutableStateOf(true) }
            //9 Защита от повторного свайпа при настройках анимации.
            val alreadyDismissed = remember { mutableStateOf(false) }
            val dismissState = rememberDismissState(
                confirmStateChange = { dismissValue ->
                    if (!alreadyDismissed.value &&
                        dismissValue == DismissValue.DismissedToStart ||
                        dismissValue == DismissValue.DismissedToEnd
                    ) {
                        alreadyDismissed.value = true

                        visible = false // запускаем анимацию исчезновения
//                        onSwipeDelete(item)
                        false // НЕ удаляем сразу — ждём завершения анимации
                    } else {
                        false
                    }
                }
            )
            // Анимация исчезновения элемента спи"ска
            AnimatedVisibility(
                visible = visible,
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            ) {
                // Когда элемент стал невидимым — вызываем удаление
                LaunchedEffect(visible) {
                    if (!visible) {
                        delay(300) // дождаться завершения анимации
                        onSwipeDelete(item) //вызывается после анимации.
                    }
                }

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(
                    DismissDirection.StartToEnd,
                    DismissDirection.EndToStart
                ),
                background = {
                    val direction = dismissState.dismissDirection
                    // Цвет фона для свайпа.
                    val color = when (direction) {
                        DismissDirection.StartToEnd -> Color.Red
                        DismissDirection.EndToStart -> Color.Red
                        null -> Color.Transparent
                    }
                    //Чтобы иконка удаления элемента была с обеих сторон во время свайпа.
                    val alignment = when (dismissState.dismissDirection) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                        null -> Alignment.CenterEnd
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = Color.White
                        )
                    }
                },
                dismissContent = {
                    NoteItem(
                        note = item,
//                        onColorSelected = {},
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .clickable { onNoteClick(item) }
                    )
                }
            )
        }}
    }
}

@Composable
private fun NoteItem(
    note: Note,
//    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
//    val availableColors = listOf("orange", "blue", "green", "pink", "White")
    val cardColor = colorResource(id = note.color)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
//            Column(
//                modifier = Modifier.fillMaxSize()
//            ) {
            //Строка с заголовком
            Text(
                text = note.heading,
                style = MaterialTheme.typography.titleLarge,
                //автоматически подбирает цвет текста (чёрный или белый) для хорошей читаемости на фоне карточки.
                color = contentColorFor(cardColor)
            )
//            Spacer(Modifier.weight(1f))
            //Разделитель
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
                //Строка с описанием
            Text(
                text = note.description,
                style = MaterialTheme.typography.titleMedium,
                color = contentColorFor(cardColor)
            )
//            }
            //Строка с ценой
//            Text(
//                text = note.color,
//                style = MaterialTheme.typography.titleMedium
//            )

            //Выбор цвета на главном экране(неправильный функционал))
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Выберите цвет:",
//                style = MaterialTheme.typography.labelMedium
//            )
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                availableColors.forEach { colorName ->
//                    val color = colorFromName(colorName)
//                    Box(
//                        modifier = Modifier
//                            .size(36.dp)
//                            .clip(CircleShape)
//                            .background(color)
//                            .border(
//                                width = if (note.color == colorName) 3.dp else 1.dp,
//                                color = if (note.color == colorName) Color.Black else Color.Gray,
//                                shape = CircleShape
//                            )
//                            .clickable {onColorSelected(colorName)}
//                    )
//                }
//            }
        }
    }
}

//10 Функция получения цвета из ресурса.
@Composable
fun colorFromName(colorname: String): Color {
    val context = LocalContext.current
    val colorResId = when (colorname) {
        "orange" -> R.color.orange
        "blue" -> R.color.blue
        "green" -> R.color.green
        "pink" -> R.color.pink
        else -> R.color.defaultColor // Цвет по умолчанию
    }
    return colorResource(id = colorResId)
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    NoteTheme {
        HomeBody(
            listOf(
                Note(1, "Game", "100.0", 20),
                Note(2, "Pen", "200.0", 30),
                Note(3, "TV", "300.0", 50)
            ), onNoteClick = {}, onSwipeDelete = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    NoteTheme {
        HomeBody(listOf(), onNoteClick = {}, onSwipeDelete = {})
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NoteTheme {
        NoteItem(
            Note(1, "Game", "100.0", 20),
//            onColorSelected = {}
        )
    }
}