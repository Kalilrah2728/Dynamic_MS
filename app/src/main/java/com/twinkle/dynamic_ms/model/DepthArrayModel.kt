package com.twinkle.dynamic_ms.model

class DepthArrayModel (
    val title: String,
    val postition: String,
    val display: String,
    val subTitleDepth: Int,
    val subTitleContent: ArrayList<SubTitleContentModel>,
    val rowDepth: Int,
    val rowArr: ArrayList<RowArrModel>,
)