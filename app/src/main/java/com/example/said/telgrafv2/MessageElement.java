package com.example.said.telgrafv2;

public class MessageElement {
    String messageId,senderId,recipientId,messageText,conversationId,messageTime;

    //mesajlar ile ilgili gerekli bilgilerin alinmasi constructeri
    public MessageElement(String messageId, String senderId, String recipientId, String messageText, String conversationId, String messageTime) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageText = messageText;
        this.conversationId = conversationId;
        this.messageTime = messageTime;
    }

    //getter ve setter fonksiyonlari ;)
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
