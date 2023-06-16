package com.twinkle.dynamic_ms.model

import android.view.View

class FormViewComponent(
    val createdView: View,
    viewComponentModel: FormComponentItem
) {
    private val viewComponentModel: FormComponentItem = viewComponentModel
    fun getViewComponentModel(): FormComponentItem {
        return viewComponentModel
    }

}

class FormViewComponentAge(
    val year: View, val month: View, val days: View,
    viewComponentModel: FormComponentItem
) {
    private val viewComponentModel: FormComponentItem = viewComponentModel
    fun getViewComponentModel(): FormComponentItem {
        return viewComponentModel
    }

}

class FormViewComponentNum(
    val txtM: View, val txtF: View,
    viewComponentModel: FormComponentItem
) {
    private val viewComponentModel: FormComponentItem = viewComponentModel
    fun getViewComponentModel(): FormComponentItem {
        return viewComponentModel
    }

}












