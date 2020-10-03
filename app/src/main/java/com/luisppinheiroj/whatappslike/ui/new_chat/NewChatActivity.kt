package com.luisppinheiroj.whatappslike.ui.new_chat

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView

import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.helper.DummyData
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.chat_room.ChatRoomActivity

class NewChatActivity : BaseActivity(),NewChatMvp.View{


    private lateinit var mPresenter: NewChatPresenter<NewChatMvp.View>

    @BindView(R.id.new_chat_toolbar)
    lateinit var toolbar : Toolbar

    @BindView(R.id.users_recycler_view)
    lateinit var usersListView : RecyclerView

    lateinit var usersAdapter: UsersAdapter


    private var groupChatEnabled :Boolean = false

    private var searchView : SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        ButterKnife.bind(this)
        mPresenter = NewChatPresenter();
        mPresenter.onAttach(this)

        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupUsersList()

    }

    private fun setupUsersList(){
        usersListView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        usersListView.itemAnimator = DefaultItemAnimator()
        usersListView.setHasFixedSize(true)
        // dummy data
        usersAdapter = UsersAdapter(this,DummyData.getDummyContacts(),this)
        usersListView.adapter = usersAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_chat,menu)
        // implement Search
        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.menu_new_chat_search).actionView as SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.maxWidth = Integer.MAX_VALUE;
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_chat_group ->
                return  true
                // ADD NEW Group Conversations
            R.id.menu_new_chat_settings ->
                return true
            else -> return false
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUserClicked() {
        if (!groupChatEnabled){
            val intent : Intent = Intent(this, ChatRoomActivity::class.java)
            startActivity(intent)
        }else{
            // add participant to the group

        }

    }

}