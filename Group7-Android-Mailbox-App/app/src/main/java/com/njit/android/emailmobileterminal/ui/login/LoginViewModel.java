package com.njit.android.emailmobileterminal.ui.login;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.njit.android.emailmobileterminal.FetchEmail;
import com.njit.android.emailmobileterminal.R;
import com.njit.android.emailmobileterminal.utils.ToastHelper;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final static String HOST_STRING = "imap.gmail.com";

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
    public void login(final FetchEmail fetchEmail,final  String username, final String password) {
        if (!isUserNameValid(username)) {
            ToastHelper.showResID(R.string.invalid_username);
            return;
        }
        if (!isPasswordValid(password)){
            ToastHelper.showResID(R.string.invalid_password);
            return;
        }

        // can be launched in a separate asynchronous job
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.e("username:",username);
                    Log.e("password:",password);
                    fetchEmail.login(HOST_STRING,username,password);

                    if(fetchEmail.isLoggedIn()){
                        loginResult.postValue(new LoginResult(true,"", username, password));
                    } else{
                        loginResult.postValue(new LoginResult(false,"Log in failed", username, password));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e("e:"," "+e.toString());
                    loginResult.postValue(new LoginResult(false,e.toString(), username, password));
                }

            }
        }).start();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (TextUtils.isEmpty(username.trim())) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.trim().length() > 5;
    }
}
