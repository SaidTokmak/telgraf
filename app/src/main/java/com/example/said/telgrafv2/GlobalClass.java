package com.example.said.telgrafv2;

import android.app.Application;

//global classta butun uygulama sayfalarindan erismek istedigimiz bilgieri tutuyoruz
public class GlobalClass extends Application {
    static String token,email;
    static int id;

    public static int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
