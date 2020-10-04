package com.luisppinheiroj.whatappslike.ui.chat_room

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.ChatMessage
import com.luisppinheiroj.whatappslike.data.model.ChatObject
import com.luisppinheiroj.whatappslike.data.model.UserObject
import com.luisppinheiroj.whatappslike.helper.AppConstants
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_MESSAGES_PATH
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.CHATS_PATH
import com.luisppinheiroj.whatappslike.helper.InputHelper
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.main.MainActivity
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList


class ChatRoomActivity : BaseActivity(), ChatRoomMvp.View {


    @BindView(R.id.chatroom_toolbar)
    lateinit var chatRoomToolbar : Toolbar

    @BindView(R.id.chatroom_message_recycler_view)
    lateinit var messagesRecyclerView : FastScrollRecyclerView

    @BindView(R.id.chatroom_attach_file)
    lateinit var attachFileImg : ImageView

    @BindView(R.id.chatroom_message_field)
    lateinit var messageField: EditText

    @BindView(R.id.chatroom_send_msg_fab)
    lateinit var sendMessageFab : FloatingActionButton

    private lateinit var mPresenter : ChatRoomPresenter<ChatRoomMvp.View>

    lateinit var messagesAdapter : MessagesAdapter
    lateinit var linearLayoutManager :LinearLayoutManager

    private lateinit var currentChats : ChatObject

    private lateinit var database : FirebaseDatabase

    private var messagesList :ArrayList<ChatMessage> = ArrayList()

    private lateinit var currentUserUID : String

    private var lastMessage : String = ""
    private var lastMessageTimeStamp : Long= Date().time




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)
        ButterKnife.bind(this)
        mPresenter = ChatRoomPresenter()
        mPresenter.onAttach(this)

        database = FirebaseDatabase.getInstance()

        currentChats = intent.getParcelableExtra(AppConstants.CHATS_OBJECT) as ChatObject

        //currentChats = DummyData.getDummyChats()[0]

        currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid


        chatRoomToolbar.title = currentChats.title
        chatRoomToolbar.setNavigationOnClickListener{
            onBackPressed()
        }
        setSupportActionBar(chatRoomToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupMessages()

        messageField.onFocusChangeListener = View.OnFocusChangeListener {
                _, hasFocus ->
            run {
                if (hasFocus)
                    if (messagesAdapter.itemCount > 0)
                        linearLayoutManager.scrollToPosition(messagesAdapter.itemCount -1)
            }
        }

        sendMessageFab.setOnClickListener{
            sendRealMessage()
        }



    }

    override fun onBackPressed() {
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent : Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun setupMessages(){
        linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        messagesRecyclerView.layoutManager = linearLayoutManager
        messagesRecyclerView.setHasFixedSize(true)
        messagesRecyclerView.setHideScrollbar(true)
        messagesAdapter = MessagesAdapter(this, ArrayList())
        messagesRecyclerView.adapter = messagesAdapter
        messagesAdapter.group_chat = currentChats.participantUsers.size>3

        getChatMessages()
    }

    private fun getChatMessages() {
        val mChatMessagesDb = FirebaseDatabase.getInstance().reference
            .child(CHATS_PATH)
            .child(currentChats.chatId!!)
            .child(CHATS_MESSAGES_PATH)

        mChatMessagesDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, @Nullable s: String?) {
                if (dataSnapshot.exists()) {
                    var messageUid = dataSnapshot.key
                    var messageBody = dataSnapshot.child("text").value.toString()
                    var senderUID = dataSnapshot.child("sender").value.toString()
                    var msgTimestamp :Long? = dataSnapshot.child("timestamp").value.toString().toLongOrNull()
                    if (msgTimestamp == null) msgTimestamp = Date().time


                    val senderUser = UserObject(senderUID,"","")

                    val chatMessage = ChatMessage(messageUid, messageBody, msgTimestamp,
                        false,senderUser,ArrayList(),false)

                    if (senderUID != currentUserUID) chatMessage.isReceived = true
                    messagesList.add(chatMessage)
                    messagesAdapter.add(chatMessage)
                    lastMessage = chatMessage.body
                    linearLayoutManager.scrollToPosition(messagesAdapter.itemCount-1)
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, @Nullable s: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, @Nullable s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendRealMessage() {
        val mChatMessagesRef = FirebaseDatabase.getInstance().reference
            .child(CHATS_PATH)
            .child(currentChats.chatId!!)
            .child(CHATS_MESSAGES_PATH)

        val newMessageUid: String = mChatMessagesRef.push().key!!
        val newMessageRef: DatabaseReference = mChatMessagesRef.child(newMessageUid)

        val newMessageDataMap: MutableMap<String ,Any?> = HashMap()


        if (!InputHelper.isEmpty(messageField)){
            lastMessage = messageField.text.toString()
            lastMessageTimeStamp = Date().time

                newMessageDataMap["sender"] = FirebaseAuth.getInstance().currentUser!!.uid
            newMessageDataMap["text"] = messageField.text.toString()
            newMessageDataMap["timestamp"] = lastMessageTimeStamp
            newMessageRef.updateChildren(newMessageDataMap)



            messageField.text.clear()
            linearLayoutManager.scrollToPosition(messagesAdapter.itemCount-1)
        }else{
            Toasty.error(this,"The Message Is Empty").show()
        }

    }

    private fun getParticipantInfo(participantUid : String, message :ChatMessage){
        var userInfoRef = FirebaseDatabase.getInstance().reference
            .child(FirebaseConstants.USERS_PATH).child(participantUid)
        userInfoRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val username : String = snapshot.child("name").value.toString()
                    val userEmail : String = snapshot.child("name").value.toString()
                    val participant = UserObject(participantUid,userEmail,username)
                    message.sender = participant
                    messagesAdapter.add(message)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }


    private fun saveLastUpdatesInChats(){
        val chatInfoRef : DatabaseReference =FirebaseDatabase.getInstance().reference
            .child(CHATS_PATH)
            .child(currentChats.chatId!!)
            .child(FirebaseConstants.CHATS_INFO_PATH)
        chatInfoRef.child("lastMessage").setValue(lastMessage)
        chatInfoRef.child("timestamp").setValue(lastMessageTimeStamp)


    }





    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        saveLastUpdatesInChats()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}