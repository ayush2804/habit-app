package com.ayushpandey.habitapp.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class HabitSwatch(
    val argb: Int,
    val name: String,
    val softArgb: Int
)

object HabitColors {
    val Lagoon = HabitSwatch(0xFF0E7C7B.toInt(), "Lagoon", 0xFFD7F3F2.toInt())
    val Coral = HabitSwatch(0xFFFF6B4A.toInt(), "Coral", 0xFFFFE4DC.toInt())
    val Sun = HabitSwatch(0xFFF4A259.toInt(), "Sun", 0xFFFFEFD9.toInt())
    val Ocean = HabitSwatch(0xFF2F80ED.toInt(), "Ocean", 0xFFD6E8FF.toInt())
    val Meadow = HabitSwatch(0xFF3D9A5F.toInt(), "Meadow", 0xFFDDF3E5.toInt())
    val Berry = HabitSwatch(0xFFE85D75.toInt(), "Berry", 0xFFFFE0E6.toInt())
    val IndigoNight = HabitSwatch(0xFF3D5A80.toInt(), "Harbor", 0xFFDCE6F2.toInt())
    val Citrus = HabitSwatch(0xFFE9C46A.toInt(), "Citrus", 0xFFFFF5D9.toInt())
    val Plum = HabitSwatch(0xFF9B5DE5.toInt(), "Orchid", 0xFFF0E4FF.toInt())
    val Mint = HabitSwatch(0xFF2EC4B6.toInt(), "Mint", 0xFFD5F7F3.toInt())
    val Ember = HabitSwatch(0xFFE76F51.toInt(), "Ember", 0xFFFFE5DD.toInt())
    val Sky = HabitSwatch(0xFF00A8E8.toInt(), "Sky", 0xFFD4F1FF.toInt())

    val options: List<Int> = listOf(
        Lagoon, Coral, Sun, Ocean, Meadow, Berry,
        IndigoNight, Citrus, Plum, Mint, Ember, Sky
    ).map { it.argb }

    val swatches: List<HabitSwatch> = listOf(
        Lagoon, Coral, Sun, Ocean, Meadow, Berry,
        IndigoNight, Citrus, Plum, Mint, Ember, Sky
    )

    fun asCompose(colorArgb: Int): Color = Color(colorArgb)

    fun softFor(colorArgb: Int): Color {
        val match = swatches.firstOrNull { it.argb == colorArgb }
        return Color(match?.softArgb ?: softBlend(colorArgb))
    }

    fun brushFor(colorArgb: Int): Brush {
        val base = asCompose(colorArgb)
        return Brush.linearGradient(
            listOf(base.copy(alpha = 0.95f), base.copy(alpha = 0.55f), softFor(colorArgb))
        )
    }

    private fun softBlend(argb: Int): Int {
        val c = Color(argb)
        val mixed = Color(
            red = c.red * 0.25f + 0.75f,
            green = c.green * 0.25f + 0.75f,
            blue = c.blue * 0.25f + 0.75f,
            alpha = 1f
        )
        return android.graphics.Color.argb(
            255,
            (mixed.red * 255).toInt(),
            (mixed.green * 255).toInt(),
            (mixed.blue * 255).toInt()
        )
    }
}
