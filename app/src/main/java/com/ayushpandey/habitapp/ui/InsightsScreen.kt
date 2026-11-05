package com.ayushpandey.habitapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: HabitViewModel,
    onBack: () -> Unit
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refreshStats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                "A quiet look at your consistency — not a dashboard of noise.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            val s = stats
            if (s == null) {
                Text("Loading…", style = MaterialTheme.typography.bodyLarge)
            } else {
                InsightRow(
                    title = "Today",
                    body = "${s.completedToday} of ${s.dueToday} due habits completed"
                )
                InsightRow(
                    title = "Active habits",
                    body = "${s.activeHabits}"
                )
                InsightRow(
                    title = "7-day completion",
                    body = "${(s.completionRate7d * 100).roundToInt()}%"
                )
                InsightRow(
                    title = "Longest streak",
                    body = "${s.longestStreak} days"
                )
                InsightRow(
                    title = "Total check-ins",
                    body = "${s.totalCheckIns}"
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InsightRow(title: String, body: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.headlineLarge)
    }
}
