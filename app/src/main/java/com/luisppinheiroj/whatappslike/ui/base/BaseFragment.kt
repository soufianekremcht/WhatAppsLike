package com.luisppinheiroj.whatappslike.ui.base

import androidx.fragment.app.Fragment
import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp


open class BaseFragment : Fragment(),
    BaseMvp.View{

    private val baseActivity : BaseActivity? = activity as? BaseActivity


    override fun onError(tag: String, resId: Int) {
        onError(tag ,baseActivity!!.getString(resId))
    }

    override fun onError(tag: String, message: String?) {
        baseActivity!!.onError(tag,message)
    }


    override fun showMessage(message: String?) {
        baseActivity!!.showMessage(message)
    }

    override fun showMessage(resId: Int) {
        baseActivity!!.showMessage(resId)
    }


}