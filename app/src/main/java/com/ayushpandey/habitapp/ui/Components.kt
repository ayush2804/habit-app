package com.ayushpandey.habitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
                    .size(34.dp)
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
    accent: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..7).forEach { dayIso ->
            val selected = ScheduleDays.isScheduled(scheduleDays, dayIso)
            FilterChip(
                selected = selected,
                onClick = { onToggle(dayIso) },
                label = { Text(ScheduleDays.LABELS[dayIso - 1]) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent.copy(alpha = 0.22f),
                    selectedLabelColor = accent
                )
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
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HabitColors.swatches.forEach { swatch ->
            val compose = HabitColors.asCompose(swatch.argb)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(compose)
                        .then(
                            if (swatch.argb == selected) {
                                Modifier.border(2.5.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                            } else {
                                Modifier.border(1.dp, compose.copy(alpha = 0.35f), CircleShape)
                            }
                        )
                        .clickable { onSelect(swatch.argb) }
                )
            }
        }
    }
}
