package com.njit.android.emailmobileterminal.ui.sendmail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendMailViewModel extends ViewModel {
    private MutableLiveData<String> edit_text_addresser_value;
    private MutableLiveData<String> edit_text_subject_value;
    private MutableLiveData<String> edit_text_content_value;

    public SendMailViewModel() {
        edit_text_addresser_value = new MutableLiveData<>();
//        edit_text_addresser_value.setValue("");

        edit_text_subject_value = new MutableLiveData<>();
//        edit_text_subject_value.setValue("");

        edit_text_content_value = new MutableLiveData<>();
//        edit_text_content_value.setValue("");

    }

    public MutableLiveData<String> getEdit_text_addresser_value() {
        return edit_text_addresser_value;
    }

    public MutableLiveData<String> getEdit_text_subject_value() {
        return edit_text_subject_value;
    }

    public MutableLiveData<String> getEdit_text_content_value() {
        return edit_text_content_value;
    }
    public void clearEditTextAfterSendMessage(){
        edit_text_addresser_value.postValue("");
        edit_text_subject_value.postValue("");
        edit_text_content_value.postValue("");
    }
}
