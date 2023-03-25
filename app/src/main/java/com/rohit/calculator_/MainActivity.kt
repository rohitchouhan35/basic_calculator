package com.rohit.calculator_

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null
    private var isDigitPresent: Boolean = false
    private var isDigitLast: Boolean = false
    private var isOperandPresent: Boolean = false
    private var isOperandLast: Boolean = false
    private var isDecimalPresent: Boolean = false
    private var equalPressed: Boolean = false
    private var answer: String = ""
    private var operandCount: Int = 0
    private var isDecimalInAnswer: Boolean = false
    private var digitCount: Int = 0
    private var containsE: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        equalAnimation()

        tvInput = findViewById(R.id.textView)
    }

    fun onEqual() {
        if(!isOperandPresent){
            return
        }
        if(!isOperandLast && tvInput?.text != null){
            equalPressed = true
            answer = calculate(tvInput?.text.toString())
            isDecimalInAnswer = answer.contains('.')
            tvInput?.text = answer
            tvInput?.setTextColor(Color.parseColor("#39b34b"))
            if(answer.length >= 14) tvInput?.textSize = 31f
            else tvInput?.textSize = 45f
        }

        isOperandPresent = false
        operandCount = 0
        digitCount = 0
    }

    fun onDigit(view: View) {
        if(digitCount >= 15) {
            Toast.makeText(this, "Can't enter more than 15 digits", Toast.LENGTH_SHORT).show()
            return
        }
        isDigitLast = true
        tvInput?.textSize = 35f
        if(equalPressed) {
            tvInput?.text = ""
            equalPressed = false
        }
        tvInput?.setTextColor(Color.parseColor("#ffffff"))
        if(tvInput?.text == null) {
            isDecimalPresent = false
        }
        isDigitPresent = true
        isOperandLast = false
        tvInput?.append((view as Button).text)
        tvInput?.setTextColor(Color.parseColor("#000000"))

        digitCount++
    }

    fun onOperand(view: View) {

        if(operandCount > 0 && isDigitLast) return
        tvInput?.textSize = 35f
        equalPressed = false
        isDecimalPresent = false

        if(!isDigitPresent) {
            Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show()
        } else {
            if(isOperandLast) {
                val lenText: Int? = tvInput?.text?.length
                if (lenText != null) {
                    tvInput?.text = tvInput?.text?.substring(0, lenText-3)
                    operandCount--
                }
            }

            tvInput?.setTextColor(Color.parseColor("#6807f0"))
            val bText: CharSequence? = (view as Button).text
//            tvInput?.append(" $bText ")
            if(bText.toString() == "X"){
                tvInput?.append(" x ")
            }
            if(bText.toString() == "+"){
                tvInput?.append(" + ")
            }
            if(bText.toString() == "-"){
                tvInput?.append(" - ")
            }
            if(bText.toString() == "/"){
                tvInput?.append(" / ")
            }
            if(bText.toString() == "%"){
                tvInput?.append(" % ")
            }
            operandCount++
        }

        isOperandPresent = true
        isOperandLast = true
        isDigitLast = false
        digitCount = 0
    }

    fun onDecimal() {
        if(!isDecimalPresent && !isOperandLast && isDigitPresent) {
            tvInput?.append(".")
            isDecimalPresent = true
            isOperandLast = false
        }
    }

    fun onClear() {
        tvInput?.textSize = 35f
        tvInput?.text = ""
        isDigitPresent = false
        isDecimalPresent = false
        isOperandLast = false
        isOperandPresent = false
        isDigitLast = false
        operandCount = 0
        digitCount = 0
    }

    fun onCut() {
        if(tvInput?.text == null || tvInput?.text.toString() == ""){
            return
        }
//        Toast.makeText(this, tvInput?.text.toString(), Toast.LENGTH_SHORT).show()
        val str: String = tvInput?.text.toString()
        val len: Int = str.length

        if(len >= 4 && str[len-1] == ' '){
            val temp: String = str.substring(0, len-3)
            tvInput?.text = temp
            isOperandLast = false
            isOperandPresent = false
            isDigitLast = true
            operandCount = 0
        }else {
            val temp: String = str.substring(0, len-1)
            tvInput?.text = temp
        }

        if(tvInput?.text.toString() == "") tvInput?.text = null
    }

    private fun calculate(str: String): String {
        val result = solve(str)
        var ans: String
        ans = if (result - result.toInt() == 0.0) {
            result.toInt().toString() + ""
        } else result.toString() + ""
        var count = 0
        val length = ans.length
        for (j in 0 until length) {
            if (ans[j] == '.') {
                count = length - j - 1
                break
            }
        }
        containsE = false
        val temp2 = result.toString() + ""
        val lastE: String
        val sbLastE = StringBuilder("")
        for (j in temp2.length - 1 downTo 0) {
            if (temp2[j] == 'E') {
                containsE = true
                break
            }
            sbLastE.append(temp2[j])
        }
        lastE = sbLastE.reverse().toString()
        println(lastE)
        if (containsE) {
            val lastPower = stringToInt(lastE)
            if (lastPower <= 14) {
                val finalTemp = result.toString() + ""
                val sbFinalTemp = StringBuilder("")
                for (j in finalTemp.indices) {
                    if (finalTemp[j] == '.') continue
                    if (finalTemp[j] == 'E') break
                    sbFinalTemp.append(finalTemp[j])
                }
                while (sbFinalTemp.length != lastPower + 1) sbFinalTemp.append("0")
                ans = sbFinalTemp.toString()
            }
        } else {
            var temp1 = 0
            var isDecimal = false
            val tempSb = StringBuilder("")
            if (count > 6) {
                for (j in ans.indices) {
                    if (temp1 > 6) break
                    if (ans[j] == '.') isDecimal = true
                    if (isDecimal) temp1++
                    tempSb.append(ans[j])
                }
                ans = tempSb.toString()
            }
        }
        return ans
    }

    private fun solve(str: String): Double {
        val num1Str = StringBuilder("")
        val len = str.length
        var i = 0
        while (str[i] in '0'..'9' || str[i] == '.') {
            num1Str.append(str[i])
            i++
        }
        i++
        val operand: Char = str[i++]
        val num2Str = StringBuilder("")
        while (i < len) {
            num2Str.append(str[i])
            i++
        }
        val num1 = num1Str.toString().toDouble()
        val num2 = num2Str.toString().toDouble()
        var result = 0.0
        if (operand == '+') {
            result = num1 + num2
        }
        when (operand) {
            '+' -> result = num1 + num2
            '-' -> result = num1 - num2
            'x' -> result = num1 * num2
            '/' -> result = num1 / num2
            '%' -> result = num1 % num2
            else -> {}
        }
        return result
    }

    private fun stringToInt(s: String): Int {
        return try {
            Integer.valueOf(s)
        } catch (ex: NumberFormatException) {
            -1
        }
    }

    fun onCut(view: View) {}
    fun onClear(view: View) {}


//    private fun equalAnimation() {
//        val button: Button = findViewById(R.id.equalsTo)
//        val shape = GradientDrawable()
//        shape.shape = GradientDrawable.RECTANGLE
//        shape.setColor(Color.parseColor("#39b34b"))
//        shape.cornerRadius = 1000f
//        button.background = shape
//    }

//    <TableLayout
//    android:layout_height="55dp"
//    android:layout_width="fill_parent"
//    android:layout_marginTop="10dp"
//    android:stretchColumns="*">
//
//    <TableRow
//    android:layout_height="fill_parent"
//    android:layout_width="fill_parent"
//    android:gravity="fill_vertical"
//    android:layout_weight="0.5">
//
//
//    <ImageView
//    android:id="@+id/cal_history"
//    android:layout_width="34dp"
//    android:layout_height="55dp"
//    android:layout_gravity="start|bottom"
//    android:layout_marginRight="21dp"
//    android:onClick="onCut"
//    android:padding="14dp"
//    android:src="@mipmap/history_cal">
//
//    </ImageView>
//
//    <ImageView
//    android:layout_width="81dp"
//    android:layout_height="55dp"
//    android:layout_gravity="end|bottom"
//    android:layout_marginLeft="8dp"
//    android:onClick="onCut"
//    android:padding="14dp"
//    android:src="@mipmap/img">
//
//    </ImageView>
//    </TableRow>
//    </TableLayout>
}