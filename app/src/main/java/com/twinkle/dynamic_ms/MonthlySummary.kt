package com.twinkle.dynamic_ms

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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    var countingNumM: Int = 0
    var countingNumF: Int = 0

    var tallyFormArrayList: java.util.ArrayList<DynamicFormJson>? = null
    var txtJson: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    WidgetItems.TABLETITLE.label -> createTableTitle(component)
                    WidgetItems.FEMALE.label -> createFemale(component)
                    WidgetItems.MALE.label -> createMale(component)
                    WidgetItems.NUMBERONE.label -> createNumberOne(component)
                }
            }
        }

        // save button


    }


    private fun isLabelNull(viewComponentModel: FormComponentItem) {
        if (viewComponentModel.label != null) createLabelForViews(viewComponentModel)
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

    private fun createTableTitle(component: FormComponentItem) {
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
        component.male.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString.toString())
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
        layoutParams2.weight = 0.5f

        component.female.let { labelString ->
            val textView = TextView(this)
            textView.textSize = 15f
            textView.gravity = Gravity.CENTER
            textView.setTextColor(Color.BLACK)
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.setPadding(5, 5, 5, 5)
            textView.text = createStringForViewLabel(false, labelString.toString())
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
                        //headingName.setText(it.tallysheet_name)


                        val text1 = it.json_op

                        populateForm(text1)
                        /*val bgcolor = Color.parseColor(it.color_code)*/
                        /*binding.mainLayout.setBackgroundColor(bgcolor)
                        binding.toolbar.setBackgroundColor(bgcolor)*/
                        /*binding.mainLayout.setBackgroundColor(resources.getColor(R.color.light_green))
                        binding.toolbar.setBackgroundResource(R.drawable.green_title_bg)*/
                        /*binding.constraintLayout2.visibility = View.VISIBLE*/

                        /*countingNumM = 0
                        countingNumF = 0
                        newCasesTvM.setText(countingNumM.toString())
                        newCasesTvF.setText(countingNumF.toString())*/
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



}