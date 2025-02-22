package com.example.util.simpletimetracker.feature_statistics_detail.interactor

import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.domain.interactor.CategoryInteractor
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.domain.model.ChartFilterType
import com.example.util.simpletimetracker.feature_statistics_detail.mapper.StatisticsDetailViewDataMapper
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailPreviewCompareViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailPreviewMoreViewData
import com.example.util.simpletimetracker.navigation.params.screen.TypesFilterParams
import javax.inject.Inject

class StatisticsDetailPreviewInteractor @Inject constructor(
    private val prefsInteractor: PrefsInteractor,
    private val recordTypeInteractor: RecordTypeInteractor,
    private val categoryInteractor: CategoryInteractor,
    private val statisticsDetailViewDataMapper: StatisticsDetailViewDataMapper,
) {

    suspend fun getPreviewData(
        filterParams: TypesFilterParams,
        isForComparison: Boolean,
    ): List<ViewHolderType> {
        val selectedIds = filterParams.selectedIds
        val filter = filterParams.filterType
        val isDarkTheme = prefsInteractor.getDarkMode()

        val viewData = when (filter) {
            ChartFilterType.ACTIVITY -> {
                recordTypeInteractor.getAll()
                    .filter { it.id in selectedIds }
                    .mapIndexed { index, type ->
                        statisticsDetailViewDataMapper.mapToPreview(
                            recordType = type,
                            isDarkTheme = isDarkTheme,
                            isFirst = index == 0,
                            isForComparison = isForComparison,
                        )
                    }
            }
            ChartFilterType.CATEGORY -> {
                categoryInteractor.getAll()
                    .filter { it.id in selectedIds }
                    .map { category ->
                        statisticsDetailViewDataMapper.mapToPreview(
                            category = category,
                            isDarkTheme = isDarkTheme,
                            isForComparison = isForComparison,
                        )
                    }
            }
        }

        return if (isForComparison) {
            buildComparisonViewData(viewData)
        } else {
            buildFilterViewData(viewData, isDarkTheme)
        }
    }

    private fun buildFilterViewData(
        viewData: List<ViewHolderType>,
        isDarkTheme: Boolean,
    ): List<ViewHolderType> {
        return when {
            viewData.isEmpty() -> {
                statisticsDetailViewDataMapper
                    .mapToPreviewEmpty(isDarkTheme)
                    .let(::listOf)
            }
            viewData.size == 1 -> {
                viewData
            }
            else -> {
                viewData.first().let(::listOf) +
                    StatisticsDetailPreviewMoreViewData +
                    viewData.drop(1)
            }
        }
    }

    private fun buildComparisonViewData(
        viewData: List<ViewHolderType>,
    ): List<ViewHolderType> {
        return if (viewData.isEmpty()) {
            viewData
        } else {
            StatisticsDetailPreviewCompareViewData.let(::listOf) + viewData
        }
    }
}