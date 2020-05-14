package com.njit.android.emailmobileterminal.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.njit.android.emailmobileterminal.FetchEmail;
import com.njit.android.emailmobileterminal.MainActivity;
import com.njit.android.emailmobileterminal.MyApplication;
import com.njit.android.emailmobileterminal.R;
import com.njit.android.emailmobileterminal.bean.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    EditText usernameEditText;
    EditText passwordEditText;
    private ProgressBar loadingProgressBar;
    private Button loginButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loginButton.setEnabled(false);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                enableView(true);
                if (loginResult.getSuccess()!=true) {
                    showLoginFailed(loginResult.getError());
                } else {
                    updateUiWithUser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    UserInfo userInfo = new UserInfo(loginResult.getUserName(),loginResult.getUserPwd());
                    MyApplication.getInstance().setUserInfo(userInfo);
                    startActivity(intent);
                    finish();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    FetchEmail fetchEmail = new FetchEmail();
//                    MyApplication.getInstance().setFetchEmail(fetchEmail);
//                    loginViewModel.login(fetchEmail,usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            usernameEditText.setText(userInfo.getUserName());
            tryLogin(userInfo.getUserName(), userInfo.getUserPwd());
        }
    }


    private void tryLogin(String userName,String userPwd){
        enableView(false);
        FetchEmail fetchEmail = new FetchEmail();
        MyApplication.getInstance().setFetchEmail(fetchEmail);
        loginViewModel.login(fetchEmail,userName, userPwd);
    }

    private void enableView(boolean status){
        loadingProgressBar.setVisibility(status ? View.GONE : View.VISIBLE);
        usernameEditText.setEnabled(status);
        passwordEditText.setEnabled(status);
        loginButton.setClickable(status);
    }

    private void updateUiWithUser() {
        String welcome = getString(R.string.welcome);
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
