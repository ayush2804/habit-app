package com.ayushpandey.habitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ayushpandey.habitapp.data.HabitColors
import com.ayushpandey.habitapp.data.HabitTemplate
import com.ayushpandey.habitapp.data.HabitTemplates
import com.ayushpandey.habitapp.data.TemplateCategory

@Composable
fun TemplateBrowser(
    selectedCategory: TemplateCategory?,
    onCategoryChange: (TemplateCategory?) -> Unit,
    onPick: (HabitTemplate) -> Unit,
    onCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Start from a template",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "World-class starters with color, days, and reminder baked in — or build your own.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip(
                label = "All",
                selected = selectedCategory == null,
                accent = MaterialTheme.colorScheme.primary,
                onClick = { onCategoryChange(null) }
            )
            TemplateCategory.entries.forEach { cat ->
                CategoryChip(
                    label = cat.label,
                    selected = selectedCategory == cat,
                    accent = HabitColors.asCompose(cat.accentArgb),
                    onClick = { onCategoryChange(cat) }
                )
            }
        }

        val templates = if (selectedCategory == null) {
            HabitTemplates.all
        } else {
            HabitTemplates.all.filter { it.category == selectedCategory }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(templates, key = { it.id }) { template ->
                TemplateCard(template = template, onClick = { onPick(template) })
            }
        }

        TextButton(onClick = onCustom, modifier = Modifier.align(Alignment.End)) {
            Text("Custom habit instead")
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = accent.copy(alpha = 0.2f),
            selectedLabelColor = accent,
            selectedLeadingIconColor = accent
        )
    )
}

@Composable
fun TemplateCard(
    template: HabitTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = HabitColors.asCompose(template.colorArgb)
    val soft = HabitColors.softFor(template.colorArgb)
    Box(
        modifier = modifier
            .width(196.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(soft, Color.White, accent.copy(alpha = 0.18f)))
            )
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = templateIcon(template.iconKey),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                template.category.label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = accent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                template.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                template.blurb,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
