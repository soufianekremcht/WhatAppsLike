package com.luisppinheiroj.whatappslike.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    fun onError(tag : String,resId: Int) {
        onError(tag,getString(resId))
    }

    fun onError(tag:String,message: String?) {
        Toasty.error(this,message!!).show()
        Log.e(tag,message)
    }

    fun showMessage(message: String?) {
        Toasty.info(this,message!!,Toasty.LENGTH_SHORT).show()
    }

    fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }


}