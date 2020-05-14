package com.njit.android.emailmobileterminal;

import android.app.Application;
import android.text.TextUtils;

import com.njit.android.emailmobileterminal.bean.UserInfo;
import com.njit.android.emailmobileterminal.utils.SPHelper;

import javax.mail.Message;

public class MyApplication extends Application {
    private final String keyUserName = "MyApplication_keyUserName";
    private final String keyUserPwd = "MyApplication_keyUserPwd";
    private FetchEmail fetchEmail;
    private UserInfo userInfo;
    private Message message;
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        myApplication = this;
    }

    public static MyApplication getInstance(){
        return myApplication;
    }
    public FetchEmail getFetchEmail() {
        return fetchEmail;
    }

    public void setFetchEmail(FetchEmail fetchEmail) {
        this.fetchEmail = fetchEmail;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null){
            String name = SPHelper.getString(this, keyUserName);
            String pwd = SPHelper.getString(this, keyUserPwd);
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                userInfo = new UserInfo(name, pwd);
            }
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        SPHelper.putString(this, keyUserName, userInfo.getUserName());
        SPHelper.putString(this, keyUserPwd, userInfo.getUserPwd());
    }

    public void logOut(){
        SPHelper.putString(this, keyUserName, "");
        SPHelper.putString(this, keyUserPwd, "");
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
