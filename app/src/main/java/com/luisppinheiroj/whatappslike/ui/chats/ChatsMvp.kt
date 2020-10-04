package com.luisppinheiroj.whatappslike.ui.chats

import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp

interface ChatsMvp{
    interface View : BaseMvp.View,ChatsAdapter.ChatsAdapterListener{

    }
    interface Presenter<V : View> : BaseMvp.Presenter<V>{

    }
}