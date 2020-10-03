package com.luisppinheiroj.whatappslike.ui.base.mvp

import androidx.annotation.StringRes

interface BaseMvp {
    interface View{
        fun onError(tag:String,@StringRes resId: Int)

        fun onError(tag:String,message: String?)

        fun showMessage(message: String?)

        fun showMessage(@StringRes resId: Int)

    }
    interface Presenter<V : View> {
        fun getMvpView() :V?
        fun onAttach(mvpView: V?)
        fun onDetach()

    }
}