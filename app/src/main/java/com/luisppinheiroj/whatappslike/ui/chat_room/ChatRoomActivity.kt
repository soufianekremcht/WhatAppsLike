package com.luisppinheiroj.whatappslike.ui.chat_room

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.ChatMessage
import com.luisppinheiroj.whatappslike.helper.DummyData
import com.luisppinheiroj.whatappslike.helper.InputHelper
import com.luisppinheiroj.whatappslike.helper.KeyboardUtils
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import java.util.*

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

    lateinit var messagesAdapter : ChatRoomAdapter
    lateinit var linearLayoutManager :LinearLayoutManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)
        ButterKnife.bind(this)
        mPresenter = ChatRoomPresenter()
        mPresenter.onAttach(this)

        chatRoomToolbar.setNavigationOnClickListener{
            onBackPressed()
        }
        setSupportActionBar(chatRoomToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupMessages()

        messageField.onFocusChangeListener = View.OnFocusChangeListener {
                v, hasFocus ->
            run {
                if (hasFocus) {
                    if (messagesAdapter.itemCount > 0){
                        linearLayoutManager.smoothScrollToPosition(messagesRecyclerView,
                            null, messagesAdapter.itemCount)
                    }

                }
            }
        }

        sendMessageFab.setOnClickListener{
            if (!InputHelper.isEmpty(messageField)){
                val message : ChatMessage = ChatMessage(5,messageField.text.toString()
                    ,DummyData.getDummyContacts(), Date().time,false,0,DummyData.getDummyContacts()[0],false)
                messagesAdapter.add(message)
                messageField.text.clear()
                KeyboardUtils.hideSoftInput(this)
            }else{
                onError("ChatRoom","The message is Empty")
                KeyboardUtils.hideSoftInput(this)
            }

        }


    }


    fun setupMessages(){
        linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        messagesRecyclerView.layoutManager = linearLayoutManager
        messagesRecyclerView.setHasFixedSize(true)
        messagesRecyclerView.setHideScrollbar(true)
        messagesAdapter = ChatRoomAdapter(this,DummyData.getDummyMessages())
        messagesRecyclerView.adapter = messagesAdapter
    }


    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}