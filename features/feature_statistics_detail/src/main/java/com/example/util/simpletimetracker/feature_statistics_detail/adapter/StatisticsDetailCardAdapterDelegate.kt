package com.example.util.simpletimetracker.feature_statistics_detail.adapter

import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.core.view.ViewCompat
import com.example.util.simpletimetracker.feature_base_adapter.createRecyclerBindingAdapterDelegate
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.example.util.simpletimetracker.feature_views.extension.visible
import com.example.util.simpletimetracker.feature_statistics_detail.databinding.StatisticsDetailCardItemBinding as Binding
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailCardViewData as ViewData

fun createStatisticsDetailCardAdapterDelegate(
    titleTextSize: Int,
    subtitleTextSize: Int,
    onItemClick: () -> Unit
) = createRecyclerBindingAdapterDelegate<ViewData, Binding>(
    Binding::inflate
) { binding, item, _ ->

    with(binding) {
        item as ViewData

        tvStatisticsDetailCardValue.text = item.title
        tvStatisticsDetailCardValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())

        tvStatisticsDetailCardSecondValue.visible = item.secondTitle.isNotEmpty()
        tvStatisticsDetailCardSecondValue.text = item.secondTitle
        tvStatisticsDetailCardSecondValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, subtitleTextSize.toFloat())

        tvStatisticsDetailCardDescription.text = item.subtitle
        tvStatisticsDetailCardDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, subtitleTextSize.toFloat())

        if (item.icon != null) {
            cardStatisticsDetailCardIcon.visible = true
            ivStatisticsDetailCardIcon.setBackgroundResource(item.icon.iconDrawable)
            ViewCompat.setBackgroundTintList(
                ivStatisticsDetailCardIcon,
                ColorStateList.valueOf(item.icon.iconColor)
            )
            root.setOnClick(onItemClick)
        } else {
            cardStatisticsDetailCardIcon.visible = false
            root.isClickable = false
        }
    }
}