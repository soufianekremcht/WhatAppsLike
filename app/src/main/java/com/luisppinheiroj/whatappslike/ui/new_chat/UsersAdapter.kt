package com.luisppinheiroj.whatappslike.ui.new_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.SimpleContact
import kotlinx.android.synthetic.main.item_chat_user.view.*

class UsersAdapter(var mContext: Context, var users : ArrayList<SimpleContact>,var listener:UsersAdapterListener) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userLayout :RelativeLayout = itemView.chat_user_layout
        var userIconFront : RelativeLayout = itemView.user_icon_front
        var userIconText : TextView = itemView.icon_text

        var userIconBack : RelativeLayout = itemView.icon_back

        var userName :TextView = itemView.user_name
        var userEmail : TextView = itemView.user_email

        fun onBind(position :Int){
            val user = users[position]
            userName.text = user.name
            userEmail.text = user.email
            userIconText.text = user.name.substring(0,1)

            userLayout.setOnClickListener{
                // go to Chat Room Activity
                listener.onUserClicked()

            }


        }
    }

    interface UsersAdapterListener{
        fun onUserClicked()
    }

}