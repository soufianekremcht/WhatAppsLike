package com.luisppinheiroj.whatappslike.data.model

data class ChatMessage(
    val id: Int, val body: String, val participants: ArrayList<SimpleContact>, val date: Long,
    val read: Boolean, val thread: Int, var sender: SimpleContact,
    var isReceived: Boolean) {


}
