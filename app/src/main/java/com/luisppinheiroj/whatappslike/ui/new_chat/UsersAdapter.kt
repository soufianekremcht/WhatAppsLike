package com.luisppinheiroj.whatappslike.ui.new_chat

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.data.model.UserObject
import com.luisppinheiroj.whatappslike.helper.AppHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_chat_user.view.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class UsersAdapter(var mContext: Context, var users : ArrayList<UserObject>, var listener:UsersAdapterListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() , Filterable{



    var filteredUsers : ArrayList<UserObject> =ArrayList()
    var groupChatEnabled : Boolean = false
    var searchText : String = ""


    init {
        filteredUsers = users
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun getUsers() : List<UserObject>{
        return filteredUsers
    }


    // get Filtered Users

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {

                val charString = charSequence.toString()
                var usersName = " ";

                var filteredData : ArrayList<UserObject> = ArrayList()

                if (charString.isNotEmpty()) {
                    val filteredList: ArrayList<UserObject> = ArrayList()
                    for (user in users) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name of the user
                        // lower case of english only
                        if (user.name.contains(charString)) {
                            usersName += user.name + " "
                            filteredList.add(user)
                        }
                    }
                    Toasty.error(mContext,"Names : "+usersName,Toasty.LENGTH_LONG).show()
                    filteredData.addAll(filteredList)
                }

                val filterResults = FilterResults()
                filterResults.values = filteredData

                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                // set the filtered users
                filteredUsers.clear()
                if (filterResults.values != null)
                    filteredUsers.addAll(filterResults.values as ArrayList<UserObject>)

                // refresh the list with filtered data
                notifyDataSetChanged()

            }
        }
    }


    private fun highlightSearchedText(holder: UserViewHolder, position: Int) {
        val sb = SpannableStringBuilder(holder.userName.text)
        val regex: String = searchText
        val word: Pattern = Pattern.compile(regex)
        val match: Matcher = word.matcher(holder.userName.text.toString())
        while (match.find()) {
            val fcs = ForegroundColorSpan(
                ContextCompat.getColor(mContext, R.color.accent_green)
            ) //specify color here
            sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        holder.userName.text = sb
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userLayout :RelativeLayout = itemView.chat_user_layout
        var userIconFront : RelativeLayout = itemView.user_icon_front
        var userIconFrontImg : ImageView = itemView.user_icon_profile
        var userIconText : TextView = itemView.icon_text

        var userIconBack : RelativeLayout = itemView.icon_back

        var userName :TextView = itemView.user_name
        var userEmail : TextView = itemView.user_email
        var userCheckBox : CheckBox = itemView.chat_user_check_box

        fun onBind(position :Int){
            val user = filteredUsers[position]
            userName.text = user.name
            userEmail.text = user.email
            userIconText.text = user.name.substring(0,1).toUpperCase(Locale.getDefault())

            if (searchText != "") highlightSearchedText(this,position)

            userIconFrontImg.setColorFilter(AppHelper.getRandomMaterialColor(mContext,"400"))

            if (groupChatEnabled){
                userCheckBox.visibility = View.VISIBLE
            }else{
                userCheckBox.visibility = View.GONE
            }

            userLayout.setOnClickListener{
                // go to Chat Room Activity
                listener.onUserClicked(user)
            }

            userCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                user.isSelected = isChecked
                // Set Selected Background
            }


        }
    }

    interface UsersAdapterListener{
        fun onUserClicked(user: UserObject)
    }

}