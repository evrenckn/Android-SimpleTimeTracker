package com.example.util.simpletimetracker.feature_statistics.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.util.simpletimetracker.core.adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.core.adapter.hint.createHintAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.statistics.createStatisticsAdapterDelegate
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.di.BaseViewModelFactory
import com.example.util.simpletimetracker.core.dialog.ChartFilterDialogListener
import com.example.util.simpletimetracker.domain.extension.orZero
import com.example.util.simpletimetracker.feature_statistics.R
import com.example.util.simpletimetracker.feature_statistics.adapter.createStatisticsChartAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics.adapter.createStatisticsEmptyAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics.adapter.createStatisticsInfoAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics.di.StatisticsComponentProvider
import com.example.util.simpletimetracker.feature_statistics.extra.StatisticsExtra
import com.example.util.simpletimetracker.feature_statistics.viewModel.StatisticsSettingsViewModel
import com.example.util.simpletimetracker.feature_statistics.viewModel.StatisticsViewModel
import com.example.util.simpletimetracker.navigation.params.StatisticsParams
import kotlinx.android.synthetic.main.statistics_fragment.*
import javax.inject.Inject

class StatisticsFragment : BaseFragment(R.layout.statistics_fragment),
    ChartFilterDialogListener {

    @Inject
    lateinit var settingsViewModelFactory: BaseViewModelFactory<StatisticsSettingsViewModel>

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<StatisticsViewModel>

    private val settingsViewModel: StatisticsSettingsViewModel by viewModels(
        ownerProducer = { activity as AppCompatActivity },
        factoryProducer = { settingsViewModelFactory }
    )
    private val viewModel: StatisticsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val statisticsAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createStatisticsChartAdapterDelegate(viewModel::onFilterClick),
            createStatisticsInfoAdapterDelegate(),
            createHintAdapterDelegate(),
            createStatisticsAdapterDelegate(
                addTransitionNames = true,
                onItemClick = viewModel::onItemClick
            ),
            createStatisticsEmptyAdapterDelegate(viewModel::onFilterClick),
            createLoaderAdapterDelegate()
        )
    }

    override fun initDi() {
        (activity?.application as StatisticsComponentProvider)
            .statisticsComponent
            ?.inject(this)
    }

    override fun initUi() {
        parentFragment?.postponeEnterTransition()

        rvStatisticsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = statisticsAdapter

            viewTreeObserver.addOnPreDrawListener {
                parentFragment?.startPostponedEnterTransition()
                true
            }
        }
    }

    override fun initViewModel() {
        with(viewModel) {
            extra = StatisticsExtra(shift = arguments?.getInt(ARGS_POSITION).orZero())
            statistics.observe(viewLifecycleOwner, statisticsAdapter::replace)
        }
        with(settingsViewModel) {
            rangeLength.observe(viewLifecycleOwner, viewModel::onNewRange)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onVisible()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onHidden()
    }

    override fun onChartFilterDialogDismissed() {
        viewModel.onFilterApplied()
    }

    companion object {
        private const val ARGS_POSITION = "args_position"

        fun newInstance(data: Any?): StatisticsFragment = StatisticsFragment().apply {
            val bundle = Bundle()
            when (data) {
                is StatisticsParams -> {
                    bundle.putInt(ARGS_POSITION, data.shift)
                }
            }
            arguments = bundle
        }
    }
}
