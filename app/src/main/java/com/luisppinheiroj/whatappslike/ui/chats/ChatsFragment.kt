package com.luisppinheiroj.whatappslike.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.ui.base.BaseFragment

class ChatsFragment: BaseFragment(),ChatsMvp.View{

    @BindView(R.id.chats_recycler_view)
    lateinit var chatsRecyclerView : RecyclerView

    @BindView(R.id.chats_empty_view)
    lateinit var empty_list_view :LinearLayout


    lateinit var conversationsAdapter : ConversationsAdapter

    private lateinit var mPresenter :ChatsPresenter<ChatsMvp.View>


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
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        chatsRecyclerView.itemAnimator = DefaultItemAnimator()
        conversationsAdapter = ConversationsAdapter(context!!,ArrayList())
        chatsRecyclerView.adapter = conversationsAdapter
        checkEmptyView()
    }

    fun  checkEmptyView(){
        if (conversationsAdapter.itemCount<1){
            chatsRecyclerView.visibility = View.GONE
            empty_list_view.visibility = View.VISIBLE
        }else{
            chatsRecyclerView.visibility = View.VISIBLE
            empty_list_view.visibility = View.GONE
        }

    }
}