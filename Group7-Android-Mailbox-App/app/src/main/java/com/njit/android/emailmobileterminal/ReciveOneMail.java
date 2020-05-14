package com.njit.android.emailmobileterminal;

import android.annotation.SuppressLint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * Receive emails. Can also receive emails with attachments
 * We can download images etc..
 *
 * */
@SuppressLint("DefaultLocale")
public class ReciveOneMail {

    /**
     * store MIME style message
     */
    private MimeMessage mimeMessage = null;

    /**
     * Storage directory for downloaded attachments
     */
    private String saveAttachPath = "";

    /**
     * Store email content
     */
    private StringBuffer bodytext = new StringBuffer();

    /**
     * default date format
     */
    private String dateformat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

    public ReciveOneMail(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    public void setMimeMessage(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    /**
     * 获得发件人的地址和姓名
     */
    /**
     * Obtain sender's email address and personal name
     * @return fromaddr
     * @throws Exception
     */
    public String getFrom() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        if (from == null)
            from = "";
        String personal = address[0].getPersonal();
        if (personal == null)
            personal = "";
        String fromaddr = personal + "<" + from + ">";
        return fromaddr;
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
     */
    /**
     * Retrieve receiver's email address and personal name according to different parameters:
     * "TO", "CC", and "BCC"
     *
     * @param type
     * @return mailaddr
     * @throws Exception
     */
    @SuppressLint("DefaultLocale")
    public String getMailAddress(String type) throws Exception {
        String mailaddr = "";
        String addtype = type.toUpperCase();
        InternetAddress[] address = null;
        if (addtype.equals("TO") || addtype.equals("CC")
                || addtype.equals("BCC")) {
            if (addtype.equals("TO")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.TO);
            } else if (addtype.equals("CC")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.BCC);
            }
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String email = address[i].getAddress();
                    if (email == null)
                        email = "";
                    else {
                        email = MimeUtility.decodeText(email);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null)
                        personal = "";
                    else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    String compositeto = personal + "<" + email + ">";
                    mailaddr += "," + compositeto;
                }
                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new Exception("Error emailaddr type!");
        }
        return mailaddr;
    }

    /**
     * 获得邮件主题
     */
    /**
     * Retrieve email subject
     *
     * @return subject
     * @throws MessagingException
     */
    public String getSubject() throws MessagingException {
        String subject = "";
        try {
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            if (subject == null)
                subject = "";
        } catch (Exception exce) {
        }
        return subject;
    }

    /**
     * 获得邮件发送日期
     */
    /**
     * Retrieve email sent date
     *
     * @return format.format(sentdate)
     * @throws Exception
     */
    @SuppressLint("SimpleDateFormat")
    public String getSentDate() throws Exception {
        Date sentdate = mimeMessage.getSentDate();
        SimpleDateFormat format = new SimpleDateFormat(dateformat);
        return format.format(sentdate);
    }

    /**
     * 获得邮件正文内容
     */
    /**
     * Retrieve email content
     *
     * @return bodytext.toString()
     */
    public String getBodyText() {
        return bodytext.toString();
    }

    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     */
    /**
     * Parse email according to different MimeTypes.
     * Store the email content to a StringBuffer object.
     *
     * @param part
     * @throws Exception
     */
    public void getMailContent(Part part) throws Exception {
        String contenttype = part.getContentType();
        int nameindex = contenttype.indexOf("name");
        boolean conname = false;
        if (nameindex != -1)
            conname = true;
        System.out.println("CONTENTTYPE: " + contenttype);
        if (part.isMimeType("text/plain") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailContent((Part) part.getContent());
        } else {
        }
    }

    /**
     * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
     */
    /**
     * Check if this email needs a reply sign. If need, then return true. Otherwise return false.
     *
     * @return replysign
     * @throws MessagingException
     */
    public boolean getReplySign() throws MessagingException {
        boolean replysign = false;
        String needreply[] = mimeMessage
                .getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replysign = true;
        }
        return replysign;
    }

    /**
     * 获得此邮件的Message-ID
     */
    /**
     * Retrieve an email's message id
     *
     * @return mimeMessage.getMessageID()
     * @throws MessagingException
     */
    public String getMessageId() throws MessagingException {
        return mimeMessage.getMessageID();
    }

    /**
     * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】pop3协议使用时不能判断。
     */
    /**
     * Check if an email is read. If is not read, then return false, otherwise return true.
     *
     * @return isnew
     * @throws MessagingException
     */
    public boolean isNew() throws MessagingException {

        /**
         * flag for (un)read. isnew is set to be false, so every time when emails show up,
         * they are marked as unread.
         */
        boolean isnew = false;//由于isnew设为false所以每次显示的都为未读

        Flags flags = ((Message) mimeMessage).getFlags();
        System.out.println("--------flags-------" + flags);
        Flags.Flag[] flag = flags.getSystemFlags();
        System.out.println("----flag----" + flag);
        System.out.println("flags's length: " + flag.length);
        for (int i = 0; i < flag.length; i++) {
            System.out.println("flag=======" + flag[i]);
            System.out.println("-=-=-=Flags.Flag.SEEN=-=-=-="+Flags.Flag.SEEN);
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                System.out.println("seen Message.......");
                break;
            }
        }
        return isnew;
    }

    /**
     * 判断此邮件是否包含附件
     */
    /**
     * Check if an email contains attachments
     *
     * @param part
     * @return attachflag
     * @throws Exception
     */
    @SuppressLint("DefaultLocale")
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachflag = false;
        // String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE))))
                    attachflag = true;
                else if (mpart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) mpart);
                } else {
                    String contype = mpart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1)
                        attachflag = true;
                    if (contype.toLowerCase().indexOf("name") != -1)
                        attachflag = true;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent());
        }
        return attachflag;
    }

    /**
     * 【保存附件】
     */
    /**
     * Save attachments
     *
     * @param part
     * @throws Exception
     */
    @SuppressLint("DefaultLocale")
    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);//主体部分得到处理
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE)))) {//ATTACHMENT附件，INLINE嵌入
                    fileName = mpart.getFileName();
                    if (fileName.toLowerCase().indexOf("gb18030") != -1) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mpart.getInputStream());
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart);
                } else {
                    fileName = mpart.getFileName();
                    if ((fileName != null)
                            && (fileName.toLowerCase().indexOf("GB18030") != -1)) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }

    /**
     * 【设置附件存放路径】
     */
    public void setAttachPath(String attachpath) {
        this.saveAttachPath = attachpath;
    }

    /**
     * 【设置日期显示格式】
     */
    public void setDateFormat(String format) throws Exception {
        this.dateformat = format;
    }

    /**
     * 【获得附件存放路径】
     */
    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * 【真正的保存附件到指定目录里】
     */
    /**
     * Save file to designated directory
     *
     * @param fileName
     * @param in
     * @throws Exception
     */
    @SuppressLint("DefaultLocale")
    private void saveFile(String fileName, InputStream in) throws Exception {
        String osName = System.getProperty("os.name");
        System.out.println("----fileName----" + fileName);
        // String storedir = getAttachPath();
//        String separator = "";
        if (osName == null)
            osName = "";
        File storefile = new File(File.separator + "mnt" + File.separator
                + "sdcard" + File.separator + fileName);

        storefile.createNewFile();
        System.out.println("storefile's path: " + storefile.toString());
//         for(int i=0;storefile.exists();i++){
//         storefile = new File(storedir+separator+fileName+i);
//         }

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Failed to save file");
        } finally {
            bos.close();
            bis.close();
        }
    }
}
