package com.example.util.simpletimetracker.feature_base_adapter.hint

import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType

data class HintViewData(
    val text: String
) : ViewHolderType {

    override fun getUniqueId(): Long = text.hashCode().toLong()

    override fun isValidType(other: ViewHolderType): Boolean = other is HintViewData
}