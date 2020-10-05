package com.luisppinheiroj.whatappslike.ui.chat_room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.ChatMessage
import com.luisppinheiroj.whatappslike.helper.AppHelper
import com.luisppinheiroj.whatappslike.helper.InputHelper
import com.luisppinheiroj.whatappslike.helper.MyTimeUtils
import kotlinx.android.synthetic.main.item_received_msg.view.*
import kotlinx.android.synthetic.main.item_sent_msg.view.*

class MessagesAdapter(var mContext :Context, var chatMessages : ArrayList<ChatMessage>):
    RecyclerView.Adapter<MessagesAdapter.ChatRoomViewHolder>(),FastScroller.SectionIndexer {

    val VIEW_RECEIVED_MESSAGE :Int = 0
    val VIEW_SENT_MESSAGE :Int = 1

    var group_chat : Boolean = true

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isReceived)
            VIEW_RECEIVED_MESSAGE
        else VIEW_SENT_MESSAGE

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val layout : Int = when(viewType){
            VIEW_RECEIVED_MESSAGE -> R.layout.item_received_msg
            VIEW_SENT_MESSAGE -> R.layout.item_sent_msg
            else -> R.layout.item_received_msg
        }
        val view = LayoutInflater.from(mContext).inflate(layout,parent,false)
        return ChatRoomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  chatMessages.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.onBind(position)
    }


    fun add(newChatMessage :ChatMessage){
        chatMessages.add(newChatMessage)
        notifyItemInserted(itemCount-1)
    }
    fun addAll(newChatMessages: List<ChatMessage>){
        chatMessages.clear()
        chatMessages.addAll(newChatMessages)
        notifyDataSetChanged()
    }


    fun getItem(position: Int): ChatMessage{
        return chatMessages[position]
    }


    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // sent message layout


        // received message Layout




        fun onBind(position: Int){
            val message : ChatMessage = chatMessages[position]
            if (message.isReceived){
                val receivedMsgBody: TextView = itemView.received_msg_body
                val receivedMsgSenderPhotoText : TextView = itemView.received_msg_sender_photo_text
                val receivedMsgSenderPhoto : ImageView = itemView.received_msg_sender_photo
                val receivedMsgSenderName : TextView = itemView.received_msg_sender_name
                val receivedMsgDate : TextView = itemView.received_msg_date_text

                receivedMsgSenderName.text = message.sender.name
                receivedMsgDate.text = MyTimeUtils.formatTimestamp(mContext,message.date)

                // show sender name
                receivedMsgSenderName.visibility = View.VISIBLE

                receivedMsgBody.text = message.body
                if (!InputHelper.isEmpty(message.sender.name))
                    receivedMsgSenderPhotoText.text = message.sender.name.substring(0,1).toUpperCase()
                receivedMsgSenderPhoto.setColorFilter(
                    AppHelper.getRandomMaterialColor(mContext, "400"))

            }else{
                val sentMessageBody :TextView = itemView.sent_message_body
                val sentMsgTimestamp : TextView = itemView.sent_msg_date_text

                sentMsgTimestamp.text = MyTimeUtils.formatTimestamp(mContext,message.date)
                sentMessageBody.text = message.body
            }



        }
    }

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).body.substring(0,1)
    }
}