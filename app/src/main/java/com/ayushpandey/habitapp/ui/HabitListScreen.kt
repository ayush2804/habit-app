package com.ayushpandey.habitapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayushpandey.habitapp.data.HabitEntity
import com.ayushpandey.habitapp.data.HabitOutcome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(viewModel: HabitViewModel) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Habit App", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Track what matters today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::openAddDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add habit")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = habits.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyHabitsHint(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }

            AnimatedVisibility(
                visible = habits.isNotEmpty(),
                enter = fadeIn() + slideInVertically { it / 8 },
                exit = fadeOut()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(habits, key = { it.id }) { habit ->
                        HabitRow(
                            habit = habit,
                            onClick = { viewModel.onHabitTapped(habit) }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                    item { Spacer(Modifier.height(88.dp)) }
                }
            }
        }
    }

    if (ui.showAddDialog) {
        AddHabitDialog(
            title = ui.addTitle,
            error = ui.addError,
            onTitleChange = viewModel::onAddTitleChange,
            onDismiss = viewModel::dismissAddDialog,
            onSave = viewModel::saveNewHabit
        )
    }

    ui.showOutcomePicker?.let { habit ->
        OutcomePickerDialog(
            habitTitle = habit.title,
            onDismiss = viewModel::dismissOutcomePicker,
            onCompleted = { viewModel.chooseOutcome(HabitOutcome.COMPLETED) },
            onFailed = { viewModel.chooseOutcome(HabitOutcome.FAILED) }
        )
    }

    val reflectionHabit = ui.reflectionTarget
    val outcome = ui.pendingOutcome
    if (reflectionHabit != null && outcome != null) {
        ReflectionDialog(
            habitTitle = reflectionHabit.title,
            outcome = outcome,
            note = ui.reflectionNote,
            error = ui.reflectionError,
            onNoteChange = viewModel::onReflectionNoteChange,
            onDismiss = viewModel::dismissReflection,
            onSave = viewModel::saveReflection
        )
    }
}

@Composable
private fun EmptyHabitsHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "No habits yet",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Tap + to add your first habit. When you check in, you’ll jot a quick reflection — how it felt, or what to improve.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun HabitRow(
    habit: HabitEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutcomeBadge(outcome = habit.lastOutcome)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = statusLabel(habit.lastOutcome),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun OutcomeBadge(outcome: HabitOutcome?) {
    val (bg, icon) = when (outcome) {
        HabitOutcome.COMPLETED -> MaterialTheme.colorScheme.primary to Icons.Default.Check
        HabitOutcome.FAILED -> MaterialTheme.colorScheme.error to Icons.Default.Close
        null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f) to null
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun statusLabel(outcome: HabitOutcome?): String = when (outcome) {
    HabitOutcome.COMPLETED -> "Completed — tap to check in again"
    HabitOutcome.FAILED -> "Missed — tap to check in again"
    null -> "Tap to mark done or missed"
}

@Composable
private fun AddHabitDialog(
    title: String,
    error: String?,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add habit") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Habit name") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSave() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun OutcomePickerDialog(
    habitTitle: String,
    onDismiss: () -> Unit,
    onCompleted: () -> Unit,
    onFailed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(habitTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "How did it go?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = onCompleted,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Completed") }
                OutlinedButton(
                    onClick = onFailed,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Missed") }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ReflectionDialog(
    habitTitle: String,
    outcome: HabitOutcome,
    note: String,
    error: String?,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val prompt = when (outcome) {
        HabitOutcome.COMPLETED -> "How do you feel after completing this?"
        HabitOutcome.FAILED -> "What got in the way, and what could you try next time?"
    }
    val label = when (outcome) {
        HabitOutcome.COMPLETED -> "How you feel"
        HabitOutcome.FAILED -> "Reason & improvement"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(habitTitle) },
        text = {
            Column {
                Text(
                    prompt,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text(label) },
                    minLines = 3,
                    maxLines = 6,
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
