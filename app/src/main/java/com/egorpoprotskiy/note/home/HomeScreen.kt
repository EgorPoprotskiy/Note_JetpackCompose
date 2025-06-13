package com.egorpoprotskiy.note.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.egorpoprotskiy.note.navigation.NavigationDestination
import com.egorpoprotskiy.note.ui.theme.NoteTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Состояния видимости для каждого элемента по id
    val visibleMap = remember { mutableStateMapOf<Int, Boolean>() }
    // Инициализация видимости для всех элементов (если ещё не инициализирована)
    LaunchedEffect(noteList) {
        noteList.forEach { note ->
            if (!visibleMap.containsKey(note.id)) {
                visibleMap[note.id] = true
            }
        }
    }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            items(items = noteList, key = { it.id }) { item ->
                var visible = visibleMap[item.id] ?: true
                //9 Защита от повторного свайпа при настройках анимации.
                val alreadyDismissed = remember { mutableStateOf(false) }
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (!alreadyDismissed.value &&
                            dismissValue == DismissValue.DismissedToStart ||
                            dismissValue == DismissValue.DismissedToEnd
                        ) {
                            alreadyDismissed.value = true

                            visibleMap[item.id] = false // запускаем анимацию исчезновения
                            false // НЕ удаляем сразу — ждём завершения анимации
                        } else {
                            false
                        }
                    }
                )
                // Анимация элемента списка
                AnimatedVisibility(
                    visible = visible,
                    //плавное появление элемента списка.
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(
                        animationSpec = tween(
                            300
                        )
                    ),
                    //плавное исчезновение элемента списка.
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                ) {
                    // Когда элемент стал невидимым — вызываем удаление
                    LaunchedEffect(visible) {
                        if (!visible) {
                            delay(300) // Ждём окончания анимации

                            // Показываем Snackbar и ждём результата
                            val result = scope.launch {
                                val snackbarResult = snackbarHostState.showSnackbar(
                                    message = "Заметка удалена",
                                    actionLabel = "Отмена",
                                    duration = SnackbarDuration.Short
                                )
                                if (snackbarResult == SnackbarResult.ActionPerformed) {
                                    // Отмена удаления — восстанавливаем элемент
                                    visibleMap[item.id] = true
                                    alreadyDismissed.value = false
                                } else {
                                    // Подтверждаем удаление
                                    onSwipeDelete(item)
                                }
                            }
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
                                    //отступ иконки от краёв экрана.
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
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                                    .clickable { onNoteClick(item) }
                            )
                        }
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}



@Composable
private fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier
) {
    val cardColor = colorResource(id = note.color)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        //назначение вцета для карточки, на основе выбранного цвета.
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            //Строка с заголовком
            Text(
                text = note.heading,
                style = MaterialTheme.typography.titleLarge,
                //автоматически подбирает цвет текста (чёрный или белый) для хорошей читаемости на фоне карточки.
                color = contentColorFor(cardColor)
            )
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
        }
    }
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
            Note(1, "Game", "100.0", 20)
        )
    }
}