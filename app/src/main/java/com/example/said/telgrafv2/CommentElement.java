package com.example.said.telgrafv2;

public class CommentElement {

    private String commentId,commentUserName,time,commentMain,profilePicture;
    int userId;

    public CommentElement() {
    }

    //constructer ile gerekli bilgilerin alinmasi
    public CommentElement(String commentId,int userId,String commentUserName, String time, String commentMain,String profilePicture) {
        this.commentId=commentId;
        this.userId=userId;
        this.commentUserName = commentUserName;
        this.time = time;
        this.commentMain = commentMain;
        this.profilePicture=profilePicture;
    }

    //getter ve setter fonksiyonlari ;)
    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCommentMain() {
        return commentMain;
    }

    public void setCommentMain(String  commentMain) {
        this.commentMain = commentMain;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
