package com.luisppinheiroj.whatappslike.ui.chats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.ChatObject
import com.luisppinheiroj.whatappslike.data.model.UserObject
import com.luisppinheiroj.whatappslike.helper.AppConstants
import com.luisppinheiroj.whatappslike.helper.AppHelper
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_INFO_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_PATH
import com.luisppinheiroj.whatappslike.ui.base.BaseFragment
import com.luisppinheiroj.whatappslike.ui.chat_room.ChatRoomActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.view_network_error.*
import java.util.*
import kotlin.collections.ArrayList


class ChatsFragment: BaseFragment(),ChatsMvp.View{



    @BindView(R.id.chats_recycler_view)
    lateinit var chatsRecyclerView : RecyclerView

    @BindView(R.id.empty_list_layout)
    lateinit var empty_list_view :LinearLayout

    @BindView(R.id.network_error_layout)
    lateinit var networkErrorLayout :LinearLayout


    @BindView(R.id.chats_progressbar)
    lateinit var chatsProgressBar : ProgressBar

    lateinit var chatsAdapter : ChatsAdapter

    private lateinit var mPresenter :ChatsPresenter<ChatsMvp.View>

    private lateinit var currentUserUID :String
    private var chatsList : ArrayList<ChatObject> = ArrayList()


    companion object{
        fun newInstance(): ChatsFragment{
            val args = Bundle()

            val fragment = ChatsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_chats,container,false)
        ButterKnife.bind(this,view)
        mPresenter = ChatsPresenter()
        mPresenter.onAttach(this)
        currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // show only the progress bar
        chatsProgressBar.visibility = View.VISIBLE
        networkErrorLayout.visibility = View.GONE
        chatsRecyclerView.visibility = View.GONE
        empty_list_view.visibility = View.GONE

        chatsRecyclerView.layoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL,false)
        chatsRecyclerView.itemAnimator = DefaultItemAnimator()
        chatsAdapter = ChatsAdapter(context!!,chatsList,this)
        chatsRecyclerView.adapter = chatsAdapter
        setupChats()

    }



    override fun onStart() {
        super.onStart()

        setupChats()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        // remove All Listeners
        super.onDestroy()
    }


    private fun setupChats(){
        if (AppHelper.isNetworkStatusAvialable(context!!)){
            getUserChatList()
        }else{
            showNetworkErrorView()
        }

    }




    private fun getUserChatList() {
        val mUserChatDB = FirebaseDatabase.getInstance().reference
            .child(USERS_PATH)
            .child(currentUserUID)
            .child(USERS_CHATS_PATH)

        mUserChatDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for ((i, childSnapshot) in dataSnapshot.children.withIndex()) {

                        val retrievedChat = ChatObject(childSnapshot.key!!, "last message " + i,
                            Date().time, false, "Testing "+ i,null,
                            false)

                        // If The Chat Is Already Existed
                        var exists = false
                        for (chat in chatsList) {
                            if (chat.chatId.equals(retrievedChat.chatId))
                                exists = true
                        }
                        if (exists) continue


                        retrievedChat.snippet ="My Snippet"

                        chatsList.add(retrievedChat)
                        getChatData(retrievedChat)


                    }
                    checkEmptyView()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity?.let { Toasty.error(it,databaseError.message).show() }
                checkEmptyView()
            }
        })
    }

    private fun getChatData(currentChat: ChatObject) {
        val mChatInfoDB = FirebaseDatabase.getInstance().reference
            .child(CHATS_PATH)
            .child(currentChat.chatId!!)
            .child(CHATS_INFO_PATH)

        mChatInfoDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var lastMessage = ""
                    var timeStamp: Long = Date().time

                    val retrievedChatId = dataSnapshot.child("id").value.toString()

                    if (dataSnapshot.child("lastMessage").value != null)
                        lastMessage = dataSnapshot.child("lastMessage").value.toString()

                    if (dataSnapshot.child("timestamp").value != null)
                        timeStamp = dataSnapshot.child("timestamp").value.toString().toLong()

                    /*** Testing  ***/

                    currentChat.snippet = lastMessage
                    currentChat.date = timeStamp

                    // Problem Here : Still Confusing
                    // Search For All The Participants In The Chat
                    for (participantSnapshot in dataSnapshot.child(USERS_PATH).children) {
                        var participantUid = ""
                        if (participantSnapshot.key != null){
                            participantUid = participantSnapshot.key!!
                            getParticipantInfo(participantUid, currentChat)


                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError("GetChatsData", databaseError.message)
                checkEmptyView()
            }
        })
    }

    private fun getParticipantInfo(userUid : String,chat :ChatObject){
        val userInfoRef = FirebaseDatabase.getInstance().reference
            .child(USERS_PATH).child(userUid)
        userInfoRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(userInfosnapshot: DataSnapshot) {
                if (userInfosnapshot.exists()){

                    val name : String = userInfosnapshot.child("name").value.toString()
                    val email : String = userInfosnapshot.child("email").value.toString()
                    val participant = UserObject(userUid,email,name)

                    if (!chat.participantUsers.contains(participant)){
                        chat.participantUsers.add(participant)
                    }

                    // Set Title For The Chat
                    if (chat.participantUsers.size>2 && !chat.title.contains(" Group")){
                        chat.title += " Group"
                    }
                    else{
                        if (participant.name != FirebaseAuth.getInstance().currentUser!!.displayName){
                            chat.title = participant.name + " Chat"
                        }
                    }
                    chatsAdapter.notifyDataSetChanged()
                    checkEmptyView()

                }
            }
            override fun onCancelled(error: DatabaseError) {
                onError("GetParticipantInfo", error.message)
                checkEmptyView()
            }


        })
    }



    private fun  checkEmptyView(){
        networkErrorLayout.visibility = View.GONE
        chatsProgressBar.visibility = View.GONE
        if (chatsAdapter.itemCount<1){
            chatsRecyclerView.visibility = View.GONE
            empty_list_view.visibility = View.VISIBLE
        }else{
            chatsRecyclerView.visibility = View.VISIBLE
            empty_list_view.visibility = View.GONE
        }

    }

    private fun showNetworkErrorView() {
        chatsProgressBar.visibility = View.GONE
        networkErrorLayout.visibility = View.VISIBLE

        no_internet_reload_btn.setOnClickListener{
            chatsProgressBar.visibility = View.VISIBLE
            networkErrorLayout.visibility = View.GONE
            setupChats()
        }

    }


    override fun onChatClicked(chat: ChatObject) {
        var intent : Intent = Intent(activity,ChatRoomActivity::class.java)
        intent.putExtra(AppConstants.CHATS_OBJECT,chat)
        startActivity(intent)
    }


    private fun removeAllDbListeners(){

    }

}