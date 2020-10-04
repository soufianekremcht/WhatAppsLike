package com.luisppinheiroj.whatappslike.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.ChatObject
import com.luisppinheiroj.whatappslike.helper.AppHelper
import com.luisppinheiroj.whatappslike.helper.MyTimeUtils
import kotlinx.android.synthetic.main.item_conversation.view.*

class ChatsAdapter(var mContext : Context, var chatObjects :ArrayList<ChatObject>,var listener : ChatsAdapterListener)
    : RecyclerView.Adapter<ChatsAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation,parent,false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatObjects.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun add(chat :ChatObject){
        chatObjects.add(chat)
        //
        notifyItemInserted(chatObjects.size)
    }
    fun addAll(chats:List<ChatObject>){
        chatObjects.clear()
        chatObjects.addAll(chats)
        notifyDataSetChanged()
    }
    fun update(chatToUpdate : ChatObject){
        for (i in 0 ..chatObjects.size){
            if (chatObjects[i].chatId.equals(chatToUpdate.chatId)) {
                chatObjects[i] = chatToUpdate
                notifyItemChanged(i)
                break
            }

        }
    }

    inner class ConversationViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var chatItemLayout :RelativeLayout = itemView.chat_item_layout
        var chatIconFront : RelativeLayout = itemView.chat_icon_front
        var chatIconFrontImg : ImageView = itemView.chat_icon_img
        var chatIconText :TextView = itemView.chat_icon_text

        var chatLastMsg : TextView = itemView.chat_last_message_text
        var chatMsgTimestamp : TextView = itemView.chat_message_timestamp
        var chatMsgSender : TextView = itemView.message_sender

        fun onBind(position: Int){
            val chatObject = chatObjects[position]

            chatMsgTimestamp.text = MyTimeUtils.formatConversationTimestamp(mContext,chatObject.date)
            chatLastMsg.text = chatObject.snippet
            chatMsgSender.text = chatObject.title

            chatIconFrontImg.setColorFilter(AppHelper.getRandomMaterialColor(mContext,"500"))

            chatIconText.text = chatObject.title.subSequence(0,1)


            chatItemLayout.setOnClickListener{
                listener.onChatClicked(chatObject)
            }

        }

    }

    interface ChatsAdapterListener{
        fun onChatClicked(chat : ChatObject)
    }
}