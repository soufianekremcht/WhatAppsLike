package com.luisppinheiroj.whatappslike.helper

import com.luisppinheiroj.whatappslike.data.model.ChatMessage
import com.luisppinheiroj.whatappslike.data.model.ChatObject
import com.luisppinheiroj.whatappslike.data.model.UserObject
import java.util.*
import kotlin.collections.ArrayList

object DummyData{

    fun getDummyChats() : List<ChatObject>{
        var chats :ArrayList<ChatObject> = ArrayList()
        chats.add(ChatObject("1","last message",3,false,
            "Testing 1",null,false))

        chats.add(ChatObject("2","last chat",3,false,
            "Testing 2",null,false ))

        chats.add(ChatObject("3","last money",3,false,
            "Testing 3",null,false))

        chats.add(ChatObject("4","Wow snippet",3,false,
            "Testing 4",null,false))
        return chats
    }

    fun getDummyMessages() : ArrayList<ChatMessage>{
        var chatMessages :ArrayList<ChatMessage> = ArrayList()

        chatMessages.add(ChatMessage("1","First Message",
            Date().time,false, getDummyContacts()[0],ArrayList(),true))


        chatMessages.add(ChatMessage("2","Second Message",
             Date().time,false, getDummyContacts()[0], ArrayList(),true))
/*
        chatMessages.add(ChatMessage("3","Third Message",
            Date().time,false, getDummyContacts()[0],ArrayList(),true))

        chatMessages.add(ChatMessage("4","Forth Message",
            Date().time,false, getDummyContacts()[0],ArrayList(),false))

        chatMessages.add(ChatMessage("5","Fifth Message",
            Date().time,false, getDummyContacts()[0],ArrayList(),true))*/

        return chatMessages

    }
    fun  getDummyContacts() : ArrayList<UserObject>{
        val contacts : ArrayList<UserObject> = ArrayList()
        contacts.add(UserObject("A","First@gmail.com","FirstContact"))
        contacts.add(UserObject("B","Second@gmail.com","SecondContact"))
//        contacts.add(UserObject("C","Third@gmail.com","ThirdContact"))
//        contacts.add(UserObject("D","Forth@gmail.com","ForthContact"))
//        contacts.add(UserObject("F","FiFth@gmail.com","ForthContact"))
        return contacts
    }
}