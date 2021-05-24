package com.example.hellonotes.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.hellonotes.R

@SuppressLint("AppCompatCustomView")
class GradientTextView: TextView {
    var Color1 : Int=0
    var Color2 : Int=0

    constructor(context: Context?) : super(context)
    constructor(context: Context?,attributeSet: AttributeSet) : super(context,attributeSet)
    constructor(context: Context?,attributeSet: AttributeSet,defStyleAttr:Int) : super(context,attributeSet,defStyleAttr)

    fun SetColors(PrimaryColor:Int, SecondaryColor :Int){
        this.Color1=PrimaryColor
        this.Color2=SecondaryColor
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed){
            paint.shader=LinearGradient(0f,0f,width.toFloat(),height.toFloat(),

                   Color1,
                   Color2,

                    Shader.TileMode.CLAMP)

            }
        }
    }

