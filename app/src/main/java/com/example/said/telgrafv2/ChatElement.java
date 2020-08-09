package com.example.said.telgrafv2;

public class ChatElement {

    String profilePicture;
    String username,conversationId;
    String messageContent;
    String time;
    String recId;

    public ChatElement() {
    }

    //constructer ile chat ile ilgili gerekli bilgilerin alinmasi
    public ChatElement(String profilePicture,String conversationId,String username,String recId) {
        this.profilePicture=profilePicture;
        this.conversationId=conversationId;
        this.username = username;
        this.recId=recId;
    }

    //bu bilgilerin getter ve setter fonksiyonlari ;)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
