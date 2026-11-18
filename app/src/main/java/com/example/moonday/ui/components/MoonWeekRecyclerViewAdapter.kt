package com.example.moonday.ui.components

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moonday.R
import com.example.moonday.data.model.MoonDayItem
import java.text.SimpleDateFormat
import java.util.Locale

class MoonWeekRecyclerViewAdapter(
    private var days: List<MoonDayItem> = emptyList(),
    private var selectedDate: String? = null,
    private var isDarkTheme: Boolean = true,
    private val onDayClick: (MoonDayItem) -> Unit
) : RecyclerView.Adapter<MoonWeekRecyclerViewAdapter.MoonDayViewHolder>() {

    fun updateDays(newDays: List<MoonDayItem>, newSelectedDate: String? = null, newIsDarkTheme: Boolean = true) {
        val oldDate = selectedDate
        val oldTheme = isDarkTheme
        val sameDays = days.size == newDays.size && days.zip(newDays).all { (a, b) -> a.calendarDate == b.calendarDate }

        if (sameDays && oldDate != newSelectedDate && oldTheme == newIsDarkTheme) {
            val oldIdx = days.indexOfFirst { it.calendarDate == oldDate }
            val newIdx = newDays.indexOfFirst { it.calendarDate == newSelectedDate }
            selectedDate = newSelectedDate

            if (oldIdx >= 0) notifyItemChanged(oldIdx, "SELECTION")
            if (newIdx >= 0) notifyItemChanged(newIdx, "SELECTION")
            return
        }

        days = newDays
        selectedDate = newSelectedDate
        isDarkTheme = newIsDarkTheme
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoonDayViewHolder {
        val context = parent.context
        val fredokaTypeface = ResourcesCompat.getFont(context, R.font.fredoka)

        val cardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 24, 28, 24)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 8, 12, 8)
            }
        }

        val dateTextView = TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            typeface = fredokaTypeface
            gravity = Gravity.CENTER
        }
        cardLayout.addView(dateTextView)

        val svgComposeView = ComposeView(context).apply {
            layoutParams = LinearLayout.LayoutParams(130, 130).apply {
                topMargin = 10
                bottomMargin = 10
            }
        }
        cardLayout.addView(svgComposeView)

        val phaseTextView = TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = fredokaTypeface
            gravity = Gravity.CENTER
        }
        cardLayout.addView(phaseTextView)

        return MoonDayViewHolder(cardLayout, dateTextView, svgComposeView, phaseTextView)
    }

    override fun onBindViewHolder(holder: MoonDayViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("SELECTION")) {
            val item = days[position]
            val isSelected = item.calendarDate != null && item.calendarDate == selectedDate
            holder.updateSelectionOnly(isSelected, isDarkTheme)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: MoonDayViewHolder, position: Int) {
        val item = days[position]
        val isSelected = item.calendarDate != null && item.calendarDate == selectedDate
        holder.bind(item, isSelected, isDarkTheme, onDayClick)
    }

    override fun getItemCount(): Int = days.size

    class MoonDayViewHolder(
        private val cardLayout: LinearLayout,
        private val dateTextView: TextView,
        private val svgComposeView: ComposeView,
        private val phaseTextView: TextView
    ) : RecyclerView.ViewHolder(cardLayout) {

        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.US)
        private val monthDayFormat = SimpleDateFormat("MMM d", Locale.US)
        private var boundSvgData: String? = null
        private var boundIsDark: Boolean? = null

        fun updateSelectionOnly(isSelected: Boolean, isDarkTheme: Boolean) {
            val context = cardLayout.context

            val cardBg = GradientDrawable().apply {
                cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics
                )
                if (isDarkTheme) {
                    if (isSelected) {
                        setColor(Color.parseColor("#252638"))
                        setStroke(
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, context.resources.displayMetrics).toInt(),
                            Color.parseColor("#6C5CE7")
                        )
                    } else {
                        setColor(Color.parseColor("#171822"))
                        setStroke(
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt(),
                            Color.parseColor("#2A2B3D")
                        )
                    }
                } else {
                    if (isSelected) {
                        setColor(Color.parseColor("#EDE7F6"))
                        setStroke(
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, context.resources.displayMetrics).toInt(),
                            Color.parseColor("#6C5CE7")
                        )
                    } else {
                        setColor(Color.parseColor("#FFFFFF"))
                        setStroke(
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt(),
                            Color.parseColor("#E2E8F0")
                        )
                    }
                }
            }
            cardLayout.background = cardBg

            if (isDarkTheme) {
                dateTextView.setTextColor(if (isSelected) Color.parseColor("#74B9FF") else Color.WHITE)
                phaseTextView.setTextColor(if (isSelected) Color.WHITE else Color.parseColor("#A0A5C0"))
            } else {
                dateTextView.setTextColor(if (isSelected) Color.parseColor("#6C5CE7") else Color.parseColor("#1A1B2F"))
                phaseTextView.setTextColor(if (isSelected) Color.parseColor("#1A1B2F") else Color.parseColor("#5A607F"))
            }
        }

        fun bind(item: MoonDayItem, isSelected: Boolean, isDarkTheme: Boolean, onClick: (MoonDayItem) -> Unit) {
            updateSelectionOnly(isSelected, isDarkTheme)

            // Piece 1: Formatted Date (Day of Week on line 1, Month & Day on line 2)
            val rawDate = item.calendarDate ?: "Date"
            val formattedDate = try {
                val parsed = inputDateFormat.parse(rawDate)
                if (parsed != null) {
                    val dow = dayOfWeekFormat.format(parsed)
                    val md = monthDayFormat.format(parsed)
                    "$dow\n$md"
                } else {
                    rawDate
                }
            } catch (_: Exception) {
                rawDate
            }
            dateTextView.text = formattedDate

            // Piece 2: Moon SVG image - setContent ONLY if svgData or isDarkTheme changed!
            val svgData = item.moonVisual?.svg
            if (boundSvgData != svgData || boundIsDark != isDarkTheme) {
                boundSvgData = svgData
                boundIsDark = isDarkTheme
                svgComposeView.setContent {
                    MoonSvgView(svgContent = svgData, height = 65.dp)
                }
            }

            // Piece 3: Phase Name & Illumination %
            val phaseName = item.phase?.name ?: "Phase"
            val illumPercent = item.phase?.illumination?.let { "${(it * 100).toInt()}%" } ?: ""
            phaseTextView.text = "$phaseName\n$illumPercent"

            cardLayout.setOnClickListener { onClick(item) }
        }
    }
}
