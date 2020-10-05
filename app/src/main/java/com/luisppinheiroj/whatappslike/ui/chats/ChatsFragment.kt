package com.luisppinheiroj.whatappslike.ui.chats

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.luisppinheiroj.whatappslike.ui.main.MainActivity
import com.luisppinheiroj.whatappslike.ui.views.ChatDividerItemDecoration
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_network_error.*
import java.util.*
import kotlin.collections.ArrayList


class ChatsFragment : BaseFragment(), ChatsAdapter.ChatsAdapterListener {


    @BindView(R.id.chats_refresh_layout)
    lateinit var chatsRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.chats_recycler_view)
    lateinit var chatsRecyclerView: RecyclerView

    @BindView(R.id.empty_list_layout)
    lateinit var emptyListView: LinearLayout

    @BindView(R.id.network_error_layout)
    lateinit var networkErrorLayout: LinearLayout


    @BindView(R.id.chats_progressbar)
    lateinit var chatsProgressBar: ProgressBar

    lateinit var chatsAdapter: ChatsAdapter

    private lateinit var currentAppUserUid :String
    private lateinit var currentAppUserObject : UserObject
    private var chatsList: ArrayList<ChatObject> = ArrayList()


    companion object {
        fun newInstance(): ChatsFragment {
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
        savedInstanceState: Bundle?
    ): View? {

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_chats, container, false)
        ButterKnife.bind(this, view)
        currentAppUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkErrorLayout.visibility = View.GONE
        chatsRecyclerView.visibility = View.GONE
        emptyListView.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        //linearLayoutManager.reverseLayout = true
        //linearLayoutManager.stackFromEnd = true
        chatsRecyclerView.layoutManager = LinearLayoutManager(activity)
        chatsRecyclerView.addItemDecoration(ChatDividerItemDecoration(activity!!, VERTICAL, 16))
        chatsRecyclerView.itemAnimator = DefaultItemAnimator()
        chatsAdapter = ChatsAdapter(context!!, chatsList, this)
        chatsRecyclerView.adapter = chatsAdapter

        chatsRefreshLayout.setOnRefreshListener {
            chatsList = ArrayList()
            setupChats()
            chatsRefreshLayout.isRefreshing = false
            showMessage(getString(R.string.list_refresh_msg))
        }
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
        removeAllDbListeners()
        super.onDestroy()
    }


    private fun setupChats() {
        if (AppHelper.isNetworkStatusAvialable(context!!)) {
            // DATA NoT BEING FETCHED FAST
            getUserChatList()
        } else {
            showNetworkErrorView()
        }

    }


    private fun getUserChatList() {
        chatsProgressBar.visibility = View.VISIBLE

        val mUserChatDB = FirebaseDatabase.getInstance().reference
            .child(USERS_PATH)
            .child(currentAppUserUid)
            .child(USERS_CHATS_PATH)

        mUserChatDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (chatsIdSnapshot in dataSnapshot.children) {
                        val chatUid = chatsIdSnapshot.key!!
                        getChatData(chatUid)
                    }
                }else{
                    checkEmptyView()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toasty.error(activity!!, databaseError.message).show()
                checkEmptyView()
            }
        })
    }

    private fun getChatData(chatUid : String) {
        val mChatInfoDB = FirebaseDatabase.getInstance().reference
            .child(CHATS_PATH)
            .child(chatUid)
            .child(CHATS_INFO_PATH)

        mChatInfoDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(chatDataSnapshot: DataSnapshot) {
                if (chatDataSnapshot.exists()) {
                    val currentChat = ChatObject(chatUid, "S",
                        Date().time, false, "T", null,
                        false)

                    var title = ""
                    var lastMessage = "Info : New Chat Has Been Created !!!!"
                    var timeStamp: Long = Date().time
                    // chat info

                    val currentChatId = chatDataSnapshot.child("id").value.toString()
                    if (chatDataSnapshot.child("title").value != null)
                        title = chatDataSnapshot.child("title").value.toString()

                    if (chatDataSnapshot.child("lastMessage").value != null)
                        lastMessage = chatDataSnapshot.child("lastMessage").value.toString()

                    if (chatDataSnapshot.child("timestamp").value != null)
                        timeStamp = chatDataSnapshot.child("timestamp").value.toString().toLong()

                    /*** Testing  ***/

                    currentChat.snippet = lastMessage
                    currentChat.date = timeStamp
                    currentChat.title = title

                    // TODO : TEST CHAT LOADING HERE

                    var exists = false
                    for (chat in chatsList) {
                        if (chat.chatId.equals(currentChat.chatId))
                            exists = true
                    }
                    if (!exists) {
                        // add the new chat in the top
                        if (chatsList.isEmpty()) chatsList.add(currentChat)
                        else chatsList.add(0,currentChat)

                        // Problem Here : Still Confusing
                        // Search For All The Participants In The Chat
                        for (participantSnapshot in chatDataSnapshot.child(USERS_PATH).children) {
                            var participantUid = ""
                            if (participantSnapshot.key != null) {
                                participantUid = participantSnapshot.key!!
                                getParticipantInfo(participantUid, currentChat)
                            }
                        }
                    }

                }

                checkEmptyView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError("GetChatsData", databaseError.message)
                checkEmptyView()
            }
        })
    }

    private fun getParticipantInfo(userUid: String, chat: ChatObject) {
        val userInfoRef = FirebaseDatabase.getInstance().reference
            .child(USERS_PATH).child(userUid)
        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(userInfosnapshot: DataSnapshot) {
                if (userInfosnapshot.exists()) {
                    val name: String = userInfosnapshot.child("name").value.toString()
                    val email: String = userInfosnapshot.child("email").value.toString()
                    val participant = UserObject(userUid, email, name)

                    if (!chat.participantUsers.contains(participant))
                        chat.participantUsers.add(participant)
                    if (participant.uid == currentAppUserUid){
                        currentAppUserObject = participant
                        getMainActivity().main_toolbar_title.text= getString(R.string.chats)+ " : " + participant.name

                        getMainActivity().main_toolbar_sub_title.text = participant.email

                    }

                    // Set Title For The Group Chat
                    if (chat.participantUsers.size > 2 && !chat.title.contains("Group ")) {
                        chat.isGroupConversation = true
                        chat.title = "GROUP : " + chat.title
                    }
                    chatsAdapter.notifyDataSetChanged()
                    checkEmptyView()
                    // scroll to the top
                    if (chatsList.isNotEmpty())
                        chatsRecyclerView.smoothScrollToPosition(0)



                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError("GetParticipantInfo", error.message)
                checkEmptyView()
            }


        })
    }


    private fun checkEmptyView() {
        networkErrorLayout.visibility = View.GONE
        chatsProgressBar.visibility = View.GONE

        if (chatsAdapter.itemCount < 1) {
            chatsRecyclerView.visibility = View.GONE
            emptyListView.visibility = View.VISIBLE
        } else {
            chatsRecyclerView.visibility = View.VISIBLE
            emptyListView.visibility = View.GONE
        }

    }

    private fun showNetworkErrorView() {
        chatsProgressBar.visibility = View.GONE
        networkErrorLayout.visibility = View.VISIBLE


        no_internet_reload_btn.setOnClickListener {
            chatsProgressBar.visibility = View.VISIBLE
            networkErrorLayout.visibility = View.GONE
            setupChats()
        }

    }


    override fun onChatClicked(chat: ChatObject) {
        val intent = Intent(activity, ChatRoomActivity::class.java)
        intent.putExtra(AppConstants.CHATS_OBJECT, chat)
        startActivity(intent)
    }


    private fun removeAllDbListeners() {

    }

    private fun getMainActivity() = activity!! as MainActivity

}