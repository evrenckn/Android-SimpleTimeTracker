package com.example.util.simpletimetracker.feature_change_record.interactor

import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTagInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.domain.model.Record
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_change_record.mapper.ChangeRecordViewDataMapper
import com.example.util.simpletimetracker.feature_change_record.viewData.ChangeRecordCommentViewData
import com.example.util.simpletimetracker.feature_change_record.viewData.ChangeRecordViewData
import javax.inject.Inject

class ChangeRecordViewDataInteractor @Inject constructor(
    private val prefsInteractor: PrefsInteractor,
    private val recordTypeInteractor: RecordTypeInteractor,
    private val recordTagInteractor: RecordTagInteractor,
    private val recordInteractor: RecordInteractor,
    private val changeRecordViewDataMapper: ChangeRecordViewDataMapper,
    private val resourceRepo: ResourceRepo,
) {

    suspend fun getPreviewViewData(
        record: Record,
    ): ChangeRecordViewData {
        val type = recordTypeInteractor.get(record.typeId)
        val tags = recordTagInteractor.getAll().filter { it.id in record.tagIds }
        val isDarkTheme = prefsInteractor.getDarkMode()
        val useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat()
        val useProportionalMinutes = prefsInteractor.getUseProportionalMinutes()

        return changeRecordViewDataMapper.map(
            record = record,
            recordType = type,
            recordTags = tags,
            isDarkTheme = isDarkTheme,
            useMilitaryTime = useMilitaryTime,
            useProportionalMinutes = useProportionalMinutes
        )
    }

    suspend fun getLastCommentsViewData(
        typeId: Long,
    ): List<ViewHolderType> {
        return recordInteractor.getByTypeWithComment(listOf(typeId))
            .asSequence()
            .sortedBy { it.timeStarted }
            .map { it.comment }
            .toSet()
            .take(LAST_COMMENTS_TO_SHOW)
            .map { ChangeRecordCommentViewData(it) }
    }

    companion object {
        private const val LAST_COMMENTS_TO_SHOW = 20
    }
}