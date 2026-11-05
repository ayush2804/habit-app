package com.ayushpandey.habitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ayushpandey.habitapp.data.HabitColors
import com.ayushpandey.habitapp.data.HabitOutcome
import com.ayushpandey.habitapp.data.ScheduleDays

@Composable
fun WeekStrip(
    outcomes: List<HabitOutcome?>,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScheduleDays.LABELS.forEachIndexed { index, label ->
            val outcome = outcomes.getOrNull(index)
            val bg = when (outcome) {
                HabitOutcome.COMPLETED -> accent
                HabitOutcome.FAILED -> MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (outcome == null) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    } else {
                        Color.White
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleDayPicker(
    scheduleDays: Int,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..7).forEach { dayIso ->
            val selected = ScheduleDays.isScheduled(scheduleDays, dayIso)
            FilterChip(
                selected = selected,
                onClick = { onToggle(dayIso) },
                label = { Text(ScheduleDays.LABELS[dayIso - 1]) }
            )
        }
    }
}

@Composable
fun ColorPicker(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HabitColors.options.forEach { color ->
            val compose = HabitColors.asCompose(color)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(compose)
                    .then(
                        if (color == selected) {
                            Modifier.border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelect(color) }
            )
        }
    }
}
