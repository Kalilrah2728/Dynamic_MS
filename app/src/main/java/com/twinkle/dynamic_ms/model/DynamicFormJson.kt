package com.twinkle.dynamic_ms.model

class DynamicFormJson (
    val id: Int,
    val reg_id: String,
    val tallysheet_name: String,
    val label_json: Boolean,
    val json_op: String,
    val depthArr: ArrayList<DepthArrayModel>,
    val color_code: String,
    val created_by: String,
)