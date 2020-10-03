package com.luisppinheiroj.whatappslike.ui.new_chat

import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp

interface NewChatMvp{
    interface View : BaseMvp.View,UsersAdapter.UsersAdapterListener{

    }
    interface Presenter<V : View> : BaseMvp.Presenter<V>{

    }
}