package com.njit.android.emailmobileterminal;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
    private String recipients;
    private String subject;
    private String mailContent;
    private String addresser;
    private String password;

    public SendEmail(String recipients,String subject,String mailContent,String addresser,String password){
        this.recipients = recipients;
        this.subject = subject;
        this.mailContent = mailContent;
        this.addresser = addresser;
        this.password = password;
    }

    public void sendMail() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        //Set authorization
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.port", "587");
        MyAuthenticator myauth = new MyAuthenticator(addresser, password);
        Session session = Session.getInstance(props,myauth);
        //Switch on debug
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        InternetAddress fromAddress = null;
        //Sender's email address
        fromAddress = new InternetAddress(addresser);
        message.setFrom(fromAddress);
        //从gmail发送到QQ
        InternetAddress toAddress = new InternetAddress(recipients);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSubject(subject);
        message.setText(mailContent);// 设置邮件内容 Set email content
        //message.setFileName("邮件附件");
        message.saveChanges(); //存储信息 Store messages


        Transport transport = null;
        transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", 587,addresser, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    class MyAuthenticator extends javax.mail.Authenticator {
        private String strUser;
        private String strPwd;

        public MyAuthenticator(String user, String password) {
            this.strUser = user;
            this.strPwd = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(strUser, strPwd);
        }
    }
}
