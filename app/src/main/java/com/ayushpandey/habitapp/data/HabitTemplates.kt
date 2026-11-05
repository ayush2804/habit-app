package com.ayushpandey.habitapp.data

enum class TemplateCategory(val label: String, val accentArgb: Int) {
    ENERGY("Energy", HabitColors.Sun.argb),
    BODY("Body", HabitColors.Meadow.argb),
    MIND("Mind", HabitColors.Ocean.argb),
    FOCUS("Focus", HabitColors.IndigoNight.argb),
    NOURISH("Nourish", HabitColors.Coral.argb),
    HOME("Home", HabitColors.Lagoon.argb),
    GROWTH("Growth", HabitColors.Mint.argb)
}

/**
 * Curated starter habits with color, schedule, and reminder defaults.
 * iconKey maps to Material icons in the UI layer.
 */
data class HabitTemplate(
    val id: String,
    val title: String,
    val blurb: String,
    val category: TemplateCategory,
    val colorArgb: Int,
    val scheduleDays: Int = ScheduleDays.ALL,
    val reminderHour: Int? = null,
    val reminderMinute: Int = 0,
    val iconKey: String
)

object HabitTemplates {
    val weekdays = 0b0011111 // Mon–Fri
    val weekends = 0b1100000 // Sat–Sun

    val all: List<HabitTemplate> = listOf(
        HabitTemplate(
            id = "water",
            title = "Drink water",
            blurb = "A glass when you wake — hydrate before the day rushes in.",
            category = TemplateCategory.ENERGY,
            colorArgb = HabitColors.Sky.argb,
            reminderHour = 8,
            iconKey = "water"
        ),
        HabitTemplate(
            id = "morning_sun",
            title = "Morning sunlight",
            blurb = "Two minutes outside to set your body clock.",
            category = TemplateCategory.ENERGY,
            colorArgb = HabitColors.Sun.argb,
            reminderHour = 7,
            reminderMinute = 30,
            iconKey = "sun"
        ),
        HabitTemplate(
            id = "walk",
            title = "Daily walk",
            blurb = "Ten mindful minutes — movement without pressure.",
            category = TemplateCategory.BODY,
            colorArgb = HabitColors.Meadow.argb,
            reminderHour = 18,
            iconKey = "walk"
        ),
        HabitTemplate(
            id = "stretch",
            title = "Stretch & breathe",
            blurb = "Loosen shoulders, unclench the day.",
            category = TemplateCategory.BODY,
            colorArgb = HabitColors.Mint.argb,
            reminderHour = 21,
            iconKey = "stretch"
        ),
        HabitTemplate(
            id = "workout",
            title = "Strength session",
            blurb = "Train on a rhythm, not a mood.",
            category = TemplateCategory.BODY,
            colorArgb = HabitColors.Ember.argb,
            scheduleDays = weekdays,
            reminderHour = 17,
            iconKey = "fitness"
        ),
        HabitTemplate(
            id = "meditate",
            title = "Meditate",
            blurb = "Five quiet minutes — notice, don’t force.",
            category = TemplateCategory.MIND,
            colorArgb = HabitColors.Ocean.argb,
            reminderHour = 7,
            iconKey = "meditate"
        ),
        HabitTemplate(
            id = "journal",
            title = "Evening journal",
            blurb = "Three lines: what happened, what mattered, what’s next.",
            category = TemplateCategory.MIND,
            colorArgb = HabitColors.IndigoNight.argb,
            reminderHour = 21,
            reminderMinute = 30,
            iconKey = "journal"
        ),
        HabitTemplate(
            id = "gratitude",
            title = "Name three gratitudes",
            blurb = "Train attention toward what’s working.",
            category = TemplateCategory.MIND,
            colorArgb = HabitColors.Berry.argb,
            reminderHour = 22,
            iconKey = "favorite"
        ),
        HabitTemplate(
            id = "read",
            title = "Read 10 pages",
            blurb = "Books compound. Pages are enough.",
            category = TemplateCategory.FOCUS,
            colorArgb = HabitColors.Plum.argb,
            reminderHour = 20,
            iconKey = "book"
        ),
        HabitTemplate(
            id = "deep_work",
            title = "Deep work block",
            blurb = "One protected hour — phone away, one goal.",
            category = TemplateCategory.FOCUS,
            colorArgb = HabitColors.IndigoNight.argb,
            scheduleDays = weekdays,
            reminderHour = 10,
            iconKey = "focus"
        ),
        HabitTemplate(
            id = "learn",
            title = "Learn something new",
            blurb = "A lesson, a chapter, a skill rep.",
            category = TemplateCategory.GROWTH,
            colorArgb = HabitColors.Lagoon.argb,
            reminderHour = 19,
            iconKey = "school"
        ),
        HabitTemplate(
            id = "nofeed",
            title = "No doomscroll morning",
            blurb = "First 30 minutes offline after waking.",
            category = TemplateCategory.FOCUS,
            colorArgb = HabitColors.Coral.argb,
            reminderHour = 7,
            iconKey = "phoneless"
        ),
        HabitTemplate(
            id = "cook",
            title = "Cook a real meal",
            blurb = "One homemade plate — fuel you chose.",
            category = TemplateCategory.NOURISH,
            colorArgb = HabitColors.Ember.argb,
            reminderHour = 19,
            iconKey = "restaurant"
        ),
        HabitTemplate(
            id = "veggies",
            title = "Eat colorful plants",
            blurb = "Two servings of vegetables today.",
            category = TemplateCategory.NOURISH,
            colorArgb = HabitColors.Meadow.argb,
            iconKey = "eco"
        ),
        HabitTemplate(
            id = "sleep",
            title = "Lights out by 11",
            blurb = "Protect tomorrow’s energy tonight.",
            category = TemplateCategory.ENERGY,
            colorArgb = HabitColors.IndigoNight.argb,
            reminderHour = 22,
            reminderMinute = 30,
            iconKey = "bed"
        ),
        HabitTemplate(
            id = "tidy",
            title = "Reset one surface",
            blurb = "Clear a desk, a sink, a nightstand.",
            category = TemplateCategory.HOME,
            colorArgb = HabitColors.Lagoon.argb,
            reminderHour = 20,
            iconKey = "home"
        ),
        HabitTemplate(
            id = "connect",
            title = "Message someone you care about",
            blurb = "A short note beats a silent feed.",
            category = TemplateCategory.GROWTH,
            colorArgb = HabitColors.Berry.argb,
            scheduleDays = weekends or weekdays,
            reminderHour = 12,
            iconKey = "chat"
        ),
        HabitTemplate(
            id = "money",
            title = "Track spending",
            blurb = "Log today’s purchases in under a minute.",
            category = TemplateCategory.GROWTH,
            colorArgb = HabitColors.Citrus.argb,
            reminderHour = 21,
            iconKey = "payments"
        )
    )

    fun byCategory(): Map<TemplateCategory, List<HabitTemplate>> =
        TemplateCategory.entries.associateWith { cat ->
            all.filter { it.category == cat }
        }
}
