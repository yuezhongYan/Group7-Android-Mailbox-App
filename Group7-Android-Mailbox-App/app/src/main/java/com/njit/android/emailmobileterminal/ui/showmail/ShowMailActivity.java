package com.njit.android.emailmobileterminal.ui.showmail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.njit.android.emailmobileterminal.MyApplication;
import com.njit.android.emailmobileterminal.R;
import com.njit.android.emailmobileterminal.ReciveOneMail;
import com.njit.android.emailmobileterminal.bean.Email;

import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

public class ShowMailActivity extends AppCompatActivity {
    int index;
    int listMessageLength;
    String from = "default";
    String subject = "default";
    String date = "default";
    String mailContent = "default";
    private final static String HOST_STRING = "imap.gmail.com";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mail);
        index = getIntent().getIntExtra("index",0);
        listMessageLength = getIntent().getIntExtra("listMessageLength",0);
        final TextView textViewAddresser = findViewById(R.id.text_addresser_value);
        final TextView textViewSubject = findViewById(R.id.text_subject_value);
        final TextView textViewDate = findViewById(R.id.text_date_value);
        final WebView webView = findViewById(R.id.text_content_value);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getEmailOjectWithMessage();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewAddresser.setText(from);
                        textViewSubject.setText(subject);
                        textViewDate.setText(date);
                        webView.getSettings().setDefaultTextEncodingName("UTF -8");
                        webView.loadData(mailContent, "text/html; charset=UTF-8", null);
                    }
                });
            }
        }).start();
    }
    private Email getEmailOjectWithMessage(){
        MyApplication myApplication = (MyApplication)getApplication();
        try{
            Message msgParameter = myApplication.getMessage();
            ReciveOneMail pmm = new ReciveOneMail((MimeMessage) msgParameter);
            from = msgParameter.getFrom()[0].toString();
            subject = msgParameter.getSubject();
            date = pmm.getSentDate();
            pmm.getMailContent((Part) msgParameter);
            mailContent = pmm.getBodyText();
            System.out.println("From: " + from);
            System.out.println("Subject: " + subject);
            System.out.println("Date: " + date);
            return new Email(from,subject,date);
        }catch (FolderClosedException ex) {
            ex.printStackTrace();
            Message[] mess;
            try{
                myApplication.getFetchEmail().login(HOST_STRING,myApplication.getUserInfo().getUserName(),myApplication.getUserInfo().getUserPwd());
                mess = myApplication.getFetchEmail().getMessages();
                //要使用已经存在的listMessage的长度,而不能是新获取的Message数组的长度.
                int len = listMessageLength;
                Log.e("FolderClosedException","len="+len);
                Log.e("FolderClosedException","index="+index);
                Log.e("FolderClosedException","len-index-1="+(len-index-1));
                ReciveOneMail pmm = new ReciveOneMail((MimeMessage) mess[len-index-1]);
                from = mess[len-index-1].getFrom()[0].toString();
                subject = mess[len-index-1].getSubject();
                date = pmm.getSentDate();
                pmm.getMailContent((Part) mess[len-index-1]);
                mailContent = pmm.getBodyText();
                System.out.println("From: " + from);
                System.out.println("Subject: " + subject);
                System.out.println("Date: " + date);
                return new Email(from,subject,date);
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
