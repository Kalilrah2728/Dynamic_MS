package com.twinkle.dynamic_ms

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.twinkle.dynamic_ms.databinding.ActivityHealthCenterRecordsBinding
import com.twinkle.dynamic_ms.databinding.ActivityMonthlySummaryBinding
import com.twinkle.dynamic_ms.model.DynamicFormJson

class HealthCenterRecords : AppCompatActivity() {

    lateinit var binding: ActivityHealthCenterRecordsBinding
    var formComponent: FormComponent? = null
    val textColor = Color.parseColor("#000000")
    val textWhite = Color.parseColor("#FFFFFFFF")
    lateinit var newCasesTvM: TextView
    lateinit var newCasesTvF: TextView
    lateinit var headingName: TextView
    lateinit var tableTitleLay: ConstraintLayout

    var countingNumM: Int = 0
    var countingNumF: Int = 0

    var tallyFormArrayList: java.util.ArrayList<DynamicFormJson>? = null
    var txtJson: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthCenterRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)    }
}