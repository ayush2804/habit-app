package com.ayushpandey.habitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayushpandey.habitapp.data.CheckInEntity
import com.ayushpandey.habitapp.data.HabitColors
import com.ayushpandey.habitapp.data.HabitOutcome
import com.ayushpandey.habitapp.data.ScheduleDays
import com.ayushpandey.habitapp.data.StreakCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    viewModel: HabitDetailViewModel,
    onBack: () -> Unit
) {
    val habit by viewModel.habit.collectAsStateWithLifecycle()
    val checkIns by viewModel.checkIns.collectAsStateWithLifecycle()
    val edit by viewModel.edit.collectAsStateWithLifecycle()
    val current = habit

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(current?.title ?: "Habit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (current != null) {
                        IconButton(onClick = { viewModel.startEdit(current) }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.archive(current, onBack) }) {
                            Icon(Icons.Outlined.Inventory2, contentDescription = "Archive")
                        }
                        IconButton(onClick = { viewModel.delete(current, onBack) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (current == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Habit not found")
            }
            return@Scaffold
        }

        val accent = HabitColors.asCompose(current.colorArgb)
        val week = StreakCalculator.weekOutcomes(checkIns)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(
                                    HabitColors.softFor(current.colorArgb),
                                    accent.copy(alpha = 0.35f),
                                    HabitColors.softFor(current.colorArgb)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        current.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatChip(
                        icon = {
                            Icon(
                                Icons.Outlined.LocalFireDepartment,
                                null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = "Streak",
                        value = "${current.currentStreak}"
                    )
                    StatChip(label = "Best", value = "${current.bestStreak}")
                    StatChip(label = "Check-ins", value = "${checkIns.size}")
                }
            }
            item {
                Text("This week", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                WeekStrip(outcomes = week, accent = accent)
            }
            item {
                Text("Schedule", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    (1..7).filter { ScheduleDays.isScheduled(current.scheduleDays, it) }
                        .joinToString(" · ") { ScheduleDays.FULL_LABELS[it - 1] }
                        .ifEmpty { "No days selected" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (current.reminderEnabled) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Reminder %02d:%02d".format(current.reminderHour, current.reminderMinute),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
            item {
                Text("Reflection history", style = MaterialTheme.typography.titleLarge)
            }
            if (checkIns.isEmpty()) {
                item {
                    Text(
                        "No check-ins yet. Complete or miss a day from the home list to leave a note.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                items(checkIns, key = { it.id }) { checkIn ->
                    CheckInRow(checkIn)
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    edit?.let { state ->
        val h = habit ?: return@let
        AlertDialog(
            onDismissRequest = viewModel::dismissEdit,
            title = { Text("Edit habit") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.updateEdit { s -> s.copy(title = it, error = null) } },
                        label = { Text("Name") },
                        singleLine = true,
                        isError = state.error != null,
                        supportingText = state.error?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ColorPicker(
                        selected = state.colorArgb,
                        onSelect = { c -> viewModel.updateEdit { it.copy(colorArgb = c) } }
                    )
                    ScheduleDayPicker(
                        scheduleDays = state.scheduleDays,
                        onToggle = { d ->
                            viewModel.updateEdit {
                                it.copy(scheduleDays = ScheduleDays.toggle(it.scheduleDays, d))
                            }
                        }
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reminder")
                        Switch(
                            checked = state.reminderEnabled,
                            onCheckedChange = { v ->
                                viewModel.updateEdit { it.copy(reminderEnabled = v) }
                            }
                        )
                    }
                    if (state.reminderEnabled) {
                        ReminderTimeRow(
                            hour = state.reminderHour,
                            minute = state.reminderMinute,
                            onChange = { hour, minute ->
                                viewModel.updateEdit {
                                    it.copy(reminderHour = hour, reminderMinute = minute)
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.saveEdit(h) }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissEdit) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    icon: (@Composable () -> Unit)? = null
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()
            Text(value, style = MaterialTheme.typography.headlineLarge)
        }
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun CheckInRow(checkIn: CheckInEntity) {
    val date = LocalDate.ofEpochDay(checkIn.epochDay)
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    val accent = when (checkIn.outcome) {
        HabitOutcome.COMPLETED -> MaterialTheme.colorScheme.primary
        HabitOutcome.FAILED -> MaterialTheme.colorScheme.error
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accent)
                .align(Alignment.Top)
                .padding(top = 6.dp)
        )
        Column {
            Text(
                "${date.format(formatter)} · ${
                    when (checkIn.outcome) {
                        HabitOutcome.COMPLETED -> "Completed"
                        HabitOutcome.FAILED -> "Missed"
                    }
                }",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                checkIn.note,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}
