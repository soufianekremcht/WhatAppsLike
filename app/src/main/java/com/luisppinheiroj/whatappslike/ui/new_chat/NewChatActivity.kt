package com.luisppinheiroj.whatappslike.ui.new_chat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.UserObject
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_PATH
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_new_chat.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class NewChatActivity : BaseActivity(),NewChatMvp.View{


    private lateinit var mPresenter: NewChatPresenter<NewChatMvp.View>

    @BindView(R.id.new_chat_toolbar)
    lateinit var toolbar : Toolbar

    @BindView(R.id.users_recycler_view)
    lateinit var usersListView : RecyclerView

    @BindView(R.id.users_search_field)
    lateinit var searchUsersField : EditText

    @BindView(R.id.enable_group_chat_btn)
    lateinit var groupChatBtn : Button

    @BindView(R.id.create_group_chat_fab)
    lateinit var newGroupChatBtn : FloatingActionButton


    lateinit var usersAdapter: UsersAdapter

    private var groupChatEnabled :Boolean = false

    private var usersList : ArrayList<UserObject>  = ArrayList()

    private lateinit var database: DatabaseReference
    private lateinit var currentUserUID :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        ButterKnife.bind(this)
        mPresenter = NewChatPresenter();
        mPresenter.onAttach(this)

        // initial Data
        database = Firebase.database.reference
        currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid


        // UI
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }
        toolbar.title = "Find Users"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupUsersList()

        // search For Users
        searchUsersField.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        enable_group_chat_btn.setOnClickListener {
            if (usersAdapter.groupChatEnabled){
                usersAdapter.groupChatEnabled = false
                usersAdapter.notifyDataSetChanged()
                newGroupChatBtn.hide()
                enable_group_chat_btn.text = "Enable Group Chatting"
                showMessage("You Disabled Group Chatting")
            }else{
                usersAdapter.groupChatEnabled = true
                usersAdapter.notifyDataSetChanged()
                newGroupChatBtn.show()
                enable_group_chat_btn.text = "Disable Group Chatting"
                showMessage("You Enabled Group Chatting")

            }

        }

        newGroupChatBtn.setOnClickListener {
            if (usersAdapter.groupChatEnabled){
                createGroupChat()
                showMessage("New Group Chat Has Been Created")
                onBackPressed()

            }
        }





    }

    private fun setupUsersList(){
        usersListView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        usersListView.itemAnimator = DefaultItemAnimator()
        usersListView.setHasFixedSize(true)
        // dummy data
        usersAdapter = UsersAdapter(this, ArrayList(),this)
        usersListView.adapter = usersAdapter
        getUsersDetails()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_chat,menu)
        // implement Search
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUserClicked(user : UserObject) {
        if (!usersAdapter.groupChatEnabled){
            createOneUserChat(user)
            showMessage("New Chat Has Been Created")
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getUsersDetails() {
        val mUserDB = FirebaseDatabase.getInstance().reference.child(USERS_PATH)

        val query: Query = mUserDB.orderByChild("name")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var email = ""
                    var name = ""
                    for (childSnapshot in dataSnapshot.children) {
                        email = childSnapshot.child("email").value.toString()
                        name = childSnapshot.child("name").value.toString()
                        val mUser = UserObject(childSnapshot.key!!, email,name)
                        if (childSnapshot.key!! != currentUserUID){
                            usersList.add(mUser)
                            usersAdapter.add(mUser)
                        }
                    }
                    usersAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showMessage(databaseError.message)
                Log.e("NewChatActivity",databaseError.message)
            }

        })
    }


    private fun createOneUserChat(user : UserObject){
        if (user.uid.length>4){
            val chatKey = FirebaseDatabase.getInstance()
                .reference.child(CHATS_PATH).push().key

            val chatInfoDb = FirebaseDatabase.getInstance()
                .reference.child(CHATS_PATH)
                .child(chatKey!!)
                .child("info")


            val usersDb = FirebaseDatabase.getInstance().reference.child(USERS_PATH)
            val newChatMap : HashMap<String, Any> = HashMap()
            newChatMap["id"] = chatKey
            newChatMap["users/" + FirebaseAuth.getInstance().uid] = true
            val validChat = true
            newChatMap["users/" + user.uid] = true
            usersDb.child(user.uid).child(USERS_CHATS_PATH).child(chatKey).setValue(true)

            if (validChat) {
                val chatMap : Map<String,Any> = newChatMap
                chatInfoDb.updateChildren(chatMap)
                usersDb.child(FirebaseAuth.getInstance().uid!!)
                    .child(USERS_CHATS_PATH)
                    .child(chatKey)
                    .setValue(true)
            }
        }else{

        }




    }
    private fun createGroupChat(){
        val usersList : ArrayList<UserObject> = ArrayList()
        val chatKey = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH).push().key

        val chatInfoDb = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH)
            .child(chatKey!!)
            .child("info")


        val usersDb = FirebaseDatabase.getInstance().reference.child(USERS_PATH)
        val newChatMap : HashMap<String, Any> = HashMap()
        newChatMap["id"] = chatKey
        newChatMap["users/" + FirebaseAuth.getInstance().uid] = true
        var validChat = false

        for (mUser in usersAdapter.getUsers()) {
            if (mUser.isSelected) {
                validChat = true
                newChatMap["users/" + mUser.uid] = true
                usersDb.child(mUser.uid).child(USERS_CHATS_PATH).child(chatKey).setValue(true)
            }
        }
        if (validChat) {
            val chatMap : Map<String,Any> = newChatMap
            chatInfoDb.updateChildren(chatMap)
            usersDb.child(FirebaseAuth.getInstance().uid!!)
                .child(USERS_CHATS_PATH)
                .child(chatKey)
                .setValue(true)
        }
    }




}