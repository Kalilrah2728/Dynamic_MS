package com.twinkle.dynamic_ms

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.twinkle.dynamic_ms.Utils.Companion.getCustomColorStateList
import com.twinkle.dynamic_ms.Utils.Companion.setMerginToviews
import com.twinkle.dynamic_ms.databinding.ActivityMonthlySummaryBinding
import com.twinkle.dynamic_ms.model.DynamicFormJson
import com.twinkle.dynamic_ms.model.FormComponentItem
import java.io.*


class MonthlySummary : AppCompatActivity() {
    var formComponent: FormComponent? = null
    lateinit var binding: ActivityMonthlySummaryBinding
    val textColor = Color.parseColor("#000000")
    val textWhite = Color.parseColor("#FFFFFFFF")
    lateinit var newCasesTvM: TextView
    lateinit var newCasesTvF: TextView
    lateinit var headingName: TextView
    var checkBoxV = false
    var editTextAreaV = false
    var editableTextV = false
    var radioBtnV = false
    //lateinit var tableTitleLay: ConstraintLayout

    var countingNumM: Int = 0
    var countingNumF: Int = 0

    var tallyFormArrayList: java.util.ArrayList<DynamicFormJson>? = null
    var txtJson: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //tableTitleLay = findViewById(R.id.table_title)

        val json = intent.getStringExtra("tabValue")


        val `is` = resources.openRawResource(R.raw.monthly_sum_form)
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
            if (it.reg_id == "1"){

                txtJson = it.json_op
                /*colorJSon = Color.parseColor(it.color_code)
                colorJsonString = it.color_code*/
            }
        }

        json?.let {
            populateFormenu(it)
            val text1 = txtJson
            /*binding.mainLayout.setBackgroundColor(colorJSon)
            binding.toolbar.setBackgroundColor(colorJSon)*/
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

    private fun populateForm(json: String) {
        formComponent = Gson().fromJson(json, FormComponent::class.java)
        binding.miniAppFormContainer.visibility = View.VISIBLE

        //TODO:- GENERATE FORM LAYOUT
        formComponent?.let {
            it.forEach { component ->
                when (component.type) {
                    WidgetItems.NUMBER.label -> createNumberEditText(component)
                    WidgetItems.TEXTAREA.label -> createEditableTextWithLabel(component)
                    WidgetItems.TITLEMALEFEMALE.label -> createTableTitle(component)
                    WidgetItems.TITLETHREEBOXVALUE.label -> titleThreeBoxValue(component)
                    WidgetItems.TITLETWOBOXVALUEMF.label -> titleTwoBoxValueMF(component)
                    WidgetItems.FEMALE.label -> createFemale(component)
                    WidgetItems.MALE.label -> createMale(component)
                    WidgetItems.NUMBERONE.label -> createNumberOne(component)
                    WidgetItems.DUALMALEFEMALEZERO.label -> fourZeroViw(component)
                    WidgetItems.TRIPLE_MF_ZERO.label -> sixZeroViw(component)
                    WidgetItems.THREEVALUEZERO.label -> threeZeroViw(component)
                    //WidgetItems.MALOUTINBASE.label -> fourZeroViw(component)
                    WidgetItems.SUBTWOZERO.label -> createNumTest(component)
                    WidgetItems.SUBFOURZERO.label -> subFourZero(component)
                    WidgetItems.NUMTEST1.label -> createNumTest1(component)
                }
            }
        }

        // save button

    }

    private fun createMalText(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, luupSize: Int, dataRowSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        //lay.weight = luupSize * 0.3f
        if (luupSize == 1){
            lay.weight = 1.2f
        }else{
            lay.weight = 0.6f
        }
        /*if (luupSize == 3 || luupSize == 4){
            lay.weight = 1.0f
        }else{
            lay.weight = 0.5f
        }*/


        if (dataRowSize > 5){
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                70,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }else{
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
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }


    }

    private fun createHIVText(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, luupSize: Int, dataRowSize: Int, secoContainer: LinearLayout
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        //lay.weight = luupSize * 0.3f
        if (luupSize == 1){
            lay.weight = 1.2f
        }else{
            lay.weight = 0.6f
        }
        /*if (luupSize == 3 || luupSize == 4){
            lay.weight = 1.0f
        }else{
            lay.weight = 0.5f
        }*/


        if (dataRowSize > 5){
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                70,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }else{
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
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)
            secoContainer.addView(num)
            num.gravity = Gravity.CENTER
        }


    }

    private fun createSuvText(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, luupSize: Int, dataRowSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        //num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        lay.weight = 1.5f


        if (dataRowSize > 5){
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                200,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)

//========================================================" : "===============================
            val fContainer = LinearLayout(this)
            fContainer.orientation = LinearLayout.VERTICAL
            val fLayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            fLayoutParams.weight = 0.5f
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
            //========================================================" : "===============================


            numberViewContainer.addView(fContainer)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }else{
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                200,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)

            //========================================================" : "===============================
            val fContainer = LinearLayout(this)
            fContainer.orientation = LinearLayout.VERTICAL
            val fLayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            fLayoutParams.weight = 0.5f
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
            //========================================================" : "===============================


            numberViewContainer.addView(fContainer)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }


    }

    private fun createAidText(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, luupSize: Int, dataRowSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        lay.weight = 1.5f


        if (dataRowSize > 5){
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                200,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)
            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }else{
            //Second - container textView
            val editTextParam = LinearLayout.LayoutParams(
                200,
                50
            )
            editTextParam.setMargins(10, 10, 10, 10)
            val editText = TextView(this)
            editText.layoutParams = editTextParam
            editText.gravity = Gravity.CENTER
            editText.setPadding(10, 10, 10, 10)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setBackgroundResource(R.drawable.boxcurved)
            /*component.title?.let {
                editText.hint = it
            }*/
            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            num.addView(editText)

            numberViewContainer.addView(num)
            num.gravity = Gravity.CENTER
        }


    }

    private fun createMalLabel(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, loopCount: Int, luupSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        /*if (loopCount == 0){

            lay.weight = luupSize * 0.5f
        }else{
            lay.weight = luupSize * 0.3f
        }*/
        if (luupSize == 1){
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 1.2f
            }
        }else{
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 0.6f
            }
        }
        /*if (luupSize == 3 || luupSize == 4){
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 1.0f
            }
        }else{
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 0.5f
            }
        }*/


        if (component.title != null) {
            component.title.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
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
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
                num.addView(textView)
            }
        }

        numberViewContainer.addView(num)
        num.gravity = Gravity.CENTER

    }

    private fun createHIVLabel(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, loopCount: Int, luupSize: Int, secoContainer: LinearLayout
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay

        if (luupSize == 1){
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 1.2f
            }
        }else{
            if (loopCount == 0){
                lay.weight = 1.5f
            }else{
                lay.weight = 0.6f
            }
        }


        if (component.title != null) {
            component.title.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
                try {
                    if (component.fontWeight.toString().toLowerCase() == "bold"){
                        textView.setTypeface(null, Typeface.BOLD);
                    }
                }catch (e: Exception){
                    println("fontWeightBold=====NULL")
                }

                if (loopCount == 0){
                    num.addView(textView)
                    numberViewContainer.addView(num)
                    num.gravity = Gravity.CENTER
                }else{
                    num.addView(textView)
                    secoContainer.addView(num)
                    num.gravity = Gravity.CENTER
                }

            }
        }else {
            component.fieldName.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }

                if (loopCount == 0){
                    num.addView(textView)
                    numberViewContainer.addView(num)
                    num.gravity = Gravity.CENTER
                }else{
                    num.addView(textView)
                    secoContainer.addView(num)
                    num.gravity = Gravity.CENTER
                }
            }
        }



    }

    private fun createSuvLabel(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, loopCount: Int, luupSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        //num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        lay.weight = 1.5f



        if (component.title != null) {
            component.title.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                //textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
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
                //textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
                num.addView(textView)
            }
        }

        numberViewContainer.addView(num)
        //num.gravity = Gravity.CENTER

    }

    private fun createAidLabel(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        num: String, lay: String, loopCount: Int, luupSize: Int
    ) {

        //First - TextView container
        val num = LinearLayout(this)
        val lay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        num.setPadding(5, 5, 5, 5)
        num.setBackgroundResource(R.drawable.black_box)
        num.layoutParams = lay
        lay.weight = 1.5f



        if (component.title != null) {
            component.title.let {labelString ->
                val textView = TextView(this)
                textView.textSize = 15f
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setPadding(5, 5, 5, 5)
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
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
                textView.text = labelString?.let { createStringForViewLabel(false, it+"") }
                num.addView(textView)
            }
        }

        numberViewContainer.addView(num)
        num.gravity = Gravity.CENTER

    }

    private fun isValueNull(viewComponentModel: FormComponentItem, view: TextView) {
        viewComponentModel.value?.let {
            view.text = it
        }
    }

    private fun isSubTypeNull(viewComponentModel: FormComponentItem, editText: EditText) {
        if (viewComponentModel.subtype != null) {
            setInputTypeForEditText(editText, viewComponentModel)
        }
    }

    private fun setInputTypeForEditText(
        editText: EditText,
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

    private fun isPlaceHolderNull(
        viewComponentModel: FormComponentItem,
        editText: EditText
    ) {
        if (viewComponentModel.placeholder != null) editText.hint = viewComponentModel.placeholder
    }

    private fun createEditableTextWithLabel(component: FormComponentItem) {
        //isLabelNull(component)

        val editText = EditText(this)
        var rows = 1

        Utils.setMerginToviews(
            editText, 10,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            150
        )
        if (component.type.equals(WidgetItems.TEXTAREA.label)) editText.gravity = Gravity.NO_GRAVITY

        editText.setPadding(5, 5, 5, 5)
        editText.setBackgroundResource(R.drawable.edit_text_background)
        isValueNull(component, editText)
        isSubTypeNull(component, editText)
        isPlaceHolderNull(component, editText)
        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        component.rows?.let {
            rows = it.toInt()
            editText.isSingleLine = false
            val finalRow = rows
            editText.setOnKeyListener { v, keyCode, event ->
                (v as EditText).lineCount > finalRow
            }
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (editText.lineCount > finalRow) {
                        editText.setText(Utils.method(editText.text.toString()))
                        editText.setSelection(editText.text.toString().length)
                    }
                }
            })
        }
        //PASSWORD
        component.subtype?.let { subType ->
            if (subType == "password") {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            } else if (subType == "num"){
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
        //editText.setLines(rows)
        //numberViewContainer.addView(editText)
        binding.miniAppFormContainer.addView(editText)
        // formViewCollection.add(FormViewComponent(editText, component))
        //Log.i("EditTextInputType", editText.inputType.toString() + "")


    }

    private fun sixZeroViw(component: FormComponentItem) {

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer4.gravity = Gravity.CENTER

        val editTextParam = LinearLayout.LayoutParams(
            60,
            50
        )

        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

        //Fourth - container
        val numContrDeathMale = LinearLayout(this)
        numContrDeathMale.orientation = LinearLayout.VERTICAL
        val layParamsDeathMale = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrDeathMale.setPadding(5, 5, 5, 5)
        numContrDeathMale.setBackgroundResource(R.drawable.black_box)
        numContrDeathMale.layoutParams = layParamsDeathMale
        layParamsDeathMale.weight = 0.5f

        //Fifth - container
        val numContrDeathFemale = LinearLayout(this)
        numContrDeathFemale.orientation = LinearLayout.VERTICAL
        val layParamsDeathFemale = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrDeathFemale.setPadding(5, 5, 5, 5)
        numContrDeathFemale.setBackgroundResource(R.drawable.black_box)
        numContrDeathFemale.layoutParams = layParamsDeathFemale
        layParamsDeathFemale.weight = 0.5f

        val numContrSixth = LinearLayout(this)
        numContrSixth.orientation = LinearLayout.VERTICAL
        val layParamSixth = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrSixth.setPadding(5, 5, 5, 5)
        numContrSixth.setBackgroundResource(R.drawable.black_box)
        numContrSixth.layoutParams = layParamSixth
        layParamSixth.weight = 0.5f

        val numContrSeventh = LinearLayout(this)
        numContrSeventh.orientation = LinearLayout.VERTICAL
        val layParamSeventh = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrSeventh.setPadding(5, 5, 5, 5)
        numContrSeventh.setBackgroundResource(R.drawable.black_box)
        numContrSeventh.layoutParams = layParamSeventh
        layParamSeventh.weight = 0.5f

        //======================================================================================================================================================================
        val resp: JsonObject = JsonParser().parse(component.valueH).asJsonObject
        val inputOne = resp.get("input_present_one").asString
        val inputTwo = resp.get("input_present_two").asString
        val inputThree = resp.get("input_present_three").asString


        if (inputOne.toString().trim().toLowerCase() == "b"){
            dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)
            dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

            if (inputTwo.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "n"){
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }

        }else if (inputOne.toString().trim().toLowerCase() == "m"){
            dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)

            if (inputTwo.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================
            }else if (inputTwo.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "n"){
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }

        }else if (inputOne.toString().trim().toLowerCase() == "f"){
            dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

            if (inputTwo.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================
            }else if (inputTwo.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "n"){
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }

        }else if (inputOne.toString().trim().toLowerCase() == "n"){

            if (inputTwo.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================
            }else if (inputTwo.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }else if (inputTwo.toString().trim().toLowerCase() == "n"){
                //====================================================third==============================
                if (inputThree.toString().trim().toLowerCase() == "b"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "m"){
                    inputThreeMethodM(component, numberViewContainer, editTextParam, numContrSixth)
                }else if (inputThree.toString().trim().toLowerCase() == "f"){
                    inputThreeMethodF(component, numberViewContainer, editTextParam, numContrSeventh)
                }else if (inputThree.toString().trim().toLowerCase() == "n"){

                }
                //====================================================third==================================

            }

        }


        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer.addView(numContrDeathMale)
        numberViewContainer.addView(numContrDeathFemale)
        numberViewContainer.addView(numContrSixth)
        numberViewContainer.addView(numContrSeventh)


        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numContrDeathMale.gravity = Gravity.CENTER
        numContrDeathFemale.gravity = Gravity.CENTER
        numContrSixth.gravity = Gravity.CENTER
        numContrSeventh.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(numberViewContainer)

    }

    private fun inputThreeMethodF(component: FormComponentItem, numberViewContainer: LinearLayout, editTextParam: LinearLayout.LayoutParams, numContrSeventh: LinearLayout) {
        //Fourth - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val edtTxtSeventhFemale = TextView(this)
        edtTxtSeventhFemale.gravity = Gravity.CENTER
        edtTxtSeventhFemale.layoutParams = editTextParam
        edtTxtSeventhFemale.setPadding(10, 10, 10, 10)
        edtTxtSeventhFemale.inputType = InputType.TYPE_CLASS_NUMBER
        edtTxtSeventhFemale.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtDeathM = 0
        edtTxtSeventhFemale.setText(edtTxtDeathM.toString())

        component.maxlength?.let {
            edtTxtSeventhFemale.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numContrSeventh.addView(edtTxtSeventhFemale)
    }

    private fun inputThreeMethodM(component: FormComponentItem, numberViewContainer: LinearLayout, editTextParam: LinearLayout.LayoutParams, numContrSixth: LinearLayout) {
        //Fourth - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val edtTxtSixthMale = TextView(this)
        edtTxtSixthMale.gravity = Gravity.CENTER
        edtTxtSixthMale.layoutParams = editTextParam
        edtTxtSixthMale.setPadding(10, 10, 10, 10)
        edtTxtSixthMale.inputType = InputType.TYPE_CLASS_NUMBER
        edtTxtSixthMale.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtDeathM = 0
        edtTxtSixthMale.setText(edtTxtDeathM.toString())

        component.maxlength?.let {
            edtTxtSixthMale.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numContrSixth.addView(edtTxtSixthMale)
    }

    private fun subFourZero(component: FormComponentItem) {
        val resp: JsonArray = JsonParser().parse(component.placeholdervalue).asJsonArray

        //numtestParent
        val parentContainer = LinearLayout(this)
        parentContainer.orientation = LinearLayout.HORIZONTAL
        val parentLayParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parentLayParams.setMargins(20, 0, 20, 0)
        parentContainer.setBackgroundResource(R.drawable.black_box)
        parentContainer.layoutParams = parentLayParams


        //Parent Text container
        val parentTxtContainer = LinearLayout(this)
        val parentTxtLay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        parentTxtContainer.setPadding(5, 5, 5, 5)
        parentTxtContainer.layoutParams = parentTxtLay
        parentTxtLay.weight = 0.25f

        component.placeholder?.let { labelString ->
            val parentTxtV = TextView(this)
            parentTxtV.textSize = 15f
            parentTxtV.gravity = Gravity.CENTER
            parentTxtV.setTextColor(Color.BLACK)
            parentTxtV.setPadding(5, 5, 5, 5)
            parentTxtV.text = createStringForViewLabel(false, labelString)
            parentTxtContainer.addView(parentTxtV)
        }
        //====================================================================================================================================

        val parentValueContainer = LinearLayout(this)
        parentValueContainer.orientation = LinearLayout.VERTICAL
        val parentValueLayParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //parentValueLayParams.setMargins(20, 0, 20, 0)
        parentValueContainer.setBackgroundResource(R.drawable.black_box)
        parentValueContainer.layoutParams = parentValueLayParams
        parentValueLayParams.weight = 1.0f


        resp.iterator().forEach {

            //Parent layout
            val numberViewContainer = LinearLayout(this)
            numberViewContainer.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            //layoutParams.setMargins(20, 0, 20, 0)
            numberViewContainer.setBackgroundResource(R.drawable.black_box)
            numberViewContainer.layoutParams = layoutParams


            //First - TextView container
            val numberViewContainer4 = LinearLayout(this)
            val layoutParams4 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            //numberViewContainer4.setPadding(2, 2, 2, 2)
            numberViewContainer4.layoutParams = layoutParams4
            layoutParams4.weight = 0.95f

            val subParentTxt = TextView(this)
            subParentTxt.textSize = 15f
            subParentTxt.gravity = Gravity.CENTER
            subParentTxt.setTextColor(Color.BLACK)
            //subParentTxt.setPadding(2, 2, 2, 2)
            subParentTxt.text = createStringForViewLabel(false, it.asString)
            numberViewContainer4.addView(subParentTxt)

            numberViewContainer.addView(numberViewContainer4)
            numberViewContainer4.gravity = Gravity.CENTER

            val editTextParam = LinearLayout.LayoutParams(
                80,
                50
            )

            //Second - container
            val numberViewContainer1 = LinearLayout(this)
            numberViewContainer1.orientation = LinearLayout.VERTICAL
            val layoutParams1 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer1.setPadding(5, 5, 5, 5)
            numberViewContainer1.setBackgroundResource(R.drawable.black_box)
            numberViewContainer1.layoutParams = layoutParams1
            layoutParams1.weight = 0.6f

            //Third - container
            val numberViewContainer2 = LinearLayout(this)
            numberViewContainer2.orientation = LinearLayout.VERTICAL
            val layoutParams2 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer2.setPadding(5, 5, 5, 5)
            numberViewContainer2.setBackgroundResource(R.drawable.black_box)
            numberViewContainer2.layoutParams = layoutParams2
            layoutParams2.weight = 0.6f

            //Fourth - container
            val numContrDeathMale = LinearLayout(this)
            numContrDeathMale.orientation = LinearLayout.VERTICAL
            val layParamsDeathMale = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numContrDeathMale.setPadding(5, 5, 5, 5)
            numContrDeathMale.setBackgroundResource(R.drawable.black_box)
            numContrDeathMale.layoutParams = layParamsDeathMale
            layParamsDeathMale.weight = 0.6f

            //Fifth - container
            val numContrDeathFemale = LinearLayout(this)
            numContrDeathFemale.orientation = LinearLayout.VERTICAL
            val layParamsDeathFemale = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numContrDeathFemale.setPadding(5, 5, 5, 5)
            numContrDeathFemale.setBackgroundResource(R.drawable.black_box)
            numContrDeathFemale.layoutParams = layParamsDeathFemale
            layParamsDeathFemale.weight = 0.6f

            //======================================================================================================================================================================
            val resp: JsonObject = JsonParser().parse(component.valueH).asJsonObject
            val dischargeValue = resp.get("discharge").asString
            val deathValue = resp.get("deaths").asString


            if (dischargeValue.toString().trim().toLowerCase() == "b"){
                dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)
                dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

                if (deathValue.toString().trim().toLowerCase() == "b"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                }else if (deathValue.toString().trim().toLowerCase() == "m"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

                }else if (deathValue.toString().trim().toLowerCase() == "f"){
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

                }else if (deathValue.toString().trim().toLowerCase() == "n"){

                }

            }else if (dischargeValue.toString().trim().toLowerCase() == "m"){
                dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)

                if (deathValue.toString().trim().toLowerCase() == "b"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                }else if (deathValue.toString().trim().toLowerCase() == "m"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

                }else if (deathValue.toString().trim().toLowerCase() == "f"){
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

                }else if (deathValue.toString().trim().toLowerCase() == "n"){

                }

            }else if (dischargeValue.toString().trim().toLowerCase() == "f"){
                dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

                if (deathValue.toString().trim().toLowerCase() == "b"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                }else if (deathValue.toString().trim().toLowerCase() == "m"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

                }else if (deathValue.toString().trim().toLowerCase() == "f"){
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

                }else if (deathValue.toString().trim().toLowerCase() == "n"){

                }

            }else if (dischargeValue.toString().trim().toLowerCase() == "n"){

                if (deathValue.toString().trim().toLowerCase() == "b"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
                }else if (deathValue.toString().trim().toLowerCase() == "m"){
                    deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

                }else if (deathValue.toString().trim().toLowerCase() == "f"){
                    deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

                }else if (deathValue.toString().trim().toLowerCase() == "n"){

                }

            }


            numberViewContainer.addView(numberViewContainer1)
            numberViewContainer.addView(numberViewContainer2)
            numberViewContainer.addView(numContrDeathMale)
            numberViewContainer.addView(numContrDeathFemale)


            numberViewContainer1.gravity = Gravity.CENTER
            numberViewContainer2.gravity = Gravity.CENTER
            numContrDeathMale.gravity = Gravity.CENTER
            numContrDeathFemale.gravity = Gravity.CENTER


            parentValueContainer.addView(numberViewContainer)
            numberViewContainer.gravity = Gravity.CENTER

        }

        // new parent  view add
        parentContainer.addView(parentTxtContainer)
        parentContainer.addView(parentValueContainer)

        parentTxtContainer.gravity = Gravity.CENTER
        parentValueContainer.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(parentContainer)
    }

    private fun createNumTest1(component: FormComponentItem) {

        val resp: JsonArray = JsonParser().parse(component.placeholdervalue).asJsonArray

        //numtestParent
        val parentContainer = LinearLayout(this)
        parentContainer.orientation = LinearLayout.HORIZONTAL
        val parentLayParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parentLayParams.setMargins(20, 0, 20, 0)
        parentContainer.setBackgroundResource(R.drawable.black_box)
        parentContainer.layoutParams = parentLayParams


        //Parent Text container
        val parentTxtContainer = LinearLayout(this)
        val parentTxtLay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        parentTxtContainer.setPadding(5, 5, 5, 5)
        parentTxtContainer.layoutParams = parentTxtLay
        parentTxtLay.weight = 0.5f

        component.placeholder?.let { labelString ->
            val parentTxtV = TextView(this)
            parentTxtV.textSize = 15f
            parentTxtV.gravity = Gravity.CENTER
            parentTxtV.setTextColor(Color.BLACK)
            parentTxtV.setPadding(5, 5, 5, 5)
            parentTxtV.text = createStringForViewLabel(false, labelString)
            parentTxtContainer.addView(parentTxtV)
        }
        //====================================================================================================================================

        val parentValueContainer = LinearLayout(this)
        parentValueContainer.orientation = LinearLayout.VERTICAL
        val parentValueLayParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //parentValueLayParams.setMargins(20, 0, 20, 0)
        parentValueContainer.setBackgroundResource(R.drawable.black_box)
        parentValueContainer.layoutParams = parentValueLayParams
        parentValueLayParams.weight = 1.0f


        resp.iterator().forEach {

            //Parent layout
            val numberViewContainer = LinearLayout(this)
            numberViewContainer.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            //layoutParams.setMargins(20, 0, 20, 0)
            numberViewContainer.setBackgroundResource(R.drawable.black_box)
            numberViewContainer.layoutParams = layoutParams


            //First - TextView container
            val numberViewContainer4 = LinearLayout(this)
            val layoutParams4 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer4.setPadding(5, 5, 5, 5)
            numberViewContainer4.layoutParams = layoutParams4
            layoutParams4.weight = 1.0f

            val subParentTxt = TextView(this)
            subParentTxt.textSize = 15f
            subParentTxt.gravity = Gravity.CENTER
            subParentTxt.setTextColor(Color.BLACK)
            subParentTxt.setPadding(5, 5, 5, 5)
            subParentTxt.text = createStringForViewLabel(false, it.asString)
            numberViewContainer4.addView(subParentTxt)



            //Second - container
            val numberViewContainer1 = LinearLayout(this)
            numberViewContainer1.orientation = LinearLayout.VERTICAL
            val layoutParams1 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer1.setPadding(5, 5, 5, 5)
            numberViewContainer1.setBackgroundResource(R.drawable.black_box)
            numberViewContainer1.layoutParams = layoutParams1
            layoutParams1.weight = 0.5f

            //Third - container
            val numberViewContainer2 = LinearLayout(this)
            numberViewContainer2.orientation = LinearLayout.VERTICAL
            val layoutParams2 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer2.setPadding(5, 5, 5, 5)
            numberViewContainer2.layoutParams = layoutParams2
            layoutParams2.weight = 0.5f

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

            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            component.maxlength?.let {
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
            }

            numberViewContainer1.addView(editText)

            numberViewContainer.addView(numberViewContainer4)
            numberViewContainer.addView(numberViewContainer1)
            parentValueContainer.addView(numberViewContainer)

            numberViewContainer1.gravity = Gravity.CENTER
            numberViewContainer2.gravity = Gravity.CENTER
            numberViewContainer4.gravity = Gravity.CENTER

        }

        // new parent  view add
        parentContainer.addView(parentTxtContainer)
        parentContainer.addView(parentValueContainer)

        parentTxtContainer.gravity = Gravity.CENTER
        parentValueContainer.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(parentContainer)
    }

    private fun createNumTest(component: FormComponentItem) {

        val resp: JsonArray = JsonParser().parse(component.placeholdervalue).asJsonArray

        //numtestParent
        val parentContainer = LinearLayout(this)
        parentContainer.orientation = LinearLayout.HORIZONTAL
        val parentLayParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parentLayParams.setMargins(20, 0, 20, 0)
        parentContainer.setBackgroundResource(R.drawable.black_box)
        parentContainer.layoutParams = parentLayParams


        //Parent Text container
        val parentTxtContainer = LinearLayout(this)
        val parentTxtLay = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        parentTxtContainer.setPadding(5, 5, 5, 5)
        parentTxtContainer.layoutParams = parentTxtLay
        parentTxtLay.weight = 0.5f

        component.placeholder?.let { labelString ->
            val parentTxtV = TextView(this)
            parentTxtV.textSize = 15f
            parentTxtV.gravity = Gravity.CENTER
            parentTxtV.setTextColor(Color.BLACK)
            parentTxtV.setPadding(5, 5, 5, 5)
            parentTxtV.text = createStringForViewLabel(false, labelString)
            parentTxtContainer.addView(parentTxtV)
        }
        //====================================================================================================================================

        val parentValueContainer = LinearLayout(this)
        parentValueContainer.orientation = LinearLayout.VERTICAL
        val parentValueLayParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //parentValueLayParams.setMargins(20, 0, 20, 0)
        parentValueContainer.setBackgroundResource(R.drawable.black_box)
        parentValueContainer.layoutParams = parentValueLayParams
        parentValueLayParams.weight = 1.0f


        resp.iterator().forEach {

            //Parent layout
            val numberViewContainer = LinearLayout(this)
            numberViewContainer.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            //layoutParams.setMargins(20, 0, 20, 0)
            numberViewContainer.setBackgroundResource(R.drawable.black_box)
            numberViewContainer.layoutParams = layoutParams


            //First - TextView container
            val numberViewContainer4 = LinearLayout(this)
            val layoutParams4 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer4.setPadding(5, 5, 5, 5)
            numberViewContainer4.layoutParams = layoutParams4
            layoutParams4.weight = 1.0f

            val subParentTxt = TextView(this)
            subParentTxt.textSize = 15f
            subParentTxt.gravity = Gravity.CENTER
            subParentTxt.setTextColor(Color.BLACK)
            subParentTxt.setPadding(5, 5, 5, 5)
            subParentTxt.text = createStringForViewLabel(false, it.asString)
            numberViewContainer4.addView(subParentTxt)



            //Second - container
            val numberViewContainer1 = LinearLayout(this)
            numberViewContainer1.orientation = LinearLayout.VERTICAL
            val layoutParams1 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer1.setPadding(5, 5, 5, 5)
            numberViewContainer1.setBackgroundResource(R.drawable.black_box)
            numberViewContainer1.layoutParams = layoutParams1
            layoutParams1.weight = 0.5f

            //Third - container
            val numberViewContainer2 = LinearLayout(this)
            numberViewContainer2.orientation = LinearLayout.VERTICAL
            val layoutParams2 = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            numberViewContainer2.setPadding(5, 5, 5, 5)
            numberViewContainer2.layoutParams = layoutParams2
            layoutParams2.weight = 0.5f

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
            editTextParam.setMargins(10, 10, 10, 10)
            val editText1 = TextView(this)
            editText1.gravity = Gravity.CENTER
            editText1.layoutParams = editTextParam
            editText1.setPadding(10, 10, 10, 10)
            editText1.inputType = InputType.TYPE_CLASS_NUMBER
            editText1.setBackgroundResource(R.drawable.boxcurved)
            component.placeholder?.let {
                editText.hint = it
            }


            var edtTxtNumM = 0
            editText.setText(edtTxtNumM.toString())

            var edtTxtNumF = 0
            editText1.setText(edtTxtNumF.toString())

            component.maxlength?.let {
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
            }

            component.maxlength?.let {
                editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
            }

            numberViewContainer1.addView(editText)
            numberViewContainer2.addView(editText1)

            numberViewContainer.addView(numberViewContainer4)
            numberViewContainer.addView(numberViewContainer1)
            numberViewContainer.addView(numberViewContainer2)
            parentValueContainer.addView(numberViewContainer)

            numberViewContainer1.gravity = Gravity.CENTER
            numberViewContainer2.gravity = Gravity.CENTER
            numberViewContainer4.gravity = Gravity.CENTER
            numberViewContainer.gravity = Gravity.CENTER

        }

        // new parent  view add
        parentContainer.addView(parentTxtContainer)
        parentContainer.addView(parentValueContainer)

        parentTxtContainer.gravity = Gravity.CENTER
        parentValueContainer.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(parentContainer)

    }

    /*private fun createMalyrsbase(component: FormComponentItem) {
        isLabelNull(component)

        *//*if (component.labelvalue != null){
            val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
            val labelHeadType = resp.get("labelheadtype").asString

            if (labelHeadType == "yrlabel"){
                malHeadingTable(component)
            }else if (labelHeadType == "inoutlabel"){
                malHtblOutIn(component)
            }else if (labelHeadType == "headtwosubhead"){
                headTwoSubHead(component)
            }
        }*//*
        threeZeroViw(component)
    }*/

    /*private fun createMalOutInBase(component: FormComponentItem) {
        isLabelNull(component)

        *//*if (component.labelvalue != null){
            val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
            val labelHeadType = resp.get("labelheadtype").asString

            if (labelHeadType == "yrlabel"){
                malHeadingTable(component)
            }else if (labelHeadType == "inoutlabel"){
                malHtblOutIn(component)
            }
        }*//*
        fourZeroViw(component)
    }*/

    private fun threeZeroViw(component: FormComponentItem) {
        isLabelNull(component)

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
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
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f



        //Fourth - container
        val numContainerH15 = LinearLayout(this)
        numContainerH15.orientation = LinearLayout.VERTICAL
        val layParamsH15 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContainerH15.setPadding(5, 5, 5, 5)
        numContainerH15.setBackgroundResource(R.drawable.black_box)
        numContainerH15.layoutParams = layParamsH15
        layParamsH15.weight = 0.5f

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
        editTextParam.setMargins(10, 10, 10, 10)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }

        //Fourth - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val edtTxtH15 = TextView(this)
        edtTxtH15.gravity = Gravity.CENTER
        edtTxtH15.layoutParams = editTextParam
        edtTxtH15.setPadding(10, 10, 10, 10)
        edtTxtH15.inputType = InputType.TYPE_CLASS_NUMBER
        edtTxtH15.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }


        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        var edtTxtNumH15 = 0
        edtTxtH15.setText(edtTxtNumH15.toString())

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        component.maxlength?.let {
            edtTxtH15.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer1.addView(editText)
        numberViewContainer2.addView(editText1)
        numContainerH15.addView(edtTxtH15)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer.addView(numContainerH15)

        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        numContainerH15.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    /* private fun createNumInpatient(component: FormComponentItem) {


         fourZeroViw(component)

     }*/

    private fun fourZeroViw(component: FormComponentItem) {
        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer4.gravity = Gravity.CENTER

        val editTextParam = LinearLayout.LayoutParams(
            80,
            50
        )

        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

        //Fourth - container
        val numContrDeathMale = LinearLayout(this)
        numContrDeathMale.orientation = LinearLayout.VERTICAL
        val layParamsDeathMale = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrDeathMale.setPadding(5, 5, 5, 5)
        numContrDeathMale.setBackgroundResource(R.drawable.black_box)
        numContrDeathMale.layoutParams = layParamsDeathMale
        layParamsDeathMale.weight = 0.5f

        //Fifth - container
        val numContrDeathFemale = LinearLayout(this)
        numContrDeathFemale.orientation = LinearLayout.VERTICAL
        val layParamsDeathFemale = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numContrDeathFemale.setPadding(5, 5, 5, 5)
        numContrDeathFemale.setBackgroundResource(R.drawable.black_box)
        numContrDeathFemale.layoutParams = layParamsDeathFemale
        layParamsDeathFemale.weight = 0.5f

        //======================================================================================================================================================================
        val resp: JsonObject = JsonParser().parse(component.valueH).asJsonObject
        val dischargeValue = resp.get("discharge").asString
        val deathValue = resp.get("deaths").asString


        if (dischargeValue.toString().trim().toLowerCase() == "b"){
            dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)
            dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

            if (deathValue.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
            }else if (deathValue.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

            }else if (deathValue.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

            }else if (deathValue.toString().trim().toLowerCase() == "n"){

            }

        }else if (dischargeValue.toString().trim().toLowerCase() == "m"){
            dischargeMaleMethod(component, numberViewContainer, editTextParam, numberViewContainer1)

            if (deathValue.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
            }else if (deathValue.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

            }else if (deathValue.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

            }else if (deathValue.toString().trim().toLowerCase() == "n"){

            }

        }else if (dischargeValue.toString().trim().toLowerCase() == "f"){
            dischargeFemaleMethod(component, numberViewContainer, editTextParam, numberViewContainer2)

            if (deathValue.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
            }else if (deathValue.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

            }else if (deathValue.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

            }else if (deathValue.toString().trim().toLowerCase() == "n"){

            }

        }else if (dischargeValue.toString().trim().toLowerCase() == "n"){

            if (deathValue.toString().trim().toLowerCase() == "b"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)
            }else if (deathValue.toString().trim().toLowerCase() == "m"){
                deathsMaleMethod(component, numberViewContainer, editTextParam, numContrDeathMale)

            }else if (deathValue.toString().trim().toLowerCase() == "f"){
                deathsFemaleMethod(component, numberViewContainer, editTextParam, numContrDeathFemale)

            }else if (deathValue.toString().trim().toLowerCase() == "n"){

            }

        }


        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer.addView(numContrDeathMale)
        numberViewContainer.addView(numContrDeathFemale)


        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numContrDeathMale.gravity = Gravity.CENTER
        numContrDeathFemale.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    private fun deathsMaleMethod(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        editTextParam: LinearLayout.LayoutParams,
        numContrDeathMale: LinearLayout
    ) {


        //Fourth - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val edtTxtDeathMale = TextView(this)
        edtTxtDeathMale.gravity = Gravity.CENTER
        edtTxtDeathMale.layoutParams = editTextParam
        edtTxtDeathMale.setPadding(10, 10, 10, 10)
        edtTxtDeathMale.inputType = InputType.TYPE_CLASS_NUMBER
        edtTxtDeathMale.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtDeathM = 0
        edtTxtDeathMale.setText(edtTxtDeathM.toString())

        component.maxlength?.let {
            edtTxtDeathMale.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numContrDeathMale.addView(edtTxtDeathMale)

    }

    private fun deathsFemaleMethod(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        editTextParam: LinearLayout.LayoutParams,
        numContrDeathFemale: LinearLayout
    ) {


        //Fifth - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val edtTxtDeathFemale = TextView(this)
        edtTxtDeathFemale.gravity = Gravity.CENTER
        edtTxtDeathFemale.layoutParams = editTextParam
        edtTxtDeathFemale.setPadding(10, 10, 10, 10)
        edtTxtDeathFemale.inputType = InputType.TYPE_CLASS_NUMBER
        edtTxtDeathFemale.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtDeathF = 0
        edtTxtDeathFemale.setText(edtTxtDeathF.toString())

        component.maxlength?.let {
            edtTxtDeathFemale.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numContrDeathFemale.addView(edtTxtDeathFemale)



    }

    private fun dischargeFemaleMethod(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        editTextParam: LinearLayout.LayoutParams,
        numberViewContainer2: LinearLayout
    ) {


        //Thrid - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer2.addView(editText1)


    }

    private fun dischargeMaleMethod(
        component: FormComponentItem,
        numberViewContainer: LinearLayout,
        editTextParam: LinearLayout.LayoutParams,
        numberViewContainer1: LinearLayout
    ) {


        //Second - container textView

        editTextParam.setMargins(10, 10, 10, 10)
        val editText = TextView(this)
        editText.layoutParams = editTextParam
        editText.gravity = Gravity.CENTER
        editText.setPadding(10, 10, 10, 10)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setBackgroundResource(R.drawable.boxcurved)
        /*component.placeholder?.let {
            editText.hint = it
        }*/

        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer1.addView(editText)


    }

    /*private fun isMalLabelNull(viewComponentModel: DepthArrayModel) {
        createMalLabelForViews(viewComponentModel)
    }

    private fun createMalLabelForViews(viewComponentModel: DepthArrayModel) {
        val label = TextView(this)
        label.setTextColor(Color.BLACK)
        label.setTypeface(null, Typeface.BOLD)
        label.textSize = 20f
        setMerginToviews(
            label,
            40,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        viewComponentModel.title.let { labelText ->

            label.text = createStringForViewLabel(false, labelText)
            binding.miniAppFormContainer.addView(label)

        }

    }*/

    private fun isLabelNull(viewComponentModel: FormComponentItem) {
        if (viewComponentModel.label != null) createLabelForViews(viewComponentModel)
    }

    private fun createLabelForViews(viewComponentModel: FormComponentItem) {
        val label = TextView(this)
        label.setTextColor(Color.BLACK)
        label.setTypeface(null, Typeface.BOLD)
        label.textSize = 20f
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

    private fun createNumberOne(component: FormComponentItem) {
        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.0f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString)
            numberViewContainer4.addView(textView)
        }


        /*//Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f*/

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

        //Second - container textView
        val editTextParam = LinearLayout.LayoutParams(
            100,
            50
        )
        /*editTextParam.setMargins(10, 10, 10, 10)
        val editText = TextView(this)
        editText.layoutParams = editTextParam
        editText.gravity = Gravity.CENTER
        editText.setPadding(10, 10, 10, 10)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }*/

        //Thrid - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer2.addView(editText1)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)

    }


    private fun createNumberEditText(component: FormComponentItem) {

        isLabelNull(component)

        /*if (component.labelvalue != null){
            val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
            val labelHeadType = resp.get("labelheadtype").asString

            if (labelHeadType == "yrlabel"){
                malHeadingTable(component)
            }else if (labelHeadType == "inoutlabel"){
                malHtblOutIn(component)
            }else if (labelHeadType == "headtwosubhead"){
                headTwoSubHead(component)
            }
        }*/

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
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
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

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
        editTextParam.setMargins(10, 10, 10, 10)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)
        component.placeholder?.let {
            editText.hint = it
        }


        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer1.addView(editText)
        numberViewContainer2.addView(editText1)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    /*private fun headTwoSubHead(component: FormComponentItem) {
        val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
        val headLabelmain = resp.get("labelheading").asString
        val labelHeadOne = resp.get("subone").asString
        val labelHeadTwo = resp.get("subtwo").asString

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        val mainTxtHead = TextView(this)
        mainTxtHead.textSize = 15f
        mainTxtHead.gravity = Gravity.CENTER
        mainTxtHead.setTextColor(Color.BLACK)
        mainTxtHead.typeface = Typeface.DEFAULT_BOLD
        mainTxtHead.setPadding(5, 5, 5, 5)
        mainTxtHead.text = createStringForViewLabel(false, headLabelmain)
        numberViewContainer4.addView(mainTxtHead)

        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f
        val subTxtHeadOne = TextView(this)
        subTxtHeadOne.textSize = 15f
        subTxtHeadOne.gravity = Gravity.CENTER
        subTxtHeadOne.setTextColor(Color.BLACK)
        subTxtHeadOne.typeface = Typeface.DEFAULT_BOLD
        subTxtHeadOne.setPadding(5, 5, 5, 5)
        subTxtHeadOne.text = createStringForViewLabel(false, labelHeadOne)
        numberViewContainer1.addView(subTxtHeadOne)


        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams2.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(10, 10, 10, 10)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f


        val subTxtHeadTwo = TextView(this)
        subTxtHeadTwo.textSize = 15f
        subTxtHeadTwo.gravity = Gravity.CENTER
        subTxtHeadTwo.setTextColor(Color.BLACK)
        subTxtHeadTwo.typeface = Typeface.DEFAULT_BOLD
        subTxtHeadTwo.setPadding(5, 5, 5, 5)
        subTxtHeadTwo.text = createStringForViewLabel(false, labelHeadTwo)
        numberViewContainer2.addView(subTxtHeadTwo)


        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)

    }*/

    private fun titleTwoBoxValueMF(component: FormComponentItem) {
        val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
        val headLabelmain = resp.get("headlabelm").asString
        val labelHeadOne = resp.get("labelhone").asString
        val labelHeadTwo = resp.get("labelhtwo").asString

        isLabelNull(component)

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 30, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        val txtVgender= TextView(this)
        txtVgender.textSize = 15f
        txtVgender.gravity = Gravity.CENTER
        txtVgender.setTextColor(Color.BLACK)
        txtVgender.typeface = Typeface.DEFAULT_BOLD
        txtVgender.setPadding(5, 5, 5, 5)
        txtVgender.text = createStringForViewLabel(false, headLabelmain)
        numberViewContainer4.addView(txtVgender)


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 1.0f


        //Second - container - First Layout (Out Patient)
        val malOutPatientContainer = LinearLayout(this)
        malOutPatientContainer.orientation = LinearLayout.HORIZONTAL
        val malOutPatientLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        malOutPatientContainer.gravity = Gravity.CENTER
        malOutPatientContainer.setPadding(5, 5, 5, 5)
        malOutPatientContainer.setBackgroundResource(R.drawable.black_box)
        malOutPatientContainer.layoutParams = malOutPatientLay

        val malOutPatientHtxt = TextView(this)
        malOutPatientHtxt.textSize = 15f
        malOutPatientHtxt.gravity = Gravity.CENTER
        malOutPatientHtxt.setTextColor(Color.BLACK)
        malOutPatientHtxt.typeface = Typeface.DEFAULT_BOLD
        malOutPatientHtxt.setPadding(5, 5, 5, 5)
        malOutPatientHtxt.text = createStringForViewLabel(false, labelHeadOne)
        malOutPatientContainer.addView(malOutPatientHtxt)

        //Second - container - Second Layout (Gender mention)
        val malOutPatientGender = LinearLayout(this)
        malOutPatientGender.orientation = LinearLayout.HORIZONTAL
        val malOutPatientGenderLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        malOutPatientGender.setBackgroundResource(R.drawable.black_box)
        malOutPatientGender.layoutParams = malOutPatientGenderLay

        val maleContainer = LinearLayout(this)
        val maleContainerLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        maleContainer.gravity = Gravity.CENTER
        maleContainer.setBackgroundResource(R.drawable.black_box)
        maleContainer.layoutParams = maleContainerLay
        maleContainerLay.weight = 1f

        val malOutPatientGm = TextView(this)
        malOutPatientGm.textSize = 15f
        malOutPatientGm.gravity = Gravity.CENTER
        malOutPatientGm.setTextColor(Color.BLACK)
        malOutPatientGm.typeface = Typeface.DEFAULT_BOLD
        malOutPatientGm.setPadding(5, 5, 5, 5)
        malOutPatientGm.text = createStringForViewLabel(false, "MALE")
        maleContainer.addView(malOutPatientGm)

        val femaleContainer = LinearLayout(this)
        val femaleContainerLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        femaleContainer.gravity = Gravity.CENTER
        femaleContainer.setBackgroundResource(R.drawable.black_box)
        femaleContainer.layoutParams = femaleContainerLay
        femaleContainerLay.weight = 1f

        val malOutPatientGf = TextView(this)
        malOutPatientGf.textSize = 15f
        malOutPatientGf.gravity = Gravity.CENTER
        malOutPatientGf.setTextColor(Color.BLACK)
        malOutPatientGf.typeface = Typeface.DEFAULT_BOLD
        malOutPatientGf.setPadding(5, 5, 5, 5)
        malOutPatientGf.text = createStringForViewLabel(false, "FEMALE")
        femaleContainer.addView(malOutPatientGf)

        malOutPatientGender.addView(maleContainer)
        malOutPatientGender.addView(femaleContainer)

        numberViewContainer1.addView(malOutPatientContainer)
        numberViewContainer1.addView(malOutPatientGender)

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 1.0f


        //third - container - First Layout (In Patient)
        val malInPatientContainer = LinearLayout(this)
        malInPatientContainer.orientation = LinearLayout.HORIZONTAL
        val malInPatientLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        malInPatientContainer.gravity = Gravity.CENTER
        malInPatientContainer.setPadding(5,5,5, 5)
        malInPatientContainer.setBackgroundResource(R.drawable.black_box)
        malInPatientContainer.layoutParams = malInPatientLay

        val malInPatientHtxt = TextView(this)
        malInPatientHtxt.textSize = 15f
        malInPatientHtxt.gravity = Gravity.CENTER
        malInPatientHtxt.setTextColor(Color.BLACK)
        malInPatientHtxt.typeface = Typeface.DEFAULT_BOLD
        malInPatientHtxt.setPadding(5, 5, 5, 5)
        malInPatientHtxt.text = createStringForViewLabel(false, labelHeadTwo)
        malInPatientContainer.addView(malInPatientHtxt)

        //Third - container - Second Layout (Gender mention)
        val malInPatientGender = LinearLayout(this)
        malInPatientGender.orientation = LinearLayout.HORIZONTAL
        val malInPatientGenderLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        malInPatientGender.setBackgroundResource(R.drawable.black_box)
        malInPatientGender.layoutParams = malInPatientGenderLay

        val maleContainerThird = LinearLayout(this)
        val maleContainerThirdLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        maleContainerThird.gravity = Gravity.CENTER
        maleContainerThird.setBackgroundResource(R.drawable.black_box)
        maleContainerThird.layoutParams = maleContainerThirdLay
        maleContainerThirdLay.weight = 1f
        val malInPatientGm = TextView(this)
        malInPatientGm.textSize = 15f
        malInPatientGm.gravity = Gravity.CENTER
        malInPatientGm.setTextColor(Color.BLACK)
        malInPatientGm.typeface = Typeface.DEFAULT_BOLD
        malInPatientGm.setPadding(5, 5, 5, 5)
        malInPatientGm.text = createStringForViewLabel(false, "MALE")
        maleContainerThird.addView(malInPatientGm)

        val femaleContainerThird = LinearLayout(this)
        val femaleContainerThirdLay = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        femaleContainerThird.gravity = Gravity.CENTER
        femaleContainerThird.setBackgroundResource(R.drawable.black_box)
        femaleContainerThird.layoutParams = femaleContainerThirdLay
        femaleContainerThirdLay.weight = 1f
        val malInPatientGf = TextView(this)
        malInPatientGf.textSize = 15f
        malInPatientGf.gravity = Gravity.CENTER
        malInPatientGf.setTextColor(Color.BLACK)
        malInPatientGf.typeface = Typeface.DEFAULT_BOLD
        malInPatientGf.setPadding(5, 5, 5, 5)
        malInPatientGf.text = createStringForViewLabel(false, "FEMALE")
        femaleContainerThird.addView(malInPatientGf)

        malInPatientGender.addView(maleContainerThird)
        malInPatientGender.addView(femaleContainerThird)

        numberViewContainer2.addView(malInPatientContainer)
        numberViewContainer2.addView(malInPatientGender)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)

        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    private fun titleThreeBoxValue(component: FormComponentItem) {
        val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
        val labelHeading = resp.get("labelheading").asString
        val subOne = resp.get("subone").asString
        val subTwo = resp.get("subtwo").asString
        val subThree = resp.get("subthree").asString

        isLabelNull(component)

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 30, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        val txtVgender= TextView(this)
        txtVgender.textSize = 15f
        txtVgender.gravity = Gravity.CENTER
        txtVgender.setTextColor(Color.BLACK)
        txtVgender.typeface = Typeface.DEFAULT_BOLD
        txtVgender.setPadding(5, 5, 5, 5)
        txtVgender.text = createStringForViewLabel(false, labelHeading.toString())
        numberViewContainer4.addView(txtVgender)


        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f


        val textViewH = TextView(this)
        textViewH.textSize = 15f
        textViewH.gravity = Gravity.CENTER
        textViewH.setTextColor(Color.BLACK)
        textViewH.typeface = Typeface.DEFAULT_BOLD
        textViewH.setPadding(5, 5, 5, 5)
        textViewH.text = createStringForViewLabel(false, subOne.toString())
        numberViewContainer1.addView(textViewH)


        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams2.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(10, 10, 10, 10)
        numberViewContainer2.setBackgroundResource(R.drawable.black_box)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f


        val textView = TextView(this)
        textView.textSize = 15f
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setPadding(5, 5, 5, 5)
        textView.text = createStringForViewLabel(false, subTwo.toString())
        numberViewContainer2.addView(textView)


        //Fourth - container
        val numHeading15 = LinearLayout(this)
        val layParamsH15 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layParamsH15.gravity = Gravity.CENTER
        numHeading15.setPadding(10, 10, 10, 10)
        numHeading15.setBackgroundResource(R.drawable.black_box)
        numHeading15.layoutParams = layParamsH15
        layParamsH15.weight = 0.5f


        val textViewH15 = TextView(this)
        textViewH15.textSize = 15f
        textViewH15.gravity = Gravity.CENTER
        textViewH15.setTextColor(Color.BLACK)
        textViewH15.typeface = Typeface.DEFAULT_BOLD
        textViewH15.setPadding(5, 5, 5, 5)
        textViewH15.text = createStringForViewLabel(false, subThree.toString())
        numHeading15.addView(textViewH15)


        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer.addView(numHeading15)

        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        numHeading15.gravity = Gravity.CENTER

        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    private fun createTableTitle(component: FormComponentItem) {

        val resp: JsonObject = JsonParser().parse(component.labelvalue).asJsonObject
        val headLabelmain = resp.get("labelheading").asString
        val labelHeadOne = resp.get("subone").asString
        val labelHeadTwo = resp.get("subtwo").asString

        isLabelNull(component)

        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 5, 20, 5)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        val txtVmainHeading= TextView(this)
        txtVmainHeading.textSize = 15f
        txtVmainHeading.gravity = Gravity.CENTER
        txtVmainHeading.setTextColor(Color.BLACK)
        txtVmainHeading.typeface = Typeface.DEFAULT_BOLD
        txtVmainHeading.setPadding(5, 5, 5, 5)
        txtVmainHeading.text = createStringForViewLabel(false, headLabelmain)
        numberViewContainer4.addView(txtVmainHeading)

        //Second - container
        val numberViewContainer1 = LinearLayout(this)
        numberViewContainer1.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams1.gravity = Gravity.CENTER
        numberViewContainer1.setPadding(5, 5, 5, 5)
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        val txtVsubHone = TextView(this)
        txtVsubHone.textSize = 15f
        txtVsubHone.gravity = Gravity.CENTER
        txtVsubHone.setTextColor(Color.BLACK)
        txtVsubHone.typeface = Typeface.DEFAULT_BOLD
        txtVsubHone.setPadding(5, 5, 5, 5)
        txtVsubHone.text = createStringForViewLabel(false, labelHeadOne)
        numberViewContainer1.addView(txtVsubHone)


        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams2.gravity = Gravity.CENTER
        numberViewContainer2.setPadding(10, 10, 10, 10)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

        val txtVsubHtwo = TextView(this)
        txtVsubHtwo.textSize = 15f
        txtVsubHtwo.gravity = Gravity.CENTER
        txtVsubHtwo.setTextColor(Color.BLACK)
        txtVsubHtwo.typeface = Typeface.DEFAULT_BOLD
        txtVsubHtwo.setPadding(5, 5, 5, 5)
        txtVsubHtwo.text = createStringForViewLabel(false, labelHeadTwo)
        numberViewContainer2.addView(txtVsubHtwo)


        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)
    }

    private fun createFemale(component: FormComponentItem) {

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams


        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
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
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

        //Second - container textView
        val editTextParam = LinearLayout.LayoutParams(
            100,
            50
        )

        //Thrid - Container TextView
        editTextParam.setMargins(10, 10, 10, 10)
        val editText1 = TextView(this)
        editText1.gravity = Gravity.CENTER
        editText1.layoutParams = editTextParam
        editText1.setPadding(10, 10, 10, 10)
        editText1.inputType = InputType.TYPE_CLASS_NUMBER
        editText1.setBackgroundResource(R.drawable.boxcurved)

        var edtTxtNumF = 0
        editText1.setText(edtTxtNumF.toString())

        component.maxlength?.let {
            editText1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer2.addView(editText1)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)


    }

    private fun createMale(component: FormComponentItem) {

        isLabelNull(component)
        //Parent layout
        val numberViewContainer = LinearLayout(this)
        numberViewContainer.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 0)
        numberViewContainer.setBackgroundResource(R.drawable.black_box)
        numberViewContainer.layoutParams = layoutParams

        //First - TextView container
        val numberViewContainer4 = LinearLayout(this)
        val layoutParams4 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer4.setPadding(5, 5, 5, 5)
        numberViewContainer4.layoutParams = layoutParams4
        layoutParams4.weight = 1.5f

        component.placeholder?.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
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
        numberViewContainer1.setBackgroundResource(R.drawable.black_box)
        numberViewContainer1.layoutParams = layoutParams1
        layoutParams1.weight = 0.5f

        //Third - container
        val numberViewContainer2 = LinearLayout(this)
        numberViewContainer2.orientation = LinearLayout.VERTICAL
        val layoutParams2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberViewContainer2.setPadding(5, 5, 5, 5)
        numberViewContainer2.layoutParams = layoutParams2
        layoutParams2.weight = 0.5f

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

        var edtTxtNumM = 0
        editText.setText(edtTxtNumM.toString())

        component.maxlength?.let {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.toInt()))
        }

        numberViewContainer1.addView(editText)

        numberViewContainer.addView(numberViewContainer4)
        numberViewContainer.addView(numberViewContainer1)
        numberViewContainer.addView(numberViewContainer2)
        numberViewContainer1.gravity = Gravity.CENTER
        numberViewContainer2.gravity = Gravity.CENTER
        numberViewContainer4.gravity = Gravity.CENTER
        binding.miniAppFormContainer.addView(numberViewContainer)

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

                        var reg_it = it.reg_id

                        if ( it.reg_id == "99" || it.reg_id == "100" || it.reg_id == "101" || it.reg_id == "102" || it.reg_id == "103" || it.reg_id == "104"
                            || it.reg_id == "105" || it.reg_id == "106" || it.reg_id == "107" || it.reg_id == "108" || it.reg_id == "109" || it.reg_id == "110"
                            || it.reg_id == "111" || it.reg_id == "112" || it.reg_id == "113" || it.reg_id == "115"){
                            val text1 = it.depthArr
                            text1.iterator().forEach {
                                val txt1 = it.subTitleContent
                                val rowArry = it.rowArr

                                var numSTC = 0
                                var laySTC = 0
                                var numRA = 0
                                var layRA = 0


                                //===========================================================================
                                val label = TextView(this)
                                label.setTextColor(Color.BLACK)
                                label.setTypeface(null, Typeface.BOLD)
                                label.textSize = 20f
                                setMerginToviews(
                                    label,
                                    40,
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
                                        layoutParams.setMargins(20, 0, 20, 0)
                                        numberViewContainer.setBackgroundResource(R.drawable.black_box)
                                        numberViewContainer.layoutParams = layoutParams

                                        //---------------------------------------------------------------------------------------
                                        //val subTitleArry = it.subTitleArr.toString()
                                        val gson1 = Gson()
                                        val arrayData2 = gson1.toJson(it.subTitleArr)

                                        populateMalForm(arrayData2, numberViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0)
                                        numSTC++
                                        laySTC++
                                        loopCountNum++

                                        //--------------------------------------------------------------------------------------------

                                        numberViewContainer.gravity = Gravity.CENTER
                                        // nVContainer.addView(numberViewContainer)
                                        binding.miniAppFormContainer.addView(numberViewContainer)
                                    }
                                }


                                /*nVContainer.gravity = Gravity.CENTER
                                binding.miniAppFormContainer.addView(nVContainer)*/

                                rowArry.iterator().forEach {
                                    //Parent layout
                                    val numVContainer = LinearLayout(this)
                                    numVContainer.orientation = LinearLayout.HORIZONTAL
                                    val layPrams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    layPrams.setMargins(20, 0, 20, 0)
                                    numVContainer.setBackgroundResource(R.drawable.black_box)
                                    numVContainer.layoutParams = layPrams

                                    //===============================================================================
                                    val dataColRowsArry = it.dataColRows.toString()

                                    val gson3 = Gson()
                                    val arrayData3 = gson3.toJson(it.dataColRows)


                                    populateMalForm(arrayData3, numVContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size)
                                    numRA++
                                    layRA++

                                    //======================================================================================
                                    numVContainer.gravity = Gravity.CENTER
                                    binding.miniAppFormContainer.addView(numVContainer)
                                }


                            }

                        }
                        else if (it.reg_id == "116"){
                            val text1 = it.depthArr
                            text1.iterator().forEach {
                                val txt1 = it.subTitleContent
                                val rowArry = it.rowArr

                                var numSTC = 0
                                var laySTC = 0
                                var numRA = 0
                                var layRA = 0

                                //===========================================================================
                                val label = TextView(this)
                                label.setTextColor(Color.BLACK)
                                if (it.fontWeight.toString().toLowerCase() == "normal"){
                                    label.setTypeface(null, Typeface.NORMAL)
                                }else{
                                    label.setTypeface(null, Typeface.BOLD)
                                }

                                label.textSize = 20f
                                setMerginToviews(
                                    label,
                                    40,
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
                                        layoutParams.setMargins(20, 0, 20, 0)
                                        //numberViewContainer.setBackgroundResource(R.drawable.black_box)
                                        numberViewContainer.layoutParams = layoutParams

                                        //---------------------------------------------------------------------------------------
                                        //val subTitleArry = it.subTitleArr.toString()
                                        val gson1 = Gson()
                                        val arrayData2 = gson1.toJson(it.subTitleArr)

                                        populateSuvForm(arrayData2, numberViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0)
                                        numSTC++
                                        laySTC++
                                        loopCountNum++

                                        //--------------------------------------------------------------------------------------------

                                        numberViewContainer.gravity = Gravity.CENTER
                                        // nVContainer.addView(numberViewContainer)
                                        binding.miniAppFormContainer.addView(numberViewContainer)
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
                                    layPrams.setMargins(20, 0, 20, 0)
                                    //numVContainer.setBackgroundResource(R.drawable.black_box)
                                    numVContainer.layoutParams = layPrams

                                    //===============================================================================
                                    val dataColRowsArry = it.dataColRows.toString()

                                    val gson3 = Gson()
                                    val arrayData3 = gson3.toJson(it.dataColRows)


                                    populateSuvForm(arrayData3, numVContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size)
                                    numRA++
                                    layRA++

                                    //======================================================================================
                                    numVContainer.gravity = Gravity.CENTER
                                    binding.miniAppFormContainer.addView(numVContainer)
                                }


                            }

                        }
                        else if (it.reg_id == "114"){
                            val text1 = it.depthArr
                            text1.iterator().forEach {
                                val txt1 = it.subTitleContent
                                val rowArry = it.rowArr

                                var numSTC = 0
                                var laySTC = 0
                                var numRA = 0
                                var layRA = 0

                                //===========================================================================
                                val label = TextView(this)
                                label.setTextColor(Color.BLACK)

                                try {
                                    if (it.fontWeight.toString().toLowerCase() == "normal"){
                                        label.setTypeface(null, Typeface.NORMAL)
                                    }else{
                                        label.setTypeface(null, Typeface.BOLD)
                                    }
                                }catch (e: Exception){}


                                label.textSize = 20f
                                setMerginToviews(
                                    label,
                                    40,
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
                                        layoutParams.setMargins(20, 0, 20, 0)
                                        //numberViewContainer.setBackgroundResource(R.drawable.black_box)
                                        numberViewContainer.layoutParams = layoutParams

                                        //---------------------------------------------------------------------------------------
                                        //val subTitleArry = it.subTitleArr.toString()
                                        val gson1 = Gson()
                                        val arrayData2 = gson1.toJson(it.subTitleArr)

                                        populateAidForm(arrayData2, numberViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0)
                                        numSTC++
                                        laySTC++
                                        loopCountNum++

                                        //--------------------------------------------------------------------------------------------

                                        numberViewContainer.gravity = Gravity.CENTER
                                        // nVContainer.addView(numberViewContainer)
                                        binding.miniAppFormContainer.addView(numberViewContainer)
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
                                    layPrams.setMargins(20, 0, 20, 0)
                                    //numVContainer.setBackgroundResource(R.drawable.black_box)
                                    numVContainer.layoutParams = layPrams

                                    //===============================================================================
                                    val dataColRowsArry = it.dataColRows.toString()

                                    val gson3 = Gson()
                                    val arrayData3 = gson3.toJson(it.dataColRows)


                                    populateAidForm(arrayData3, numVContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size)
                                    numRA++
                                    layRA++

                                    //======================================================================================
                                    numVContainer.gravity = Gravity.CENTER
                                    binding.miniAppFormContainer.addView(numVContainer)
                                }


                            }

                        }
                        else if (it.reg_id == "117"){
                            val text1 = it.depthArr

                            //Parent layout
                            val parntContainer = LinearLayout(this)
                            parntContainer.orientation = LinearLayout.HORIZONTAL
                            val parntlayoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            parntlayoutParams.setMargins(0, 0, 0, 0)
                            parntContainer.setBackgroundResource(R.drawable.black_box)
                            parntContainer.layoutParams = parntlayoutParams


                            text1.iterator().forEach {
                                val txt1 = it.subTitleContent
                                val rowArry = it.rowArr

                                var numSTC = 0
                                var laySTC = 0
                                var numRA = 0
                                var layRA = 0


                                //===========================================================================
                                val label = TextView(this)
                                label.setTextColor(Color.BLACK)
                                label.setTypeface(null, Typeface.BOLD)
                                label.textSize = 20f
                                setMerginToviews(
                                    label,
                                    40,
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
                                        val primaryViewContainer = LinearLayout(this)
                                        primaryViewContainer.orientation = LinearLayout.HORIZONTAL
                                        val primaryLayoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        primaryLayoutParams.weight = 0.5f
                                        primaryLayoutParams.setMargins(20, 0, 20, 0)
                                        primaryViewContainer.setBackgroundResource(R.drawable.black_box)
                                        primaryViewContainer.layoutParams = primaryLayoutParams

                                        //Parent layout
                                        val secondaryContainer = LinearLayout(this)
                                        secondaryContainer.orientation = LinearLayout.HORIZONTAL
                                        val secondaryLayoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        secondaryLayoutParams.weight = 1.5f
                                        secondaryLayoutParams.setMargins(20, 0, 20, 0)
                                        secondaryContainer.setBackgroundResource(R.drawable.black_box)
                                        secondaryContainer.layoutParams = secondaryLayoutParams

                                        //---------------------------------------------------------------------------------------
                                        //val subTitleArry = it.subTitleArr.toString()
                                        val gson1 = Gson()
                                        val arrayData2 = gson1.toJson(it.subTitleArr)

                                        populateHIVSurForm(arrayData2, primaryViewContainer, "numContains${numSTC}", "lay${laySTC}",loopCountNum, txt1.size, 0, secondaryContainer)
                                        numSTC++
                                        laySTC++
                                        loopCountNum++

                                        //--------------------------------------------------------------------------------------------

                                        primaryViewContainer.gravity = Gravity.CENTER
                                        parntContainer.addView(primaryViewContainer)
                                        parntContainer.addView(secondaryContainer)
                                        //binding.miniAppFormContainer.addView(numberViewContainer)
                                    }
                                }


                                /*nVContainer.gravity = Gravity.CENTER
                                binding.miniAppFormContainer.addView(nVContainer)*/

                                rowArry.iterator().forEach {
                                    //Parent layout
                                    val priViewContainer = LinearLayout(this)
                                    priViewContainer.orientation = LinearLayout.HORIZONTAL
                                    val priLayoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    priLayoutParams.weight = 0.5f
                                    priLayoutParams.setMargins(20, 0, 20, 0)
                                    priViewContainer.setBackgroundResource(R.drawable.black_box)
                                    priViewContainer.layoutParams = priLayoutParams

                                    //Parent layout
                                    val secoContainer = LinearLayout(this)
                                    secoContainer.orientation = LinearLayout.HORIZONTAL
                                    val secoLayoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    secoLayoutParams.weight = 1.5f
                                    secoLayoutParams.setMargins(20, 0, 20, 0)
                                    secoContainer.setBackgroundResource(R.drawable.black_box)
                                    secoContainer.layoutParams = secoLayoutParams

                                    //===============================================================================
                                    val dataColRowsArry = it.dataColRows.toString()

                                    val gson3 = Gson()
                                    val arrayData3 = gson3.toJson(it.dataColRows)


                                    populateHIVSurForm(arrayData3, priViewContainer, "numContains${numRA}", "lay${layRA}", 0, 0, it.dataColRows.size, secoContainer)
                                    numRA++
                                    layRA++

                                    //======================================================================================
                                    priViewContainer.gravity = Gravity.CENTER
                                    parntContainer.addView(priViewContainer)
                                    parntContainer.addView(secoContainer)
                                    //binding.miniAppFormContainer.addView(numVContainer)
                                }


                            }

                            binding.miniAppFormContainer.addView(parntContainer)

                        }
                        else{
                            val text1 = it.json_op
                            populateForm(text1)
                        }


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
                    WidgetItems.TEXT.label -> createMalText(component, numberViewContainer, num, lay, luupSize, dataRowSize) // Prints 0
                    WidgetItems.CHECKBOX.label -> createCheckBoxGroup(component, secondContainer) // Prints 0
                    WidgetItems.TEXTAREA.label -> createEditableTextArea(component, thirdContainer) // Prints 0
                    WidgetItems.EDITTEXT.label -> createEditableTextView(component, fourthContainer) // Prints 0

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

        // save button

    }
    private fun populateHIVSurForm(
        json: String,
        priViewContainer: LinearLayout,
        num: String,
        lay: String,
        loopCountNum: Int,
        txt1Size: Int,
        dataRowSize: Int,
        secoContainer: LinearLayout
    ) {

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

                    WidgetItems.LABEL.label -> createHIVLabel(component, priViewContainer, num, lay, loopCount, luupSize, secoContainer) // Prints Table Title
                    WidgetItems.TEXT.label -> createHIVText(component, priViewContainer, num, lay, luupSize, dataRowSize, secoContainer) // Prints 0

                }
                loopCount++
            }

        }

        // save button

    }
    private fun populateSuvForm(json: String, numberViewContainer: LinearLayout, num: String, lay: String, loopCountNum: Int, txt1Size: Int, dataRowSize: Int) {

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
        //secondContainer.setBackgroundResource(R.drawable.black_box)
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
        //thirdContainer.setBackgroundResource(R.drawable.black_box)
        thirdContainer.layoutParams = thirdLayoutParams

        //Fourth - Checkbox group container
        val fourthContainer = LinearLayout(this)
        fourthContainer.orientation = LinearLayout.VERTICAL
        val fourthLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
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

                    WidgetItems.LABEL.label -> createSuvLabel(component, numberViewContainer, num, lay, loopCount, luupSize) // Prints Table Title
                    WidgetItems.TEXT.label -> createSuvText(component, numberViewContainer, num, lay, luupSize, dataRowSize) // Prints 0
                    WidgetItems.CHECKBOX.label -> createCheckBoxGroup(component, secondContainer) // Prints 0
                    WidgetItems.TEXTAREA.label -> createEditableTextArea(component, thirdContainer) // Prints 0
                    WidgetItems.EDITTEXT.label -> createEditableTextView(component, fourthContainer) // Prints 0

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
                fourthLayoutParams.weight = 1.5f
                //Fourth - Checkbox group container
                val fContainer = LinearLayout(this)
                fContainer.orientation = LinearLayout.VERTICAL
                val fLayoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                fLayoutParams.weight = 0.5f
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

        // save button

    }

    private fun populateAidForm(json: String, numberViewContainer: LinearLayout, num: String, lay: String, loopCountNum: Int, txt1Size: Int, dataRowSize: Int) {

        checkBoxV = false
        editTextAreaV = false
        editableTextV = false
        radioBtnV = false

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
        //secondContainer.setBackgroundResource(R.drawable.black_box)
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
        //thirdContainer.setBackgroundResource(R.drawable.black_box)
        thirdContainer.layoutParams = thirdLayoutParams

        //Fourth - Checkbox group container
        val fourthContainer = LinearLayout(this)
        fourthContainer.orientation = LinearLayout.VERTICAL
        val fourthLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        fourthContainer.setPadding(5,5,5,5)
        //fourthContainer.setBackgroundResource(R.drawable.black_box)
        fourthContainer.layoutParams = fourthLayoutParams
        fourthContainer.gravity = Gravity.CENTER

        //Radio - Checkbox group container
        val radioContainer = LinearLayout(this)
        radioContainer.orientation = LinearLayout.VERTICAL
        val radioLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        radioLayoutParams.weight = 1.5f
        radioContainer.setPadding(5,5,5,5)
        radioContainer.setBackgroundResource(R.drawable.black_box)
        radioContainer.layoutParams = radioLayoutParams
        radioContainer.gravity = Gravity.CENTER_VERTICAL

        val radioGroup = RadioGroup(this)
        radioGroup.orientation = LinearLayout.HORIZONTAL
        Utils.setMerginToviews(radioGroup)

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

                    WidgetItems.LABEL.label -> createAidLabel(component, numberViewContainer, num, lay, loopCount, luupSize) // Prints Table Title
                    WidgetItems.DROPDOWN.label -> createDropDown(component, numberViewContainer) // Prints Table Title
                    WidgetItems.RADIOBUTTON.label -> createRadioBtn(component, radioGroup) // Prints Table Title
                    WidgetItems.TEXT.label -> createAidText(component, numberViewContainer, num, lay, luupSize, dataRowSize) // Prints 0
                    WidgetItems.CHECKBOX.label -> createCheckBoxGroup(component, secondContainer) // Prints 0
                    WidgetItems.TEXTAREA.label -> createEditableTextArea(component, thirdContainer) // Prints 0
                    WidgetItems.EDITTEXT.label -> createEditableTextView(component, fourthContainer) // Prints 0

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
                fourthLayoutParams.weight = 1.5f
                numberViewContainer.addView(fourthContainer)

            }
            if (radioBtnV == true){
                radioContainer.addView(radioGroup)
                numberViewContainer.addView(radioContainer)

            }
        }

        // save button

    }

    @SuppressLint("RestrictedApi")
    private fun createRadioBtn(
        component: FormComponentItem,
        radioGroup: RadioGroup
    ) {

        if (radioBtnV == false){
            radioBtnV = true
        }

        val radioButton = AppCompatRadioButton(this)
        radioButton.text = component.fieldName
        radioButton.supportButtonTintList = Utils.getCustomColorStateList(this)
        radioGroup.addView(radioButton)

    }

    private fun createDropDown(component: FormComponentItem, numberViewContainer: LinearLayout) {
        //var selectedIndex = 0

        //Second - Checkbox group container
        val spinnerContainer = LinearLayout(this)
        spinnerContainer.orientation = LinearLayout.VERTICAL
        val spinnerLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        spinnerLayoutParams.weight = 1.5f
        spinnerContainer.setPadding(5,5,5,5)
        spinnerContainer.setBackgroundResource(R.drawable.black_box)
        spinnerContainer.layoutParams = spinnerLayoutParams

        //createLabelForViews(component)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(5, 5, 5, 5)
        val spinner = Spinner(this)


        spinner.setBackgroundColor(Color.WHITE)
        spinner.setBackgroundResource(R.drawable.edit_text_background)
        spinner.layoutParams = layoutParams
        spinner.setPadding(10, 10, 10, 10)
        //Spinner data source
        val spinnerDatasource = arrayOf("Closed permanently this month", "Open for some days this month", "Open permanently this month")

            val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                applicationContext,
                R.layout.form_spinner_row, spinnerDatasource
            )
            spinner.adapter = spinnerAdapter
            //spinner.setSelection(selectedIndex)

        spinnerContainer.addView(spinner)
        spinnerContainer.gravity = Gravity.CENTER
        numberViewContainer.addView(spinnerContainer)

    }

    @SuppressLint("RestrictedApi")
    private fun createCheckBoxGroup(component: FormComponentItem, secondContainer: LinearLayout) {
        if (checkBoxV == false){
            checkBoxV = true
        }

        component.fieldName?.let {
                val valueModel = it.toString()
                val checkBox = AppCompatCheckBox(this)
                checkBox.text = valueModel
                val checkBoxLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                checkBox.layoutParams = checkBoxLayoutParams
                /*valueModel.selected?.let { selected ->
                    if (selected) {
                        checkBox.isChecked = true
                    }
                }*/
                checkBox.supportBackgroundTintList = getCustomColorStateList(this)
                secondContainer.addView(checkBox)

        }

        secondContainer.gravity = Gravity.CENTER
    }

    private fun setMerginTextArea(view: View, topMergin: Int, width: Int, height: Int) {
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.setMargins(5, topMergin, 5, 0)
        view.layoutParams = layoutParams
    }
    private fun createEditableTextArea(component: FormComponentItem, secondContainer: LinearLayout) {
        //isLabelNull(component)
        if (editTextAreaV == false){
            editTextAreaV = true
        }
        val editText = EditText(this)
        var rows = 1

        setMerginTextArea(
            editText, 0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            150
        )
        //editText.gravity = Gravity.NO_GRAVITY

        editText.setPadding(5, 5, 5, 5)
        editText.setBackgroundResource(R.drawable.edit_text_background)
        editText.isSingleLine = false

        secondContainer.addView(editText)

    }
    private fun createEditableTextView(component: FormComponentItem, secondContainer: LinearLayout) {
        //isLabelNull(component)
        if (editableTextV == false){
            editableTextV = true
        }
        val editText = EditText(this)
        var rows = 1

        setMerginTextArea(
            editText, 0,
            300,
            50
        )
        //editText.gravity = Gravity.CENTER

        editText.setPadding(5, 5, 5, 5)
        editText.setBackgroundResource(R.drawable.edit_text_background)
        //editText.isSingleLine = false

        secondContainer.addView(editText)

    }


}