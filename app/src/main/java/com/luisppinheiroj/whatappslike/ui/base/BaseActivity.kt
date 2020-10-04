package com.luisppinheiroj.whatappslike.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp
import es.dmoral.toasty.Toasty

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), BaseMvp.View{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onError(tag : String,resId: Int) {
        onError(tag,getString(resId))
    }

    override fun onError(tag:String,message: String?) {
        Toasty.error(this,message!!).show()
        Log.e(tag,message)
    }

    override fun showMessage(message: String?) {
        Toasty.info(this,message!!,Toasty.LENGTH_SHORT).show()
    }

    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }


}