package com.njit.android.emailmobileterminal.bean;

public class Email {
    private String addresser;
    private String subject;
    private String date;

    public Email(String addresser, String subject, String date) {
        this.addresser = addresser;
        this.subject = subject;
        this.date = date;
    }

    public String getAddresser() {
        return addresser;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public void setAddresser(String addresser) {
        this.addresser = addresser;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
