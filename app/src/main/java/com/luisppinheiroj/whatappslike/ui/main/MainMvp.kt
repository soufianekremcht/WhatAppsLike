package com.luisppinheiroj.whatappslike.ui.main

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.luisppinheiroj.whatappslike.ui.base.mvp.BaseMvp

interface MainMvp {

    interface View : BaseMvp.View,BottomNavigationView.OnNavigationItemSelectedListener{

    }
    interface Presenter<V : View> : BaseMvp.Presenter<V>{

    }
}