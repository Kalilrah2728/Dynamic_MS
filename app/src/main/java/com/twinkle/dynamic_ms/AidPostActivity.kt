package com.twinkle.dynamic_ms

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.twinkle.dynamic_ms.Utils.Companion.setMerginToviews
import com.twinkle.dynamic_ms.databinding.ActivityAidPostBinding
import com.twinkle.dynamic_ms.databinding.ActivityMonthlySummaryBinding
import com.twinkle.dynamic_ms.model.DynamicFormJson
import com.twinkle.dynamic_ms.model.FormComponentItem
import com.twinkle.dynamic_ms.model.FormViewComponentNum
import java.io.*

class AidPostActivity : AppCompatActivity() {

    var formComponent: FormComponent? = null
    lateinit var binding: ActivityAidPostBinding

    var tallyFormArrayList: java.util.ArrayList<DynamicFormJson>? = null
    val textColor = Color.parseColor("#000000")
    val textWhite = Color.parseColor("#FFFFFFFF")
    lateinit var newCasesTvM: TextView
    lateinit var newCasesTvF: TextView
    lateinit var headingName: TextView
    lateinit var tableTitleLay: ConstraintLayout
    var countingNumM: Int = 0
    var countingNumF: Int = 0
    lateinit var tallySaveBtn: Button
    var txtJson: String = ""
    var formViewCollectionNum: ArrayList<FormViewComponentNum> = arrayListOf()


    companion object {
        var submitPropertyArrayJson: JsonArray? = null
        val tallyJsonList: java.util.ArrayList<Any> = arrayListOf<Any>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAidPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("tabValue")

        newCasesTvM = findViewById(R.id.textView5)
        newCasesTvF = findViewById(R.id.textView6)
        headingName = findViewById(R.id.title_textview)
        tableTitleLay = findViewById(R.id.table_title)

        val `is` = resources.openRawResource(R.raw.tally_form)
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

        val tallyFormJsonString = writer.toString()

        val gson = Gson()
        tallyFormArrayList = gson.fromJson<java.util.ArrayList<DynamicFormJson>>(
            tallyFormJsonString,
            object : TypeToken<List<DynamicFormJson?>?>() {}.type

        )

        tallyFormArrayList?.iterator()?.forEach {
            if (it.reg_id == "1") {
                binding.titleTextview.text = it.tallysheet_name
                txtJson = it.json_op

            }
        }

        json?.let {
            populateFormenu(it)
            val text1 = txtJson
            populateForm(text1)

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

                tallyFormArrayList?.iterator()?.forEach {
                    if (it.reg_id == component.id){
                        binding.miniAppFormContainer.removeAllViews()
                        binding.titleTextview.setText(it.tallysheet_name)


                        if (it.label_json == true){
                            tableTitleLay.visibility = View.VISIBLE
                        }else{
                            tableTitleLay.visibility = View.GONE
                        }

                        val text1 = it.json_op
                        formViewCollectionNum.clear()
                        tallyJsonList.clear()
                        populateForm(text1)
                       /* val bgcolor = Color.parseColor(it.color_code)
                        binding.mainLayout.setBackgroundColor(bgcolor)
                        binding.toolbar.setBackgroundColor(bgcolor)*/

                        binding.constraintLayout2.visibility = View.VISIBLE

                        countingNumM = 0
                        countingNumF = 0
                        newCasesTvM.setText(countingNumM.toString())
                        newCasesTvF.setText(countingNumF.toString())
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


    private fun populateForm(json: String) {
        formComponent = Gson().fromJson(json, FormComponent::class.java)
        binding.miniAppFormContainer.visibility = View.VISIBLE

        //TODO:- GENERATE FORM LAYOUT
        formComponent?.let {
            it.forEach { component ->
                when (component.type) {
                    WidgetItems.HEADER.label -> binding.miniAppFormContainer.addView(
                        createHeaderView(component)
                    )
                    WidgetItems.NUMBER.label -> createNumberEditText(component)
                    WidgetItems.FEMALE.label -> createFemale(component)
                    WidgetItems.MALE.label -> createMale(component)
                    WidgetItems.NUMBERONE.label -> createNumberOne(component)
                    WidgetItems.TABLETITLE.label -> createTableTitle(component)
                    WidgetItems.NUMBERHEALTH.label -> createNumberHealth(component)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun createNumberEditText(component: FormComponentItem) {
        newCasesTvF.visibility = View.VISIBLE
        newCasesTvM.visibility = View.VISIBLE


        var minValue = 0
        var maxValue = 0L
        var step = 1
        component.min?.let {
            minValue = it
        }
        component.max?.let {
            maxValue = it
        }
        component.step?.let {
            step = it
        }
        val finalStep = step
        val finalMinValue = minValue
        val finalMaxValue = maxValue

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.box_green)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1f


        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.box_green)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f

        //Second - container (first layout)
        val numberViewContainer11 = LinearLayout(this)
        numberViewContainer11.orientation = LinearLayout.HORIZONTAL
        val layoutParams11 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer11.setPadding(0, 0, 10, 0)
        numberViewContainer11.gravity = Gravity.END
        numberViewContainer11.layoutParams = layoutParams11

        //Second - container (Second layout)
        val numberViewContainer12 = LinearLayout(this)
        numberViewContainer12.orientation = LinearLayout.HORIZONTAL
        val layoutParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer12.gravity = Gravity.CENTER
        numberViewContainer12.layoutParams = layoutParams12

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1f

        //Second - container (first layout)
        val numberViewContainer13 = LinearLayout(this)
        numberViewContainer13.orientation = LinearLayout.HORIZONTAL
        val layoutParams13 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer13.setPadding(0, 0, 10, 0)
        numberViewContainer13.gravity = Gravity.END
        numberViewContainer13.layoutParams = layoutParams13

        //Second - container (Second layout)
        val numberViewContainer14 = LinearLayout(this)
        numberViewContainer14.orientation = LinearLayout.HORIZONTAL
        val layoutParams14 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer14.gravity = Gravity.CENTER
        numberViewContainer14.layoutParams = layoutParams14

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
        component.placeholder?.let {
            editText.hint = it
        }


        //Thrid - Container TextView
        editTextParam.setMargins(10, 0, 5, 0)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }


        val textParams = LinearLayout.LayoutParams(
            50, 50
        )
        textParams.setMargins(8, 0, 8, 0)
        val negativeButton = TextView(this)
        negativeButton.isAllCaps = false
        negativeButton.text = ""
        negativeButton.setTypeface(null, Typeface.BOLD)
        negativeButton.textSize = 25f
        negativeButton.gravity = Gravity.CENTER
        negativeButton.setTextColor(textColor)
        negativeButton.layoutParams = textParams
        negativeButton.setBackgroundResource(R.drawable.minus_green)


        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        //ClickListener for negative button
        negativeButton.setOnClickListener { v: View? ->

            if (edtTxtNumM > 0){
                edtTxtNumM--
                editText.setText(edtTxtNumM.toString())
                countingNumM--
                newCasesTvM.setText(countingNumM.toString())
            }

        }


        textParams.setMargins(8, 0, 8, 0)
        val negativeButton1 = TextView(this)
        negativeButton1.isAllCaps = false
        negativeButton1.text = ""
        negativeButton1.setTypeface(null, Typeface.BOLD)
        negativeButton1.textSize = 25f
        negativeButton1.gravity = Gravity.CENTER
        negativeButton1.setTextColor(textColor)
        negativeButton1.layoutParams = textParams
        negativeButton1.setBackgroundResource(R.drawable.minus_green)

        //ClickListener for negative button
        negativeButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF > 0){
                edtTxtNumF--
                editText1.setText(edtTxtNumF.toString())
                countingNumF--
                newCasesTvF.setText(countingNumF.toString())
            }
        }

        isValueNull(component, editText)
        editText.setText(minValue.toString())
        isSubTypeNull(component, editText)
        isPlaceHolderNull(component, editText)

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }


        isValueNull(component, editText1)
        editText1.setText(minValue.toString())
        isSubTypeNull(component, editText1)
        isPlaceHolderNull(component, editText1)

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }


        val positiveButton = TextView(this)
        positiveButton.isAllCaps = false
        positiveButton.text = ""
        positiveButton.textSize = 25f
        positiveButton.gravity = Gravity.CENTER
        positiveButton.setTextColor(textColor)
        positiveButton.layoutParams = textParams
        positiveButton.setBackgroundResource(R.drawable.plus_green)

        //ClickListener for positive button
        positiveButton.setOnClickListener { v: View? ->

            if (edtTxtNumM == 0 || edtTxtNumM > 0){
                edtTxtNumM++
                editText.setText(edtTxtNumM.toString())
                countingNumM++
                newCasesTvM.setText(countingNumM.toString())
            }

        }

        val positiveButton1 = TextView(this)
        positiveButton1.isAllCaps = false
        positiveButton1.text = ""
        positiveButton1.textSize = 25f
        positiveButton1.gravity = Gravity.CENTER
        positiveButton1.setTextColor(textColor)
        positiveButton1.layoutParams = textParams
        positiveButton1.setBackgroundResource(R.drawable.plus_green)

        //ClickListener for positive button
        positiveButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF == 0 || edtTxtNumF > 0){
                edtTxtNumF++
                editText1.setText(edtTxtNumF.toString())
                countingNumF++
                newCasesTvF.setText(countingNumF.toString())
            }

        }

        val textParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 80
        )
        textParams.setMargins(10, 5, 10, 5)
        val calculatorButton = TextView(this)
        calculatorButton.text = ""
        calculatorButton.gravity = Gravity.END
        calculatorButton.layoutParams = textParams12
        calculatorButton.setBackgroundResource(R.drawable.calc_green)
        calculatorButton.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        textParams.setMargins(10, 10, 10, 10)
        val calculatorButton1 = TextView(this)
        calculatorButton1.text = ""
        calculatorButton1.gravity = Gravity.END
        calculatorButton1.layoutParams = textParams12
        calculatorButton1.setBackgroundResource(R.drawable.calc_green)
        calculatorButton1.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }


        numberViewContainer11.addView(calculatorButton)
        numberViewContainer12.addView(negativeButton)
        numberViewContainer12.addView(editText)
        numberViewContainer12.addView(positiveButton)

        numberViewContainer1.addView(numberViewContainer11)
        numberViewContainer1.addView(numberViewContainer12)

        numberViewContainer13.addView(calculatorButton1)
        numberViewContainer14.addView(negativeButton1)
        numberViewContainer14.addView(editText1)
        numberViewContainer14.addView(positiveButton1)

        numberViewContainer2.addView(numberViewContainer13)
        numberViewContainer2.addView(numberViewContainer14)


        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
        formViewCollectionNum.add(FormViewComponentNum(editText, editText1, component))
    }

    private fun createMale(component: FormComponentItem) {
        newCasesTvF.visibility = View.GONE
        newCasesTvM.visibility = View.VISIBLE
        /*countingNumM = 0
        newCasesTvM.setText(countingNumM.toString())*/

        var minValue = 0
        var maxValue = 0L
        var step = 1
        component.min?.let {
            minValue = it
        }
        component.max?.let {
            maxValue = it
        }
        component.step?.let {
            step = it
        }
        val finalStep = step
        val finalMinValue = minValue
        val finalMaxValue = maxValue

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.box_green)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.box_green)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f

        //Second - container (first layout)
        val numberViewContainer11 = LinearLayout(this)
        numberViewContainer11.orientation = LinearLayout.HORIZONTAL
        numberViewContainer11.setPadding(0, 0, 10, 0)
        val layoutParams11 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer11.gravity = Gravity.END
        numberViewContainer11.layoutParams = layoutParams11

        //Second - container (Second layout)
        val numberViewContainer12 = LinearLayout(this)
        numberViewContainer12.orientation = LinearLayout.HORIZONTAL
        val layoutParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer12.gravity = Gravity.CENTER
        numberViewContainer12.layoutParams = layoutParams12

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams2.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(10, 10, 10, 10)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1f


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
        component.placeholder?.let {
            editText.hint = it
        }

        val textParams = LinearLayout.LayoutParams(
            50, 50
        )
        textParams.setMargins(8, 0, 8, 0)
        val negativeButton = TextView(this)
        negativeButton.isAllCaps = false
        negativeButton.text = ""
        negativeButton.setTypeface(null, Typeface.BOLD)
        negativeButton.textSize = 25f
        negativeButton.gravity = Gravity.CENTER
        negativeButton.setTextColor(textColor)
        negativeButton.layoutParams = textParams
        negativeButton.setBackgroundResource(R.drawable.minus_green)


        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        //ClickListener for negative button
        negativeButton.setOnClickListener { v: View? ->

            if (edtTxtNumM > 0){
                edtTxtNumM--
                editText.setText(edtTxtNumM.toString())
                countingNumM--
                newCasesTvM.setText(countingNumM.toString())
            }

        }

        textParams.setMargins(8, 0, 8, 0)
        isValueNull(component, editText)
        editText.setText(minValue.toString())
        isSubTypeNull(component, editText)
        isPlaceHolderNull(component, editText)

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        val positiveButton = TextView(this)
        positiveButton.isAllCaps = false
        positiveButton.text = ""
        positiveButton.textSize = 25f
        positiveButton.gravity = Gravity.CENTER
        positiveButton.setTextColor(textColor)
        positiveButton.layoutParams = textParams
        positiveButton.setBackgroundResource(R.drawable.plus_green)

        //ClickListener for positive button
        positiveButton.setOnClickListener { v: View? ->

            if (edtTxtNumM == 0 || edtTxtNumM > 0){
                edtTxtNumM++
                editText.setText(edtTxtNumM.toString())
                countingNumM++
                newCasesTvM.setText(countingNumM.toString())
            }

        }

        val textParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 80
        )
        textParams.setMargins(10, 10, 10, 10)
        val calculatorButton = TextView(this)
        calculatorButton.text = ""
        calculatorButton.gravity = Gravity.END
        calculatorButton.layoutParams = textParams12
        calculatorButton.setBackgroundResource(R.drawable.calc_green)
        calculatorButton.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        numberViewContainer11.addView(calculatorButton)
        numberViewContainer12.addView(negativeButton)
        numberViewContainer12.addView(editText)
        numberViewContainer12.addView(positiveButton)

        numberViewContainer1.addView(numberViewContainer11)
        numberViewContainer1.addView(numberViewContainer12)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
        formViewCollectionNum.add(FormViewComponentNum(editText,editText, component))

    }

    private fun createFemale(component: FormComponentItem) {
        newCasesTvF.visibility = View.VISIBLE
        newCasesTvM.visibility = View.GONE
        /*countingNumF = 0
        newCasesTvF.setText(countingNumF.toString())*/

        var minValue = 0
        var maxValue = 0L
        var step = 1
        component.min?.let {
            minValue = it
        }
        component.max?.let {
            maxValue = it
        }
        component.step?.let {
            step = it
        }
        val finalStep = step
        val finalMinValue = minValue
        val finalMaxValue = maxValue

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.box_green)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.box_green)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1f

        //Third - container (first layout)
        val numberViewContainer13 = LinearLayout(this)
        numberViewContainer13.orientation = LinearLayout.HORIZONTAL
        val layoutParams13 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer13.setPadding(0, 0, 10, 0)
        numberViewContainer13.gravity = Gravity.END
        numberViewContainer13.layoutParams = layoutParams13

        //Third - container (Second layout)
        val numberViewContainer14 = LinearLayout(this)
        numberViewContainer14.orientation = LinearLayout.HORIZONTAL
        val layoutParams14 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer14.gravity = Gravity.CENTER
        numberViewContainer14.layoutParams = layoutParams14

        //Thrid - Container TextView
        val editTextParam1 = LinearLayout.LayoutParams(
            100,
            50
        )
        editTextParam1.setMargins(10, 0, 5, 0)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam1
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText1.hint = it
        }


        val textParams = LinearLayout.LayoutParams(
            50, 50
        )
        textParams.setMargins(8, 0, 8, 0)
        val negativeButton1 = TextView(this)
        negativeButton1.isAllCaps = false
        negativeButton1.text = ""
        negativeButton1.setTypeface(null, Typeface.BOLD)
        negativeButton1.textSize = 25f
        negativeButton1.gravity = Gravity.CENTER
        negativeButton1.setTextColor(textColor)
        negativeButton1.layoutParams = textParams
        negativeButton1.setBackgroundResource(R.drawable.minus_green)

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        //ClickListener for negative button
        negativeButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF > 0){
                edtTxtNumF--
                editText1.setText(edtTxtNumF.toString())
                countingNumF--
                newCasesTvF.setText(countingNumF.toString())
            }

        }

        isValueNull(component, editText1)
        editText1.setText(minValue.toString())
        isSubTypeNull(component, editText1)
        isPlaceHolderNull(component, editText1)

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }


        val positiveButton1 = TextView(this)
        positiveButton1.isAllCaps = false
        positiveButton1.text = ""
        positiveButton1.textSize = 25f
        positiveButton1.gravity = Gravity.CENTER
        positiveButton1.setTextColor(textColor)
        positiveButton1.layoutParams = textParams
        positiveButton1.setBackgroundResource(R.drawable.plus_green)

        //ClickListener for positive button
        positiveButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF == 0 || edtTxtNumF > 0){
                edtTxtNumF++
                editText1.setText(edtTxtNumF.toString())
                countingNumF++
                newCasesTvF.setText(countingNumF.toString())
            }

        }

        val textParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 80
        )
        textParams.setMargins(10, 5, 10, 5)
        val calculatorButton = TextView(this)
        calculatorButton.text = ""
        calculatorButton.gravity = Gravity.END
        calculatorButton.layoutParams = textParams12
        calculatorButton.setBackgroundResource(R.drawable.calc_green)
        calculatorButton.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        textParams.setMargins(10, 10, 10, 10)
        val calculatorButton1 = TextView(this)
        calculatorButton1.text = ""
        calculatorButton1.gravity = Gravity.END
        calculatorButton1.layoutParams = textParams12
        calculatorButton1.setBackgroundResource(R.drawable.calc_green)
        calculatorButton1.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        numberViewContainer13.addView(calculatorButton1)
        numberViewContainer14.addView(negativeButton1)
        numberViewContainer14.addView(editText1)
        numberViewContainer14.addView(positiveButton1)

        numberViewContainer2.addView(numberViewContainer13)
        numberViewContainer2.addView(numberViewContainer14)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
        formViewCollectionNum.add(FormViewComponentNum(editText1, editText1, component))

    }

    private fun createNumberOne(component: FormComponentItem) {
        newCasesTvF.visibility = View.VISIBLE
        newCasesTvM.visibility = View.GONE
        /*countingNumM = 0
        newCasesTvF.setText(countingNumM.toString())*/

        var minValue = 0
        var maxValue = 0L
        var step = 1
        component.min?.let {
            minValue = it
        }
        component.max?.let {
            maxValue = it
        }
        component.step?.let {
            step = it
        }
        val finalStep = step
        val finalMinValue = minValue
        val finalMaxValue = maxValue

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.box_green)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 2f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        //layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.box_green)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f

        //Second - container (first layout)
        val numberViewContainer11 = LinearLayout(this)
        numberViewContainer11.orientation = LinearLayout.HORIZONTAL
        val layoutParams11 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer11.gravity = Gravity.END
        numberViewContainer11.layoutParams = layoutParams11

        //Second - container (Second layout)
        val numberViewContainer12 = LinearLayout(this)
        numberViewContainer12.orientation = LinearLayout.HORIZONTAL
        val layoutParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer12.gravity = Gravity.CENTER
        //numberViewContainer12.setPadding(10, 10, 10, 10)
        numberViewContainer12.layoutParams = layoutParams12

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
        component.placeholder?.let {
            editText.hint = it
        }

        val textParams = LinearLayout.LayoutParams(
            50, 50
        )
        textParams.setMargins(8, 0, 8, 0)
        val negativeButton = TextView(this)
        negativeButton.isAllCaps = false
        negativeButton.text = ""
        negativeButton.setTypeface(null, Typeface.BOLD)
        negativeButton.textSize = 25f
        negativeButton.gravity = Gravity.CENTER
        negativeButton.setTextColor(textColor)
        negativeButton.layoutParams = textParams
        negativeButton.setBackgroundResource(R.drawable.minus_green)


        var edtTxtNumN = 0
        editText.setText(edtTxtNumN.toString())

        //ClickListener for negative button
        negativeButton.setOnClickListener { v: View? ->

            if (edtTxtNumN > 0){
                edtTxtNumN--
                editText.setText(edtTxtNumN.toString())
                countingNumM--
                newCasesTvF.setText(countingNumM.toString())
            }
        }

        val textParams1 = LinearLayout.LayoutParams(
            50, 50
        )
        textParams1.setMargins(8, 0, 8, 0)

        isValueNull(component, editText)
        editText.setText(minValue.toString())
        isSubTypeNull(component, editText)
        isPlaceHolderNull(component, editText)

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        val positiveButton = TextView(this)
        positiveButton.isAllCaps = false
        positiveButton.text = ""
        positiveButton.textSize = 25f
        positiveButton.gravity = Gravity.CENTER
        positiveButton.setTextColor(textColor)
        positiveButton.layoutParams = textParams1
        positiveButton.setBackgroundResource(R.drawable.plus_green)


        //ClickListener for positive button
        positiveButton.setOnClickListener { v: View? ->

            if (edtTxtNumN == 0 || edtTxtNumN > 0){
                edtTxtNumN++
                editText.setText(edtTxtNumN.toString())
                countingNumM++
                newCasesTvF.setText(countingNumM.toString())
            }

        }

        val textParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 80
        )
        textParams1.setMargins(10, 10, 10, 10)
        val calculatorButton = TextView(this)
        calculatorButton.text = ""
        calculatorButton.gravity = Gravity.END
        calculatorButton.layoutParams = textParams12
        calculatorButton.setBackgroundResource(R.drawable.calc_green)
        calculatorButton.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        numberViewContainer11.addView(calculatorButton)
        numberViewContainer12.addView(negativeButton)
        numberViewContainer12.addView(editText)
        numberViewContainer12.addView(positiveButton)

        numberViewContainer1.addView(numberViewContainer11)
        numberViewContainer1.addView(numberViewContainer12)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
        formViewCollectionNum.add(FormViewComponentNum(editText,editText, component))

    }

    private fun createTableTitle(component: FormComponentItem) {
        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 5, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.empty_bg)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1f
        component.item_name?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.setTypeface(null, Typeface.BOLD)
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }

        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.empty_bg)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f
        component.male?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer1.addView(textView)
        }

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams2.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(10, 10, 10, 10)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1f

        component.female?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer2.addView(textView)
        }

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    @SuppressLint("ResourceAsColor")
    private fun createNumberHealth(component: FormComponentItem) {
        newCasesTvF.visibility = View.VISIBLE
        newCasesTvM.visibility = View.VISIBLE

        var minValue = 0
        var maxValue = 0L
        var step = 1
        component.min?.let {
            minValue = it
        }
        component.max?.let {
            maxValue = it
        }
        component.step?.let {
            step = it
        }
        val finalStep = step
        val finalMinValue = minValue
        val finalMaxValue = maxValue

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.box_green)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.box_green)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1f

        //Second - container (first layout)
        val numberViewContainer11 = LinearLayout(this)
        numberViewContainer11.orientation = LinearLayout.HORIZONTAL
        val layoutParams11 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer11.setPadding(0, 0, 10, 0)
        numberViewContainer11.gravity = Gravity.END
        numberViewContainer11.layoutParams = layoutParams11

        //Second - container (Second layout)
        val numberViewContainer12 = LinearLayout(this)
        numberViewContainer12.orientation = LinearLayout.HORIZONTAL
        val layoutParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer12.gravity = Gravity.CENTER
        numberViewContainer12.layoutParams = layoutParams12


        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        //layoutParams1.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1f

        //Second - container (first layout)
        val numberViewContainer13 = LinearLayout(this)
        numberViewContainer13.orientation = LinearLayout.HORIZONTAL
        val layoutParams13 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            50
        )
        numberViewContainer13.setPadding(0, 0, 10, 0)
        numberViewContainer13.gravity = Gravity.END
        numberViewContainer13.layoutParams = layoutParams13

        //Second - container (Second layout)
        val numberViewContainer14 = LinearLayout(this)
        numberViewContainer14.orientation = LinearLayout.HORIZONTAL
        val layoutParams14 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberViewContainer14.gravity = Gravity.CENTER
        numberViewContainer14.layoutParams = layoutParams14


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
        component.placeholder?.let {
            editText.hint = it
        }

        //Thrid - Container TextView
        val editTextParam1 = LinearLayout.LayoutParams(
            100,
            50
        )
        editTextParam1.setMargins(10, 0, 5, 0)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam1
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }



        val textParams = LinearLayout.LayoutParams(
            50, 50
        )
        textParams.setMargins(8, 0, 8, 0)
        val negativeButton = TextView(this)
        negativeButton.isAllCaps = false
        negativeButton.text = ""
        negativeButton.setTypeface(null, Typeface.BOLD)
        negativeButton.textSize = 25f
        negativeButton.gravity = Gravity.CENTER
        negativeButton.setTextColor(textColor)
        negativeButton.layoutParams = textParams
        negativeButton.setBackgroundResource(R.drawable.minus_blue)

        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        //ClickListener for negative button
        negativeButton.setOnClickListener { v: View? ->

            if (edtTxtNumM > 0){
                edtTxtNumM--
                editText.setText(edtTxtNumM.toString())
                countingNumM--
                newCasesTvM.setText(countingNumM.toString())
            }

        }

        val negativeButton1 = TextView(this)
        negativeButton1.isAllCaps = false
        negativeButton1.text = ""
        negativeButton1.setTypeface(null, Typeface.BOLD)
        negativeButton1.textSize = 25f
        negativeButton1.gravity = Gravity.CENTER
        negativeButton1.setTextColor(textColor)
        negativeButton1.layoutParams = textParams
        negativeButton1.setBackgroundResource(R.drawable.minus_blue)

        //ClickListener for negative button
        negativeButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF > 0){
                edtTxtNumF--
                editText1.setText(edtTxtNumF.toString())
                countingNumF--
                newCasesTvF.setText(countingNumF.toString())
            }
        }

        isValueNull(component, editText)
        editText.setText(minValue.toString())
        isSubTypeNull(component, editText)
        isPlaceHolderNull(component, editText)

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }


        isValueNull(component, editText1)
        editText1.setText(minValue.toString())
        isSubTypeNull(component, editText1)
        isPlaceHolderNull(component, editText1)

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }


        val positiveButton = TextView(this)
        positiveButton.isAllCaps = false
        positiveButton.text = ""
        positiveButton.textSize = 25f
        positiveButton.gravity = Gravity.CENTER
        positiveButton.setTextColor(textColor)
        positiveButton.layoutParams = textParams
        positiveButton.setBackgroundResource(R.drawable.plus_blue)

        //ClickListener for positive button
        positiveButton.setOnClickListener { v: View? ->

            if (edtTxtNumM == 0 || edtTxtNumM > 0){
                edtTxtNumM++
                editText.setText(edtTxtNumM.toString())
                countingNumM++
                newCasesTvM.setText(countingNumM.toString())
            }

        }

        val positiveButton1 = TextView(this)
        positiveButton1.isAllCaps = false
        positiveButton1.text = ""
        positiveButton1.textSize = 25f
        positiveButton1.gravity = Gravity.CENTER
        positiveButton1.setTextColor(textColor)
        positiveButton1.layoutParams = textParams
        positiveButton1.setBackgroundResource(R.drawable.plus_blue)

        //ClickListener for positive button
        positiveButton1.setOnClickListener { v: View? ->

            if (edtTxtNumF == 0 || edtTxtNumF > 0){
                edtTxtNumF++
                editText1.setText(edtTxtNumF.toString())
                countingNumF++
                newCasesTvF.setText(countingNumF.toString())
            }
        }

        val textParams12 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 80
        )
        textParams.setMargins(10, 5, 10, 5)
        val calculatorButton = TextView(this)
        calculatorButton.text = ""
        calculatorButton.gravity = Gravity.END
        calculatorButton.layoutParams = textParams12
        calculatorButton.setBackgroundResource(R.drawable.calc_blue)
        calculatorButton.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }

        textParams.setMargins(10, 10, 10, 10)
        val calculatorButton1 = TextView(this)
        calculatorButton1.text = ""
        calculatorButton1.gravity = Gravity.END
        calculatorButton1.layoutParams = textParams12
        calculatorButton1.setBackgroundResource(R.drawable.calc_blue)
        calculatorButton1.setOnClickListener {
            Toast.makeText(this, "Pressed Calculator", Toast.LENGTH_SHORT).show()
        }


        numberViewContainer11.addView(calculatorButton)
        numberViewContainer12.addView(negativeButton)
        numberViewContainer12.addView(editText)
        numberViewContainer12.addView(positiveButton)

        numberViewContainer1.addView(numberViewContainer11)
        numberViewContainer1.addView(numberViewContainer12)

        numberViewContainer13.addView(calculatorButton1)
        numberViewContainer14.addView(negativeButton1)
        numberViewContainer14.addView(editText1)
        numberViewContainer14.addView(positiveButton1)

        numberViewContainer2.addView(numberViewContainer13)
        numberViewContainer2.addView(numberViewContainer14)


        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
        formViewCollectionNum.add(FormViewComponentNum(editText,editText1, component))
    }

    private fun createHeaderView(componentItem: FormComponentItem): TextView {
        val txtHeader = TextView(this)
        when (componentItem.subtype) {
            "h1" -> txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            "h2" -> txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            "h3" -> txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
        componentItem.label?.let {
            txtHeader.text = Utils.fromHtml(it)
        }

        txtHeader.layoutParams = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        txtHeader.setTextColor(textColor)
        txtHeader.setTypeface(null, Typeface.BOLD)
        txtHeader.setPadding(0, 15, 0, 15)
        txtHeader.gravity = Gravity.CENTER
        return txtHeader
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


    private fun isLabelNull(viewComponentModel: FormComponentItem) {
        if (viewComponentModel.label != null) createLabelForViews(viewComponentModel)
    }

    private fun isSubTypeNull(viewComponentModel: FormComponentItem, editText: TextView) {
        if (viewComponentModel.subtype != null) {
            setInputTypeForEditText(editText, viewComponentModel)
        }
    }

    private fun isPlaceHolderNull(
        viewComponentModel: FormComponentItem,
        editText: TextView
    ) {
        if (viewComponentModel.placeholder != null) editText.hint = viewComponentModel.placeholder
    }

    private fun isValueNull(viewComponentModel: FormComponentItem, view: TextView) {
        viewComponentModel.value?.let {
            view.text = it
        }
    }


    private fun createLabelForViews(viewComponentModel: FormComponentItem) {
        val label = TextView(this)
        label.setTextColor(Color.BLACK)
        label.setTypeface(null, Typeface.BOLD)
        setMerginToviews(
            label,
            40,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        viewComponentModel.label?.let { labelText ->
            viewComponentModel.required?.let {
                if (it) {
                    label.text = createStringForViewLabel(it, labelText)
                } else {
                    label.text = createStringForViewLabel(false, labelText)
                }
            }
            binding.miniAppFormContainer.addView(label)
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

    private fun setInputTypeForEditText(
        editText: TextView,
        viewComponentModel: FormComponentItem
    ) {
        when (viewComponentModel.subtype) {
            "password" -> editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            "email" -> editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            "tel" -> editText.inputType = InputType.TYPE_CLASS_PHONE
            "dateTimeLocal" -> editText.inputType = InputType.TYPE_CLASS_DATETIME
            else -> editText.inputType = InputType.TYPE_CLASS_TEXT
        }
    }

}