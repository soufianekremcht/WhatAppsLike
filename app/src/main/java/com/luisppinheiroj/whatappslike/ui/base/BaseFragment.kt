package com.luisppinheiroj.whatappslike.ui.base

import androidx.fragment.app.Fragment



open class BaseFragment : Fragment(){

    private val baseActivity : BaseActivity? = activity as? BaseActivity


    fun onError(tag: String, resId: Int) {
        onError(tag ,baseActivity?.getString(resId))
    }

    fun onError(tag: String, message: String?) {
        baseActivity?.onError(tag,message)
    }


    fun showMessage(message: String?) {
        baseActivity?.showMessage(message)
    }

    fun showMessage(resId: Int) {
        baseActivity?.showMessage(resId)
    }


}