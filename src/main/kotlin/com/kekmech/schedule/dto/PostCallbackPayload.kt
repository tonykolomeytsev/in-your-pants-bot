package com.kekmech.schedule.dto

import com.google.gson.annotations.SerializedName

class PostCallbackPayload(
    val type: PayloadType,
    @SerializedName("group_id")
    val groupId: String,
    val secret: String?
)

class PostCallbackPayloadMessage(
    @SerializedName("object")
    val payload: MessagePayload
)

enum class PayloadType {
    @SerializedName("confirmation")
    CONFIRMATION,
    @SerializedName("message_new")
    MESSAGE_NEW
}

class MessagePayload(
    val message: VKMessage
)

class VKMessage(
    val id: Int,
    val date: Long,
    @SerializedName("peer_id")
    val peerId: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("from_id")
    val fromId: Int,
    val text: String?
) {

    val peerType: VKMessagePeerType get() = when {
        peerId > 2000000000 -> VKMessagePeerType.GROUP_CHAT
        peerId < 0 -> VKMessagePeerType.COMMUNITY
        else -> VKMessagePeerType.USER
    }
}

enum class VKMessagePeerType { USER, GROUP_CHAT, COMMUNITY }