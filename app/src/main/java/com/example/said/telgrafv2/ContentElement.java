package com.example.said.telgrafv2;

public class ContentElement {

    private int userId,postId;
    private String username;
    private int time;
    private String contentText;
    private String imageUrl;
    private boolean userLike;
    private String likeCount;

    public ContentElement() {
    }

    //constructer ile gerekli bilgilerin alinmasi
    public ContentElement(int postId,int userId,String username, int time, String contentText,String imageUrl,boolean userLike,String likeCount) {
        this.postId=postId;
        this.userId = userId;
        this.username = username;
        this.time = time;
        this.contentText = contentText;
        this.imageUrl=imageUrl;
        this.userLike=userLike;
        this.likeCount=likeCount;
    }

    //getter ve stter islemleri
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isUserLike() {
        return userLike;
    }

    public void setUserLike(boolean userLike) {
        this.userLike = userLike;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
}
