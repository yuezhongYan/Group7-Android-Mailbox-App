package com.njit.android.emailmobileterminal.ui.sendmail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.njit.android.emailmobileterminal.MyApplication;
import com.njit.android.emailmobileterminal.R;
import com.njit.android.emailmobileterminal.SendEmail;


public class SendMailFragment extends Fragment {
    private SendMailViewModel sendMailViewModel;
    private static final int SUCCESS = 10000;
    private static final int FAILED = 10001;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendMailViewModel = ViewModelProviders.of(this).get(SendMailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send_mail, container, false);
        final EditText editTextAddresser = root.findViewById(R.id.edit_text_addresser_value);
        sendMailViewModel.getEdit_text_addresser_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                editTextAddresser.setText(s);
            }
        });
        final EditText editTextSubject = root.findViewById(R.id.edit_text_subject_value);
        sendMailViewModel.getEdit_text_subject_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                editTextSubject.setText(s);
            }
        });

        final EditText editTextContent = root.findViewById(R.id.edit_text_content_value);
        sendMailViewModel.getEdit_text_content_value().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                editTextContent.setText(s);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.button_send_email);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(editTextAddresser.getText())
                        || TextUtils.isEmpty(editTextSubject.getText())
                        || TextUtils.isEmpty(editTextContent.getText())){
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String recipients = editTextAddresser.getText().toString();
                        String subject = editTextSubject.getText().toString();
                        String mailContent = editTextContent.getText().toString();
                        String addresser = MyApplication.getInstance().getUserInfo().getUserName();
                        String password = MyApplication.getInstance().getUserInfo().getUserPwd();
                        SendEmail sendEmailObject = new SendEmail(recipients,subject,mailContent,addresser,password);
                        try{
                            sendEmailObject.sendMail();
                            sendMailViewModel.clearEditTextAfterSendMessage();
                            handler.sendEmptyMessage(SUCCESS);

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Exception is ",e.toString());
                            Message message = Message.obtain();
                            message.what = FAILED;
                            message.obj = e.toString();
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
        return root;
    }
    private void sendMessageSuccessfully() {
        String send = getString(R.string.send);
        // TODO : initiate successful logged in experience
        Toast.makeText(getContext(), send, Toast.LENGTH_LONG).show();
    }

    private void sendMessageFailed(String errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == SUCCESS){
                sendMessageSuccessfully();
            } else if(msg.what == FAILED){
                sendMessageFailed(msg.obj.toString());
            }
        }
    };
}
