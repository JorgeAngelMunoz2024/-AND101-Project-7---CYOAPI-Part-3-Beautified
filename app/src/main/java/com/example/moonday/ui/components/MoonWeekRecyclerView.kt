package com.example.moonday.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonday.data.model.MoonDayItem
import kotlin.math.abs

@Composable
fun MoonWeekRecyclerView(
    weekDays: List<MoonDayItem>,
    onDayClick: (MoonDayItem) -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: String? = null,
    isDarkTheme: Boolean = true,
    height: Dp = 195.dp
) {
    if (weekDays.isEmpty()) return

    val adapter = remember { MoonWeekRecyclerViewAdapter(onDayClick = onDayClick) }

    AndroidView(
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                this.adapter = adapter
                isNestedScrollingEnabled = true
                clipToPadding = false

                addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    private var startX = 0f
                    private var startY = 0f

                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        when (e.action) {
                            MotionEvent.ACTION_DOWN -> {
                                startX = e.x
                                startY = e.y
                                rv.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            MotionEvent.ACTION_MOVE -> {
                                val dx = abs(e.x - startX)
                                val dy = abs(e.y - startY)
                                if (dx > dy) {
                                    rv.parent?.requestDisallowInterceptTouchEvent(true)
                                } else if (dy > dx && dy > 10) {
                                    rv.parent?.requestDisallowInterceptTouchEvent(false)
                                }
                            }
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                })
            }
        },
        update = { recyclerView ->
            val adapter = recyclerView.adapter as? MoonWeekRecyclerViewAdapter
            adapter?.updateDays(weekDays, selectedDate, isDarkTheme)

            if (selectedDate != null) {
                val targetIndex = weekDays.indexOfFirst { it.calendarDate == selectedDate }
                if (targetIndex >= 0) {
                    val lm = recyclerView.layoutManager as? LinearLayoutManager
                    if (lm != null) {
                        val firstVisible = lm.findFirstCompletelyVisibleItemPosition()
                        val lastVisible = lm.findLastCompletelyVisibleItemPosition()
                        if (targetIndex < firstVisible || targetIndex > lastVisible) {
                            recyclerView.smoothScrollToPosition(targetIndex)
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}
