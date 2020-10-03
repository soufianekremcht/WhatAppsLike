package com.luisppinheiroj.whatappslike.helper

import com.luisppinheiroj.whatappslike.data.model.ChatMessage
import com.luisppinheiroj.whatappslike.data.model.Conversation
import com.luisppinheiroj.whatappslike.data.model.SimpleContact
import java.util.*
import kotlin.collections.ArrayList

object DummyData{

    fun getDummyConversations() : List<Conversation>{
        var coversations :ArrayList<Conversation> = ArrayList()
        coversations.add(Conversation(1,"last message",3,false,
            "Testing 1",null,false, getDummyMessages()))

        coversations.add(Conversation(2,"last chat",3,false,
            "Testing 2",null,false, getDummyMessages()))

        coversations.add(Conversation(3,"last money",3,false,
            "Testing 3",null,false, getDummyMessages()))

        coversations.add(Conversation(4,"Wow snippet",3,false,
            "Testing 4",null,false, getDummyMessages()))
        return coversations
    }

    fun  getDummyMessages() : ArrayList<ChatMessage>{
        var chatMessages :ArrayList<ChatMessage> = ArrayList()

        chatMessages.add(ChatMessage(1,"First Message",
            getDummyContacts(), Date().time,false,0,
            getDummyContacts()[0],false))


        chatMessages.add(ChatMessage(2,"Second Message",
            getDummyContacts(), Date().time,false,0,
            getDummyContacts()[0],false))

        chatMessages.add(ChatMessage(3,"Third Message",
            getDummyContacts(), Date().time,false,0,
            getDummyContacts()[1],true))

        chatMessages.add(ChatMessage(4,"Forth Message",
            getDummyContacts(), Date().time,false,0,
            getDummyContacts()[0],false))

        chatMessages.add(ChatMessage(5,"Fifth Message",
            getDummyContacts(), Date().time,false,0,
            getDummyContacts()[1],true))

        return chatMessages

    }
    fun  getDummyContacts() : ArrayList<SimpleContact>{
        var contacts : ArrayList<SimpleContact> = ArrayList()
        contacts.add(SimpleContact("First@gmail.com","FirstContact"))
        contacts.add(SimpleContact("Second@gmail.com","SecondContact"))
        contacts.add(SimpleContact("Third@gmail.com","ThirdContact"))
        contacts.add(SimpleContact("Forth@gmail.com","ForthContact"))
        contacts.add(SimpleContact("Forth@gmail.com","ForthContact"))
        return contacts
    }
}