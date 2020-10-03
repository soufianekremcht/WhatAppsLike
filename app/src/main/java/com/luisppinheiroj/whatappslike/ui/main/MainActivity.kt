package com.luisppinheiroj.whatappslike.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.chats.ChatsFragment
import com.luisppinheiroj.whatappslike.ui.new_chat.NewChatActivity


class MainActivity : BaseActivity(),MainMvp.View {
    @BindView(R.id.main_toolbar)
    lateinit var mainToolbar : Toolbar

//    @BindView(R.id.navigation)
//    lateinit var bottomNav : BottomNavigationView

    @BindView(R.id.new_chat_fab)
    lateinit var newChatFab : FloatingActionButton

    private lateinit var mPresenter : MainPresenter<MainMvp.View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        mPresenter = MainPresenter()
        mPresenter.onAttach(this)

        setSupportActionBar(mainToolbar)
        //bottomNav.setOnNavigationItemSelectedListener(this)
        newChatFab.setOnClickListener{
            showNewChatActivity()
        }
        if (savedInstanceState == null)
            showChatsFragment()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.bottom_nav_chats -> showChatsFragment()
            R.id.bottom_nav_search -> showChatsFragment()
        }
        return true;
    }

    fun showNewChatActivity(){
        val intent : Intent = Intent(this,NewChatActivity::class.java)
        startActivity(intent)
    }

    fun showChatsFragment(){
        loadFragment(ChatsFragment.newInstance())
    }


    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }


}