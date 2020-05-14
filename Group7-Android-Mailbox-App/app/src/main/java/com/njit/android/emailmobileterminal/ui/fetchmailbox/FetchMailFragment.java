package com.njit.android.emailmobileterminal.ui.fetchmailbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.njit.android.emailmobileterminal.FetchEmail;
import com.njit.android.emailmobileterminal.MyAdapter;
import com.njit.android.emailmobileterminal.MyApplication;
import com.njit.android.emailmobileterminal.R;
import com.njit.android.emailmobileterminal.ReciveOneMail;
import com.njit.android.emailmobileterminal.bean.Email;
import com.njit.android.emailmobileterminal.bean.UserInfo;
import com.njit.android.emailmobileterminal.ui.showmail.ShowMailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;


public class FetchMailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final int FETCH_MESSAGE_SUCCESS = 10001;
    private static final int FETCH_EMAIL_SUCCESS = 10002;
    private static final int REFRESH_MESSAGE_SUCCESS = 10003;
    private static final int REFRESH_EMAIL_SUCCESS = 10004;
    private static final int FAILED = 10005;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<Email> list = new ArrayList<>();
    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 10;
    private GridLayoutManager mLayoutManager;
    private MyAdapter adapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    ProgressBar loadingProgressBar;
    MyApplication myApplication;
    private List<Message> listMessage = new ArrayList<>();
    private boolean isFetchingEmail = false;
    private boolean isFetchingMessage = false;
    private final static String HOST_STRING = "imap.gmail.com";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fetch_mail, container, false);
        initData();
        findView(root);
        initRefreshLayout();
        return root;
    }
    private void initData() {
        loadingProgressBar = getActivity().findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        myApplication = MyApplication.getInstance();
        if(listMessage==null || listMessage.isEmpty()){
            syncEmail(myApplication.getFetchEmail());
        }
    }


    private void findView(View root) {
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

    }

    private void initRefreshLayout() {
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);
    }

    private void initRecyclerView() {
        List<Email> firstEmails = new ArrayList<>(list);
        Log.e("initRecyclerView len",firstEmails.size()+"");
        adapter = new MyAdapter(firstEmails, getActivity(), firstEmails.size() > 0 ? true : false);
        adapter.setOnItemClickListener(new MyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object  data) {
                Toast.makeText(getActivity(),data.toString(),Toast.LENGTH_SHORT).show();
                int index = (int) data;
                myApplication.setMessage(listMessage.get(index));
                Intent intent = new Intent(getActivity(), ShowMailActivity.class);
                intent.putExtra("index",index);
                intent.putExtra("listMessageLength",listMessage.size());
                startActivity(intent);
            }
        });
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (adapter.isFadeTips() == false
                            && lastVisibleItem + 1 == adapter.getItemCount()
                            && isFetchingEmail == false) {
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }, 500);
                        Log.e("22222   fetchEmail"," updateRecyclerView adapter.getRealLastPosition():"+adapter.getRealLastPosition());
                        updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                    }

                    if (adapter.isFadeTips() == true
                            && lastVisibleItem + 2 == adapter.getItemCount()
                            && isFetchingEmail == false) {
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }, 500);
                        Log.e("33333   fetchEmail"," updateRecyclerView adapter.getRealLastPosition():"+adapter.getRealLastPosition());
                        updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    //
    private void getDatas(final int firstIndex, final int lastIndex,final List<Email> resList,final int resultcode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resList.clear();
                for (int i = firstIndex; i < lastIndex; i++) {
                    if (i < listMessage.size()) {
                        Message msgTmp = listMessage.get(i);
                        resList.add(getEmailOjectWithMessage(msgTmp,i));
                    }
                }
                android.os.Message msgHandler = handler.obtainMessage();
                msgHandler.arg1= firstIndex;
                msgHandler.arg2= lastIndex;
                msgHandler.what= resultcode;
                handler.sendMessage(msgHandler);
            }
        }).start();
    }
    private Email getEmailOjectWithMessage(Message msgParameter,int index){
        try{
            ReciveOneMail pmm = new ReciveOneMail((MimeMessage) msgParameter);
            String from = msgParameter.getFrom()[0].toString();
            String subject = msgParameter.getSubject();
            String date = pmm.getSentDate();
            System.out.println("From: " + from);
            System.out.println("Subject: " + subject);
            System.out.println("Date: " + date);
            return new Email(from,subject,date);
        }catch (FolderClosedException ex) {
            ex.printStackTrace();
            Message[] mess;
            try{
                myApplication = MyApplication.getInstance();
                myApplication.getFetchEmail().login(HOST_STRING,myApplication.getUserInfo().getUserName(),myApplication.getUserInfo().getUserPwd());
                mess = myApplication.getFetchEmail().getMessages();
                //要使用已经存在的listMessage的长度,而不能是新获取的Message数组的长度.
                int len = listMessage.size();
                Log.e("FolderClosedException","len="+len);
                Log.e("FolderClosedException","index="+index);
                Log.e("FolderClosedException","len-index-1="+(len-index-1));
                ReciveOneMail pmm = new ReciveOneMail((MimeMessage) mess[len-index-1]);
                String from = mess[len-index-1].getFrom()[0].toString();
                String subject = mess[len-index-1].getSubject();
                String date = pmm.getSentDate();
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

    private void updateRecyclerView(int fromIndex, int toIndex) {
        isFetchingEmail = true;
        getDatas(fromIndex, toIndex,list,FETCH_EMAIL_SUCCESS);
    }
//    private void updateRecyclerViewFromRefresh(int fromIndex, int toIndex) {
//        getDatas(fromIndex, toIndex,list,REFRESH_MESSAGE_SUCCESS);
//    }
    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        adapter.resetDatas();
        syncMoreEmail(myApplication.getFetchEmail());
    }
    public void syncEmail(final FetchEmail fetchEmail){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (fetchEmail == null){
                    Log.e("fetchEmail"," is null");
                    return;
                }
                if(!fetchEmail.isLoggedIn()){
                    Log.e("fetchEmail"," is not logged in");
                    return;
                }
                try{
                    Message[] mess = fetchEmail.getMessages();
                    int len = mess.length;
                    for(int i=len-1;i>=0;i--){
                        listMessage.add(mess[i]);
                    }
                    Log.e("fetchMessage sum","len:"+len);
                    handler.sendEmptyMessage(FETCH_MESSAGE_SUCCESS);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("Exception is ",e.toString());
                    android.os.Message message = android.os.Message.obtain();
                    message.what = FAILED;
                    message.obj = e.toString();
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
    public void syncMoreEmail(final FetchEmail fetchEmail){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (fetchEmail == null){
                    Log.e("fetchEmail"," is null");
                    return;
                }
                if(!fetchEmail.isLoggedIn()){
                    Log.e("fetchEmail"," is not logged in");
                    try{
                        myApplication = MyApplication.getInstance();
                        UserInfo userInfo = myApplication.getUserInfo();
                        myApplication.getFetchEmail().login(HOST_STRING,userInfo.getUserName(),userInfo.getUserPwd());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e("try again"," fetchEmail.isLoggedIn():"+fetchEmail.isLoggedIn());
                }
                try{
                    Message[] mess = fetchEmail.getMessages();
                    int len = mess.length;
                    int lenHAVED = listMessage.size();
                    if(len>lenHAVED){
                        for(int i=lenHAVED;i<len;i++){
                            //头插法插入前排
                            listMessage.add(0,mess[i]);
                        }
                    }

                    Log.e("fetchMessage sum","len-lenHAVED:"+(len-lenHAVED));
                    handler.sendEmptyMessage(REFRESH_MESSAGE_SUCCESS);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("Exception is ",e.toString());
                    android.os.Message message = android.os.Message.obtain();
                    message.what = FAILED;
                    message.obj = e.toString();
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //loadingProgressBar.setVisibility(View.GONE);
            if(msg.what == FETCH_MESSAGE_SUCCESS){
                //updateData();
                //initRecyclerView();
                //sendMessageSuccessfully();
                getDatas(0, PAGE_COUNT,list,FETCH_EMAIL_SUCCESS);
            } else if(msg.what == REFRESH_MESSAGE_SUCCESS){
                getDatas(0, PAGE_COUNT,list,REFRESH_EMAIL_SUCCESS);
            }else if(msg.what == FAILED){
                fetchMessageFailed(msg.obj.toString());
            } else if (msg.what == FETCH_EMAIL_SUCCESS){
                if(msg.arg1==0 && msg.arg2==PAGE_COUNT){
                    initRecyclerView();
                } else {
                    updateData();
                }
                isFetchingEmail = false;
                fetchMessageSuccessfully();
            } else if(msg.what == REFRESH_EMAIL_SUCCESS){
                refreshData();
            }
        }
    };
    private void updateData(){
        List<Email> newEmail = new ArrayList<>(list);
        Log.e("newEmail len=",""+newEmail.size());
        if (list.size() > 0) {
            adapter.updateList(newEmail, true);
        } else {
            adapter.updateList(null, false);
        }
    }
    private void refreshData(){
        List<Email> newEmail = new ArrayList<>(list);
        Log.e("newEmail len=",""+newEmail.size());
        if (list.size() > 0) {
            adapter.updateList(newEmail, true);
        } else {
            adapter.updateList(null, false);
        }
        refreshLayout.setRefreshing(false);
    }
    private void fetchMessageSuccessfully() {
        String send = getString(R.string.fetch);
        // TODO : initiate successful logged in experience
        Toast.makeText(getContext(), send, Toast.LENGTH_LONG).show();
    }

    private void fetchMessageFailed(String errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
