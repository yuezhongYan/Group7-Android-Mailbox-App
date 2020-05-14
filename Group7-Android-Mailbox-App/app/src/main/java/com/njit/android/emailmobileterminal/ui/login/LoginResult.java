package com.njit.android.emailmobileterminal.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    private String userName;
    private String userPwd;
    @Nullable
    private boolean success;
    @Nullable
    private String error;

    LoginResult(@Nullable boolean success,@Nullable String error, String userName,String userPwd) {
        this.userName = userName;
        this.userPwd = userPwd;
        this.success = success;
        this.error = error;
    }

    @Nullable
    boolean getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }
}
