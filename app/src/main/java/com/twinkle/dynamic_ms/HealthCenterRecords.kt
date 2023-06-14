package com.twinkle.dynamic_ms

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.twinkle.dynamic_ms.Utils.Companion.setMerginToviews
import com.twinkle.dynamic_ms.databinding.ActivityHealthCenterRecordsBinding
import com.twinkle.dynamic_ms.model.DynamicFormJson
import com.twinkle.dynamic_ms.model.FormComponentItem
import java.io.*

class HealthCenterRecords : AppCompatActivity() {

    lateinit var binding: ActivityHealthCenterRecordsBinding
    var formComponent: FormComponent? = null
    val textColor = Color.parseColor("#000000")
    val textWhite = Color.parseColor("#FFFFFFFF")
    lateinit var newCasesTvM: TextView
    lateinit var newCasesTvF: TextView
    lateinit var headingName: TextView
    lateinit var tableTitleLay: ConstraintLayout

    var checkBoxV = false
    var editTextAreaV = false
    var editableTextV = false
    var radioBtnV = false


    var countingNumM: Int = 0
    var countingNumF: Int = 0

    var tallyFormArrayList: java.util.ArrayList<DynamicFormJson>? = null
    var txtJson: String = ""
    var tallyFormJsonString: String = ""

    var monthNames =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var months = arrayOfNulls<TextView>(12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthCenterRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("tabValue")

        val `is` = resources.openRawResource(R.raw.bhuvi_sample)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        tallyFormJsonString = writer.toString()


        val gson = Gson()
        tallyFormArrayList = gson.fromJson<java.util.ArrayList<DynamicFormJson>>(
            tallyFormJsonString,
            object : TypeToken<List<DynamicFormJson?>?>() {}.type
        )

        tallyFormArrayList?.iterator()?.forEach {
            if ( it.id == 1){
                binding.titleTextview.text = it.mainTitle
                val text1 = it.depthArr

                val horizontalScrollView = HorizontalScrollView(this)
                val horizontalParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                horizontalScrollView.layoutParams = horizontalParams


                val parntVContainer = LinearLayout(this)
                parntVContainer.orientation = LinearLayout.VERTICAL
                val parntVlayParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                parntVlayParams.setMargins(0, 0, 0, 0)
                parntVContainer.setBackgroundResource(R.drawable.black_box)
                parntVContainer.layoutParams = parntVlayParams

                text1.iterator().forEach {
                    val txt1 = it.subTitleContent
                    val rowArry = it.rowArr

                    var numSTC = 0
                    var laySTC = 0
                    var numRA = 0
                    var layRA = 0

                    val label = TextView(this)
                    label.setTextColor(Color.BLACK)
                    label.setTypeface(null, Typeface.BOLD)
                    label.textSize = 20f
                    setMerginToviews(
                        label,
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    )
                    it.title.let { labelText ->

                        label.text = createStringForViewLabel(false, labelText+"")
                        binding.miniAppFormContainer.addView(label)

                    }


                    if (txt1 != null){
                        var loopCountNum = 0
                        txt1.iterator().forEach {
                            //Parent layout
                            val numberViewContainer = LinearLayout(this)
                            numberViewContainer.orientation = LinearLayout.HORIZONTAL
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.setMargins(0, 0, 0, 0)
                            numberViewContainer.setBackgroundResource(R.drawable.black_box)
                            numberViewContainer.layoutParams = layoutParams

                            val gson1 = Gson()
                            val arrayData2 = gson1.toJson(it.subTitleArr)
                            populateMalForm(arrayData2, numberViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0)
                            numSTC++
                            laySTC++
                            loopCountNum++


                            //numberViewContainer.gravity = Gravity.CENTER
                            //binding.miniAppFormContainer.addView(numberViewContainer)
                            parntVContainer.addView(numberViewContainer)

                        }
                    }

                    rowArry.iterator().forEach {
                        //Parent layout
                        val numVContainer = LinearLayout(this)
                        numVContainer.orientation = LinearLayout.HORIZONTAL
                        val layPrams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layPrams.setMargins(0, 0, 0, 0)
                        numVContainer.setBackgroundResource(R.drawable.black_box)
                        numVContainer.layoutParams = layPrams

                        val dataColRowsArry = it.dataColRows.toString()

                        val gson3 = Gson()
                        val arrayData3 = gson3.toJson(it.dataColRows)
                        populateMalForm(arrayData3, numVContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size)
                        numRA++
                        layRA++

                        //======================================================================================
                        //numVContainer.gravity = Gravity.CENTER
                        //binding.miniAppFormContainer.addView(numVContainer)
                        parntVContainer.addView(numVContainer)

                    }


                }

                horizontalScrollView.addView(parntVContainer)
                binding.miniAppFormContainer.addView(horizontalScrollView)
            }
        }

        json?.let {
            populateFormenu(it)
        }
    }

    private fun populateFormenu(it: String) {
        formComponent = Gson().fromJson(it, FormComponent::class.java)
        binding.miniAppFormContainer.visibility = View.VISIBLE

        //TODO:- GENERATE FORM LAYOUT
        formComponent?.let {
            it.forEach { component ->
                when (component.type) {
                    WidgetItems.TAB.label -> binding.horizontalContainer.addView(createTab(component))
                }
            }
        }
    }

    private fun createTab(component: FormComponentItem): TextView {

        val txt = TextView(this)
        when (component.subtype) {
            "h1" -> txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            "h2" -> txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            "h3" -> txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }

        component.label?.let {
            txt.text = Utils.fromHtml(it)
        }

        component.id?.let {
            txt.setOnClickListener {
                val gson = Gson()
                tallyFormArrayList = gson.fromJson<java.util.ArrayList<DynamicFormJson>>(
                    tallyFormJsonString,
                    object : TypeToken<List<DynamicFormJson?>?>() {}.type
                )

                tallyFormArrayList?.iterator()?.forEach {
                    if (it.id == component.id.toInt()) {
                        binding.miniAppFormContainer.removeAllViews()
                        binding.titleTextview.text = it.mainTitle


                        val horizontalScrollView = HorizontalScrollView(this)
                            val horizontalParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            horizontalScrollView.layoutParams = horizontalParams


                        val parntVContainer = LinearLayout(this)
                        parntVContainer.orientation = LinearLayout.VERTICAL
                        val parntVlayParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        parntVlayParams.setMargins(0, 0, 0, 0)
                        parntVContainer.setBackgroundResource(R.drawable.black_box)
                        parntVContainer.layoutParams = parntVlayParams

                        val text1 = it.depthArr
                        text1.iterator().forEach {
                            val txt1 = it.subTitleContent
                            val rowArry = it.rowArr

                            var numSTC = 0
                            var laySTC = 0
                            var numRA = 0
                            var layRA = 0

                            val label = TextView(this)
                            label.setTextColor(Color.BLACK)
                            label.setTypeface(null, Typeface.BOLD)
                            label.textSize = 20f
                            setMerginToviews(
                                label,
                                0,
                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                            )
                            it.title.let { labelText ->

                                label.text = createStringForViewLabel(false, labelText+"")
                                binding.miniAppFormContainer.addView(label)

                            }


                            if (txt1 != null){
                                var loopCountNum = 0
                                txt1.iterator().forEach {
                                    //Parent layout
                                    val numberViewContainer = LinearLayout(this)
                                    numberViewContainer.orientation = LinearLayout.HORIZONTAL
                                    val layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    layoutParams.setMargins(0, 0, 0, 0)
                                    numberViewContainer.setBackgroundResource(R.drawable.black_box)
                                    numberViewContainer.layoutParams = layoutParams

                                    val gson1 = Gson()
                                    val arrayData2 = gson1.toJson(it.subTitleArr)
                                    populateMalForm(arrayData2, numberViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0)
                                    numSTC++
                                    laySTC++
                                    loopCountNum++

                                    //numberViewContainer.gravity = Gravity.CENTER
                                    //binding.miniAppFormContainer.addView(numberViewContainer)
                                    parntVContainer.addView(numberViewContainer)
                                }
                            }


                            rowArry.iterator().forEach {
                                //Parent layout
                                val numVContainer = LinearLayout(this)
                                numVContainer.orientation = LinearLayout.HORIZONTAL
                                val layPrams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                layPrams.setMargins(0, 0, 0, 0)
                                numVContainer.setBackgroundResource(R.drawable.black_box)
                                numVContainer.layoutParams = layPrams

                                val dataColRowsArry = it.dataColRows.toString()

                                val gson3 = Gson()
                                val arrayData3 = gson3.toJson(it.dataColRows)
                                populateMalForm(arrayData3, numVContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size)
                                numRA++
                                layRA++

                                //numVContainer.gravity = Gravity.CENTER
                                //binding.miniAppFormContainer.addView(numVContainer)
                                parntVContainer.addView(numVContainer)
                            }


                        }

                        horizontalScrollView.addView(parntVContainer)
                        binding.miniAppFormContainer.addView(horizontalScrollView)

                    }
                }

            }

        }
        txt.layoutParams = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        txt.setTextColor(textWhite)
        txt.setBackgroundColor(textColor)
        txt.setPadding(70, 15, 15, 15)
        txt.gravity = Gravity.CENTER

        return txt

    }

    private fun createStringForViewLabel(
        required: Boolean,
        label: String
    ): SpannableStringBuilder {
        val labelStr = Utils.fromHtml(label)
        return if (required) {
            labelStringForRequiredField(labelStr)
        } else {
            val username = SpannableString(labelStr)
            val description = SpannableStringBuilder()
            username.setSpan(
                RelativeSizeSpan(1.1f), 0, username.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            username.setSpan(
                ForegroundColorSpan(textColor), 0, username.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            description.append(username)
            description
        }
    }


    private fun labelStringForRequiredField(label: String): SpannableStringBuilder {
        val username = SpannableString(label)
        val description = SpannableStringBuilder()
        username.setSpan(
            RelativeSizeSpan(1.1f), 0, username.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        username.setSpan(
            ForegroundColorSpan(textColor), 0, username.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        description.append(username)
        val commentSpannable = SpannableString(" *")
        commentSpannable.setSpan(
            ForegroundColorSpan(Color.RED), 0,
            commentSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        commentSpannable.setSpan(
            RelativeSizeSpan(1.0f), 0,
            commentSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        description.append(commentSpannable)
        return description
    }


    private fun populateMalForm(json: String, numberViewContainer: LinearLayout, num: String, lay: String, loopCountNum: Int, txt1Size: Int, dataRowSize: Int) {

        checkBoxV = false
        editTextAreaV = false
        editableTextV = false

        formComponent = Gson().fromJson(json, FormComponent::class.java)
        binding.miniAppFormContainer.visibility = View.VISIBLE

        //Second - Checkbox group container
        val secondContainer = LinearLayout(this)
        secondContainer.orientation = LinearLayout.VERTICAL
        val secondLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        secondLayoutParams.weight = 4.5f
        secondContainer.setPadding(5,5,5,5)
        secondContainer.setBackgroundResource(R.drawable.black_box)
        secondContainer.layoutParams = secondLayoutParams

        //Third - Checkbox group container
        val thirdContainer = LinearLayout(this)
        thirdContainer.orientation = LinearLayout.VERTICAL
        val thirdLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        thirdLayoutParams.weight = 4.5f
        thirdContainer.setPadding(5,5,5,5)
        thirdContainer.setBackgroundResource(R.drawable.black_box)
        thirdContainer.layoutParams = thirdLayoutParams

        //Fourth - Checkbox group container
        val fourthContainer = LinearLayout(this)
        fourthContainer.orientation = LinearLayout.VERTICAL
        val fourthLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        fourthLayoutParams.weight = 4.0f
        fourthContainer.setPadding(5,5,5,5)
        //fourthContainer.setBackgroundResource(R.drawable.black_box)
        fourthContainer.layoutParams = fourthLayoutParams
        fourthContainer.gravity = Gravity.CENTER

        //TODO:- GENERATE FORM LAYOUT
        formComponent?.let {
            var luupSize = 0
            var loopCount = 0
            //val luupSize = it.size

            it.forEach { component ->

                if (txt1Size == 2 && loopCountNum == 0 ){
                    luupSize = 1
                }else{
                    luupSize = 0
                }

                when (component.type) {

                    WidgetItems.LABEL.label -> createMalLabel(component, numberViewContainer, num, lay, loopCount, luupSize) // Prints Table Title
                    /*WidgetItems.TEXT.label -> createMalText(component, numberViewContainer, num, lay, luupSize, dataRowSize) // Prints 0
                    WidgetItems.CHECKBOX.label -> createCheckBoxGroup(component, secondContainer) // Prints 0
                    WidgetItems.TEXTAREA.label -> createEditableTextArea(component, thirdContainer) // Prints 0
                    WidgetItems.EDITTEXT.label -> createEditableTextView(component, fourthContainer) // Prints 0*/

                }
                loopCount++
            }

            if (checkBoxV == true){
                numberViewContainer.addView(secondContainer)
            }
            if (editTextAreaV == true){
                numberViewContainer.addView(thirdContainer)
            }

            if (editableTextV == true){
                //Fourth - Checkbox group container
                val fContainer = LinearLayout(this)
                fContainer.orientation = LinearLayout.VERTICAL
                val fLayoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                fLayoutParams.weight = 0.2f
                fContainer.setPadding(5,5,5,5)
                //fourthContainer.setBackgroundResource(R.drawable.black_box)
                fContainer.layoutParams = fLayoutParams
                fContainer.gravity = Gravity.CENTER

                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(0, 0, 0, 0)
                textView.text = createStringForViewLabel(false, " : ")
                fContainer.addView(textView)

                numberViewContainer.addView(fContainer)
                numberViewContainer.addView(fourthContainer)

            }
        }
    }


    private fun createMalLabel(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, loopCount: Int, luupSize: Int
    ) {
        val monthStrngs = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            300,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay

        var valueDummy = ""

        if (component.title != null) {
            component.title.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let {
                    valueDummy = it.toString()
                    createStringForViewLabel(false, it+"") }
                try {
                    if (component.fontWeight.toString().toLowerCase() == "bold"){
                        textView.setTypeface(null, Typeface.BOLD);
                    }
                }catch (e: Exception){
                    println("fontWeightBold=====NULL")
                }

                num.addView(textView)
            }
        }else {
            component.fieldName.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let {
                    valueDummy = it
                    createStringForViewLabel(false, it+"") }
                num.addView(textView)
            }
        }
        numberViewContainer.addView(num)
        num.gravity = Gravity.CENTER

        if (valueDummy == ""){

            monthStrngs.iterator().forEach {

            val secondContainer = LinearLayout(this)
            secondContainer.orientation = LinearLayout.HORIZONTAL
            val secondLayoutParams = LinearLayout.LayoutParams(
                150,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            secondContainer.setPadding(5,5,5,5)
            secondContainer.setBackgroundResource(R.drawable.black_box)
            secondContainer.layoutParams = secondLayoutParams
                secondContainer.gravity = Gravity.CENTER


                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = createStringForViewLabel(false, it+"")
                secondContainer.addView(textView)
                numberViewContainer.addView(secondContainer)

            }





        }
        else{

            for (i in 1..12){
            //Second - Checkbox group container
            val secondContainer = LinearLayout(this)
            secondContainer.orientation = LinearLayout.HORIZONTAL
            val secondLayoutParams = LinearLayout.LayoutParams(
                150,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            secondContainer.setPadding(5,5,5,5)
            secondContainer.setBackgroundResource(R.drawable.black_box)
            secondContainer.layoutParams = secondLayoutParams


                //Second - container textView
                val editTextParam = LinearLayout.LayoutParams(
                    100,
                    50
                )
                editTextParam.setMargins(10, 10, 10, 10)
                val editText = TextView(this)
                editText.layoutParams = editTextParam
                editText.gravity = Gravity.CENTER
                editText.setPadding(10, 10, 10, 10)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.setBackgroundResource(R.drawable.boxcurved)
                val edtTxtNumM = 0
                editText.text = edtTxtNumM.toString()

                secondContainer.addView(editText)
                numberViewContainer.addView(secondContainer)
                secondContainer.gravity = Gravity.CENTER
            }


        }

    }
}