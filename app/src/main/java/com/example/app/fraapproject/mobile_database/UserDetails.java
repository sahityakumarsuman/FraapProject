package com.example.app.fraapproject.mobile_database;

/**
 * Created by Duke on 1/11/2016.
 */
public class UserDetails {

    String _useName, _userEmail, _userAuthId, _imagePath, _userId;

    public UserDetails() {
    }

    public UserDetails(String _useName, String _userEmail, String _userAuthId, String _imagePath, String _userId) {

        this._useName = _useName;
        this._userEmail = _userEmail;
        this._userAuthId = _userAuthId;
        this._imagePath = _imagePath;
        this._userId = _userId;
    }


    public void setUserName(String name) {

        this._useName = name;
    }

    public String getUserName() {
        return this._useName;
    }

    public void set_userEmail(String email) {
        this._userEmail = email;
    }

    public String getEmail() {

        return this._userEmail;
    }

    public void setUserId(String userId) {
        this._userId = userId;
    }

    public String getUserId() {
        return this._userId;
    }

    public void setAuthCode(String authCode) {

        this._userAuthId = authCode;
    }

    public String getAuthCode() {
        return this._userAuthId;
    }

    public void setImagePath(String imagePath) {

        this._imagePath = imagePath;

    }

    public String getImagePath() {
        return this._imagePath;
    }

}

