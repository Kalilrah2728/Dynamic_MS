package com.twinkle.dynamic_ms.model

data class FormComponentItem(
    val access: Boolean,
    val className: String,
    val `inline`: Boolean,//Un Used
    val label: String?,
    val errorMessage: String?,
    val jsId: String?,
    val jsSubId: String?,
    val max: Long?,
    val min: Int?,
    val step: Int?,
    val multiple: Boolean?,
    val name: String?,
    val value: String?,
    val maxlength: String?,
    val rows: String?,
    val other: Boolean?,
    val placeholder: String?,
    val required: Boolean?,
    val style: String?,
    val subtype: String?,
    val toggle: Boolean?,
    val type: String?,
    val id: String?,
    val female: String?,
    val male: String?,
    val values: List<Value>?,
    val tab: String?,
    val isNew: Boolean?
)