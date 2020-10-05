package com.luisppinheiroj.whatappslike.ui.new_chat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.UserObject
import com.luisppinheiroj.whatappslike.helper.AppHelper
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.GROUPS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_PATH
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_new_chat.*
import kotlinx.android.synthetic.main.view_network_error.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class CreateChatActivity : BaseActivity(),UsersAdapter.UsersAdapterListener{




    @BindView(R.id.new_chat_toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.users_refresh_layout)
    lateinit var  usersRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.users_recycler_view)
    lateinit var usersListView: RecyclerView

    @BindView(R.id.empty_list_layout)
    lateinit var emptyListView: LinearLayout

    @BindView(R.id.users_search_field)
    lateinit var searchUsersField: EditText

    @BindView(R.id.enable_group_chat_btn)
    lateinit var groupChatBtn: Button

    @BindView(R.id.create_group_chat_fab)
    lateinit var newGroupChatBtn: FloatingActionButton


    lateinit var usersAdapter: UsersAdapter

    private var groupChatEnabled: Boolean = false

    private var usersList: ArrayList<UserObject> = ArrayList()

    private lateinit var database: DatabaseReference
    private lateinit var currentUserUID: String
    private lateinit var currentAppUser : UserObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        ButterKnife.bind(this)

        // initial Data
        database = Firebase.database.reference
        currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid


        // UI
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        // toolbar
        toolbar.title = "Find Users"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupUsersList()
        getUsersData()

        usersRefreshLayout.setOnRefreshListener {
            getUsersData()
            showMessage("Refreshing the list..")
            usersRefreshLayout.isRefreshing = false
        }


        // search For Users
        searchUsersField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // filter data
                if (s.toString().isEmpty()) {
                    usersAdapter.filteredUsers.clear()
                    usersAdapter.filteredUsers.addAll(usersList)
                    usersAdapter.notifyDataSetChanged()
                }else{
                    usersAdapter.searchText = s.toString()
                    usersAdapter.filter.filter(s)
                }


            }

        })

        enable_group_chat_btn.setOnClickListener {
            if (usersAdapter.groupChatEnabled) {
                usersAdapter.groupChatEnabled = false
                usersAdapter.notifyDataSetChanged()
                newGroupChatBtn.hide()
                enable_group_chat_btn.text = getString(R.string.enable_group_chatting)
                showMessage(getString(R.string.disable_group_chatting_msg))
            } else {
                usersAdapter.groupChatEnabled = true
                usersAdapter.notifyDataSetChanged()
                newGroupChatBtn.show()
                enable_group_chat_btn.text = getString(R.string.disable_group_chatting)

                showMessage(getString(R.string.group_chat_enabled_msg))

            }

        }

        newGroupChatBtn.setOnClickListener {
            if (usersAdapter.groupChatEnabled) {
                // show name dialog
                showGroupNameDialog()

            }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUserClicked(user: UserObject) {
        if (!usersAdapter.groupChatEnabled) {
            createOneUserChat(user)
            showMessage("New Chat Has Been Created")
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }




    private fun setupUsersList() {
        usersListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
            false)
        usersListView.itemAnimator = DefaultItemAnimator()
        usersListView.setHasFixedSize(true)
        // dummy data

        usersAdapter = UsersAdapter(this, usersList, this)
        usersListView.adapter = usersAdapter
    }

    private fun getUsersData() {
        if (AppHelper.isNetworkStatusAvialable(this)) {
            getUsersDetails()
        } else {
            showNetworkErrorView()
        }

    }

    private fun getUsersDetails() {
        searchUsersField.text.clear()
        usersAdapter.users.clear()

        usersList.clear()
        //usersAdapter.notifyDataSetChanged()

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
                        val mUser = UserObject(childSnapshot.key!!, email, name)
                        if (childSnapshot.key!! != currentUserUID) {
                            usersList.add(mUser)
                        }else{
                            currentAppUser = mUser
                        }
                    }
                    usersAdapter.notifyDataSetChanged()
                    checkEmptyList()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showMessage(databaseError.message)
                Log.e("NewChatActivity", databaseError.message)
                checkEmptyList()
            }

        })
    }


    private fun createOneUserChat(user: UserObject) {

        // TODO : Check if the chat is already created

        val validChat = true
        val chatKey = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH).push().key

        val chatInfoDb = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH)
            .child(chatKey!!)
            .child("info")


        val usersDb = FirebaseDatabase.getInstance().reference.child(USERS_PATH)
        val newChatMap: HashMap<String, Any> = HashMap()


        newChatMap["id"] = chatKey
        newChatMap["timestamp"] = Date().time
        newChatMap["users/" + FirebaseAuth.getInstance().uid] = true

        newChatMap["title"] = currentAppUser.name + ","+user.name

        newChatMap["users/" + user.uid] = true
        usersDb.child(user.uid).child(USERS_CHATS_PATH).child(chatKey).setValue(true)

        if (validChat) {
            val chatMap: Map<String, Any> = newChatMap
            chatInfoDb.updateChildren(chatMap)
            usersDb.child(FirebaseAuth.getInstance().uid!!)
                .child(USERS_CHATS_PATH)
                .child(chatKey)
                .setValue(true)
        }else{
            onError("newOneToOneChat","the Chat is not valid")
        }
    }

    private fun createGroupChat(groupTitle: String) {

        val chatKey = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH).push().key

        val chatInfoDb = FirebaseDatabase.getInstance()
            .reference.child(CHATS_PATH)
            .child(chatKey!!)
            .child("info")

        val groupsDbRef = FirebaseDatabase.getInstance()
            .reference.child(GROUPS_PATH)


        val usersDb = FirebaseDatabase.getInstance().reference.child(USERS_PATH)
        val newGroupChatDataMap: HashMap<String, Any> = HashMap()

        newGroupChatDataMap["id"] = chatKey
        newGroupChatDataMap["title"] = groupTitle
        newGroupChatDataMap["users/" + FirebaseAuth.getInstance().uid] = true

        var validChat = false
        var selectedUsers = 0

        for (mUser in usersAdapter.getUsers()) {
            if (mUser.isSelected) {
                selectedUsers++
                if (selectedUsers > 1) validChat = true
                // add participants id to the current chat
                newGroupChatDataMap["users/" + mUser.uid] = true

                usersDb.child(mUser.uid)
                    .child(USERS_CHATS_PATH)
                    .child(chatKey).setValue(true)
            }
        }

        if (validChat) {
            chatInfoDb.updateChildren(newGroupChatDataMap)
            //groupsDbRef.updateChildren(newGroupChatDataMap)
            // add chat id to the participant users
            usersDb.child(FirebaseAuth.getInstance().uid!!)
                .child(USERS_CHATS_PATH)
                .child(chatKey)
                .setValue(true)
            // add to the groups path
            groupsDbRef.child(chatKey).setValue(true)

            showMessage("New Group Chat Has Been Created")
            onBackPressed()
        } else {
            onError("NewGroupChat", "You need to select more Than one user")
        }


    }

    private fun showGroupNameDialog() {
        var dialog: MaterialDialog = MaterialDialog(this)

            .show {
                input(hintRes = R.string.group_chat_hint) { dialog, text ->
                    if (text.length > 5) {
                        createGroupChat(text.toString())
                    } else {
                        onError("newGroupChat", "The title is too short")
                    }
                }
                title(R.string.group_name)
                positiveButton(R.string.submit)
                negativeButton(R.string.cancel)
            }

    }


    private fun checkEmptyList() {
        if (usersAdapter.itemCount > 0) {
            usersListView.visibility = View.VISIBLE
            emptyListView.visibility = View.GONE
        } else {
            usersListView.visibility = View.GONE
            emptyListView.visibility = View.VISIBLE
        }
    }

    private fun showNetworkErrorView() {
        network_error_layout.visibility = View.VISIBLE
        no_internet_reload_btn.setOnClickListener {
            getUsersData()
            network_error_layout.visibility = View.GONE
        }
    }


}