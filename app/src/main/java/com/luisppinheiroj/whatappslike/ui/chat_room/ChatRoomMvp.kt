package com.luisppinheiroj.whatappslike.ui.chat_room

import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp

interface ChatRoomMvp{
    interface View : BaseMvp.View{

    }
    interface Presenter<V:View> : BaseMvp.Presenter<V>{

    }
}