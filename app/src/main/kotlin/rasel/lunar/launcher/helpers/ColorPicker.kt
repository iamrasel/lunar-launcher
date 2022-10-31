/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.helpers

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText


internal class ColorPicker(private val initialColor: String,
    private val editText: TextInputEditText, private val sliderA: Slider, private val sliderR: Slider,
    private val sliderG: Slider, private val sliderB: Slider, private val colorPreview: LinearLayoutCompat) {

    @SuppressLint("SetTextI18n")
    fun pickColor() {
        editText.setText(initialColor)
        stringToSlider(initialColor)
        colorPreview.setBackgroundColor(Color.parseColor("#$initialColor"))

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 6){
                    sliderA.value = 255F
                    sliderR.value = Integer.parseInt(s.substring(0..1), 16).toFloat()
                    sliderG.value = Integer.parseInt(s.substring(2..3), 16).toFloat()
                    sliderB.value = Integer.parseInt(s.substring(4..5), 16).toFloat()
                } else if (s.length == 8){
                    stringToSlider(s.toString())
                } else if (s.isEmpty()) {
                    sliderA.value = 0F
                    sliderR.value = 0F
                    sliderG.value = 0F
                    sliderB.value = 0F
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        sliderA.addOnChangeListener(Slider.OnChangeListener { _: Slider?, _: Float, _: Boolean ->
            editText.setText(colorString.uppercase())
            colorPreview.setBackgroundColor(Color.parseColor("#$colorString"))
        })

        sliderR.addOnChangeListener(Slider.OnChangeListener { _: Slider?, _: Float, _: Boolean ->
            editText.setText(colorString.uppercase())
            colorPreview.setBackgroundColor(Color.parseColor("#$colorString"))
        })

        sliderG.addOnChangeListener(Slider.OnChangeListener { _: Slider?, _: Float, _: Boolean ->
            editText.setText(colorString.uppercase())
            colorPreview.setBackgroundColor(Color.parseColor("#$colorString"))
        })

        sliderB.addOnChangeListener(Slider.OnChangeListener { _: Slider?, _: Float, _: Boolean ->
            editText.setText(colorString.uppercase())
            colorPreview.setBackgroundColor(Color.parseColor("#$colorString"))
        })
    }

    private fun stringToSlider(s: String) {
        sliderA.value = Integer.parseInt(s.substring(0..1), 16).toFloat()
        sliderR.value = Integer.parseInt(s.substring(2..3), 16).toFloat()
        sliderG.value = Integer.parseInt(s.substring(4..5), 16).toFloat()
        sliderB.value = Integer.parseInt(s.substring(6..7), 16).toFloat()
    }

    private val colorString: String get() {
        var a = Integer.toHexString((((255*sliderA.value)/sliderA.valueTo).toInt()))
        if(a.length==1) a = "0$a"
        var r = Integer.toHexString((((255*sliderR.value)/sliderR.valueTo).toInt()))
        if(r.length==1) r = "0$r"
        var g = Integer.toHexString((((255*sliderG.value)/sliderG.valueTo).toInt()))
        if(g.length==1) g = "0$g"
        var b = Integer.toHexString((((255*sliderB.value)/sliderB.valueTo).toInt()))
        if(b.length==1) b = "0$b"
        return "$a$r$g$b"
    }

}
