package com.luisppinheiroj.whatappslike.data.model



data class Conversation(
    var id: Long?,
    var snippet: String,
    var date: Int,
    var read: Boolean,
    var title: String,
    var photoUri: String?,
    var isGroupConversation: Boolean,
    var chatMessages : List<ChatMessage>
) {
    fun getStringToCompare(): String {
        return copy(id = 0).toString()
    }
}