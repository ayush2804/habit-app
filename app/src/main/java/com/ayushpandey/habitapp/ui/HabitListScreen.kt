package com.ayushpandey.habitapp.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayushpandey.habitapp.data.HabitColors
import com.ayushpandey.habitapp.data.HabitEntity
import com.ayushpandey.habitapp.data.HabitOutcome
import com.ayushpandey.habitapp.data.HabitWithToday
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    viewModel: HabitViewModel,
    onOpenInsights: () -> Unit,
    onOpenDetail: (Long) -> Unit
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val dueToday = habits.filter { viewModel.isDueToday(it.habit) }
    val doneToday = dueToday.count { it.todayCheckIn?.outcome == HabitOutcome.COMPLETED }
    val progress = if (dueToday.isEmpty()) 0f else doneToday.toFloat() / dueToday.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Habit App", style = MaterialTheme.typography.titleLarge)
                        Text(
                            if (dueToday.isEmpty()) "Build a rhythm that sticks"
                            else "$doneToday of ${dueToday.size} due today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenInsights) {
                        Icon(Icons.Outlined.Insights, contentDescription = "Insights")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (dueToday.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
                Spacer(Modifier.height(4.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (habits.isEmpty()) {
                    EmptyHabitsHint(Modifier.fillMaxSize().padding(32.dp))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(habits, key = { it.habit.id }) { item ->
                            HabitRow(
                                item = item,
                                dueToday = viewModel.isDueToday(item.habit),
                                onCheckIn = { viewModel.onHabitCheckIn(item.habit) },
                                onOpen = { onOpenDetail(item.habit.id) },
                                onUndo = { viewModel.undoToday(item.habit) }
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
    }

    if (ui.showAddDialog) {
        AddHabitDialog(
            state = ui,
            onTitleChange = viewModel::onAddTitleChange,
            onColorChange = viewModel::onAddColorChange,
            onScheduleToggle = viewModel::onAddScheduleToggle,
            onReminderEnabled = viewModel::onAddReminderEnabled,
            onReminderTime = viewModel::onAddReminderTime,
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

    // Touch stats so Compose tracks it for subtitle refresh when returning
    stats
}

@Composable
private fun EmptyHabitsHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("No habits yet", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text(
            "Add a habit, set the days it matters, and optionally a reminder. Each check-in captures how it went — that’s how streaks grow.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun HabitRow(
    item: HabitWithToday,
    dueToday: Boolean,
    onCheckIn: () -> Unit,
    onOpen: () -> Unit,
    onUndo: () -> Unit
) {
    val habit = item.habit
    val today = item.todayCheckIn
    val accent = HabitColors.asCompose(habit.colorArgb)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    when (today?.outcome) {
                        HabitOutcome.COMPLETED -> accent
                        HabitOutcome.FAILED -> MaterialTheme.colorScheme.error
                        null -> accent.copy(alpha = 0.18f)
                    }
                )
                .clickable(onClick = onCheckIn),
            contentAlignment = Alignment.Center
        ) {
            when (today?.outcome) {
                HabitOutcome.COMPLETED -> Icon(
                    Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                HabitOutcome.FAILED -> Icon(
                    Icons.Default.Close,
                    contentDescription = "Missed",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                null -> Box(
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onOpen)
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (habit.currentStreak > 0) {
                    Icon(
                        Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        "${habit.currentStreak} day streak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        when {
                            !dueToday -> "Not scheduled today"
                            today == null -> "Tap the circle to check in"
                            else -> "Tap for details"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }

        if (today != null) {
            TextButton(onClick = onUndo) { Text("Undo") }
        }
    }
}

@Composable
private fun AddHabitDialog(
    state: HomeUiState,
    onTitleChange: (String) -> Unit,
    onColorChange: (Int) -> Unit,
    onScheduleToggle: (Int) -> Unit,
    onReminderEnabled: (Boolean) -> Unit,
    onReminderTime: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New habit") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.addTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Habit name") },
                    singleLine = true,
                    isError = state.addError != null,
                    supportingText = state.addError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Color", style = MaterialTheme.typography.labelLarge)
                ColorPicker(selected = state.addColor, onSelect = onColorChange)
                Text("Days", style = MaterialTheme.typography.labelLarge)
                ScheduleDayPicker(
                    scheduleDays = state.addSchedule,
                    onToggle = onScheduleToggle
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Daily reminder")
                    Switch(
                        checked = state.addReminderEnabled,
                        onCheckedChange = onReminderEnabled
                    )
                }
                if (state.addReminderEnabled) {
                    ReminderTimeRow(
                        hour = state.addReminderHour,
                        minute = state.addReminderMinute,
                        onChange = onReminderTime
                    )
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ReminderTimeRow(
    hour: Int,
    minute: Int,
    onChange: (Int, Int) -> Unit
) {
    val label = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Time $label", modifier = Modifier.weight(1f))
        TextButton(onClick = {
            val next = (hour + 1) % 24
            onChange(next, minute)
        }) { Text("+1h") }
        TextButton(onClick = {
            val nextMin = (minute + 15) % 60
            val nextHour = if (minute + 15 >= 60) (hour + 1) % 24 else hour
            onChange(nextHour, nextMin)
        }) { Text("+15m") }
    }
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
                Text("How did it go?", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onCompleted, modifier = Modifier.fillMaxWidth()) {
                    Text("Completed")
                }
                OutlinedButton(onClick = onFailed, modifier = Modifier.fillMaxWidth()) {
                    Text("Missed")
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
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
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
