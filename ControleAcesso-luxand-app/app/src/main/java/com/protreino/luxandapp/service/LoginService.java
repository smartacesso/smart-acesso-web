package com.protreino.luxandapp.service;

import com.protreino.services.to.SimpleUser;

import retrofit2.Call;

public class LoginService {

    private static volatile LoginService instance;

    private SimpleUser user = null;

    private LoginService() {
    }

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    public void setLoggedInUser(SimpleUser user) {
        this.user = user;
    }

    public Call<String> isWorking() {
        return null;
    }
}