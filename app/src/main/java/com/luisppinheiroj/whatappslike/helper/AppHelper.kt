package com.luisppinheiroj.whatappslike.helper

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color





object AppHelper{
    fun getRandomMaterialColor(context : Context, typeColor: String): Int {
        var returnColor: Int = Color.GRAY
        val arrayId: Int = context.resources.getIdentifier(
            "mdcolor_$typeColor",
            "array",
            context.packageName
        )
        if (arrayId != 0) {
            val colors: TypedArray = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }
}