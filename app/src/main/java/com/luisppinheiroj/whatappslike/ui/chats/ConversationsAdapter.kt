package com.luisppinheiroj.whatappslike.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.Conversation

class ConversationsAdapter(var mContext : Context, var conversations :List<Conversation>)
    : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation,parent,false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ConversationViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun onBind(position: Int){

        }

    }
}