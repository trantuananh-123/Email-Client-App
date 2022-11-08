/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.MessageIDTerm;
import model.EmailMessage;
import model.User;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

/**
 *
 * @author tom18
 */
public class EmailDAO implements IEmailDAO {

    private Statement statement;
    private PreparedStatement preStatement;
    private Connection connection;
    private ResultSet rs;
    private Properties properties = new Properties();

    private List<String> attachFiles = new ArrayList<>();

    public EmailDAO(Connection connection) {
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.starttls.enable", "true");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imaps.sasl.enable", "true");

        this.connection = connection;
        try {
            this.statement = this.connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EmailMessage> getMessage(User user, Integer page, Integer size, Integer type) {
        try {
            String emailType = "";
            if (null != type) {
                switch (type) {
                    case 0:
                        emailType = "Inbox";
                        break;
                    case 1:
                        emailType = "[Gmail]/Có gắn dấu sao";
                        break;
                    case 2:
                        emailType = "[Gmail]/Quan trọng";
                        break;
                    case 3:
                        emailType = "[Gmail]/Thùng rác";
                        break;
                    case 4:
                        emailType = "[Gmail]/Thư nháp";
                        break;
                    case 5:
                        emailType = "[Gmail]/Thư rác";
                        break;
                    case 6:
                        emailType = "[Gmail]/Thư đã gửi";
                        break;
                    case 7:
                        emailType = "[Gmail]/Tất cả thư";
                        break;
                    default:
                        break;
                }
            }
            return getMessage(user, page, size, emailType);
        } catch (Exception ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean sendEmail(User user, EmailMessage emailMessage) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user.getEmail(), user.getPassword());
                }
            });
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailMessage.getFrom()));
            if (emailMessage.getTo() != null) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getTo()));
            }
            if (emailMessage.getCc() != null) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailMessage.getCc()));
            }
            if (emailMessage.getBcc() != null) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailMessage.getBcc()));
            }
            message.setSubject(emailMessage.getSubject());

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(emailMessage.getContent());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (emailMessage.getFile() != null && emailMessage.getFile().size() > 0) {
                for (String filePath : emailMessage.getFile()) {
                    BodyPart messageFilePart = new MimeBodyPart();
                    FileDataSource source = new FileDataSource(filePath);
                    messageFilePart.setDataHandler(new DataHandler(source));
                    messageFilePart.setFileName(MimeUtility.encodeText(source.getName()));
                    multipart.addBodyPart(messageFilePart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);

            return true;

        } catch (AddressException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<EmailMessage> getDetailEmail(User user, String messageId) {
        List<EmailMessage> result = new ArrayList<>();
        try {
            Session session = UserDAO.session;
            Store store;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", user.getEmail(), user.getPassword());

            Folder emailFolder = store.getFolder("[Gmail]/Tất cả thư");
            emailFolder.open(Folder.READ_ONLY);
            MessageIDTerm messageIDTerm = new MessageIDTerm(messageId);
            Message[] messages = emailFolder.search(messageIDTerm);
            for (int i = 0; i < messages.length; i++) {
                attachFiles.clear();
                Message message = messages[i];
                Address[] from = message.getFrom();
                String tos = "";
                Address[] to = message.getRecipients(Message.RecipientType.TO);
                for (Address t : to) {
                    InternetAddress iaTo = (InternetAddress) t;
                    tos += ("<" + iaTo.getAddress() + ">") + ";";
                }
                if (tos.trim().endsWith(";")) {
                    tos = tos.trim().substring(0, tos.length() - 1);
                }
                InternetAddress iaFrom = (InternetAddress) from[0];
                String name = MimeUtility.decodeText(iaFrom.toString()).replaceAll("<" + iaFrom.getAddress() + ">", "");
                String content = getTextFromMessage(message, 1).trim();
                EmailMessage emailMessage = new EmailMessage(messageId, name, message.getSubject(), iaFrom.getAddress(), tos, content);
                emailMessage.setFile(attachFiles);
                result.add(emailMessage);
            }
            emailFolder.close(true);
            store.close();
            return result;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean downloadAttachment(User user, String messageId) {
        List<EmailMessage> result = new ArrayList<>();
        try {
            Session session = UserDAO.session;
            Store store;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", user.getEmail(), user.getPassword());

            Folder emailFolder = store.getFolder("[Gmail]/Tất cả thư");
            emailFolder.open(Folder.READ_ONLY);
            MessageIDTerm messageIDTerm = new MessageIDTerm(messageId);
            Message[] messages = emailFolder.search(messageIDTerm);
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = MimeUtility.decodeText(part.getFileName());
                            part.saveFile("D:/Nam-4/Nam-4/Lap-Trinh-Mang/Email_Client" + File.separator + fileName);
                        }
                    }
                }
            }
            emailFolder.close(true);
            store.close();
            return true;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Boolean deleteMail(User user, String messageId) {
        try {
            Session session = UserDAO.session;
            Store store;
            store = session.getStore("imap");
            store.connect("imap.gmail.com", user.getEmail(), user.getPassword());

            Folder emailFolder = store.getFolder("INBOX");
            Folder trashFolder = store.getFolder("[Gmail]/Thùng rác");
            emailFolder.open(Folder.READ_WRITE);
            trashFolder.open(Folder.READ_WRITE);
            MessageIDTerm messageIDTerm = new MessageIDTerm(messageId);
            Message[] messages = emailFolder.search(messageIDTerm);
            emailFolder.copyMessages(messages, trashFolder);
//            for (int i = 0; i < messages.length; i++) {
//                Message message = messages[i];
//                message.setFlag(Flags.Flag.DELETED, true);
//            }
            emailFolder.expunge();
            emailFolder.close(true);
            store.close();
            return true;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Boolean forwardEmail(User user, EmailMessage emailMessage) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user.getEmail(), user.getPassword());
                }
            });
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(user.getEmail()));
            if (emailMessage.getTo() != null) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getTo()));
            }
            if (emailMessage.getCc() != null) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailMessage.getCc()));
            }
            if (emailMessage.getBcc() != null) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailMessage.getBcc()));
            }
            message.setSubject(emailMessage.getSubject());

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(emailMessage.getContent());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

//            if (emailMessage.getFile() != null && emailMessage.getFile().size() > 0) {
//                for (String filePath : emailMessage.getFile()) {
//                    BodyPart messageFilePart = new MimeBodyPart();
//                    FileDataSource source = new FileDataSource(filePath);
//                    messageFilePart.setDataHandler(new DataHandler(source));
//                    messageFilePart.setFileName(MimeUtility.encodeText(source.getName()));
//                    multipart.addBodyPart(messageFilePart);
//                }
//            }
            message.setContent(multipart);
            Transport.send(message);

            return true;

        } catch (AddressException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Boolean replyEmail(User user, EmailMessage emailMessage) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user.getEmail(), user.getPassword());
                }
            });

            Store store;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", user.getEmail(), user.getPassword());

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            MessageIDTerm messageIDTerm = new MessageIDTerm(emailMessage.getId());
            Message[] messages = emailFolder.search(messageIDTerm);
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                String from = InternetAddress.toString(message.getFrom());
                String replyTo = InternetAddress.toString(message
                        .getReplyTo());
                String to = InternetAddress.toString(message
                        .getRecipients(Message.RecipientType.TO));

                Message replyMessage = new MimeMessage(session);
                replyMessage = (MimeMessage) message.reply(false);
                replyMessage.setFrom(new InternetAddress(to));
                replyMessage.setText("Thanks");
                replyMessage.setReplyTo(message.getReplyTo());

                // Send the message by authenticating the SMTP server
                // Create a Transport instance and call the sendMessage
                Transport t = session.getTransport("smtp");
                try {
                    //connect to the smpt server using transport instance
                    //change the user and password accordingly	
                    t.connect(user.getEmail(), user.getPassword());
                    t.sendMessage(replyMessage,
                            replyMessage.getAllRecipients());
                } finally {
                    t.close();
                }
                System.out.println("message replied successfully ....");

                // close the store and folder objects
                emailFolder.close(false);
                store.close();

            }
            return true;
        } catch (AddressException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private List<EmailMessage> getMessage(User user, Integer page, Integer size, String type) throws Exception {
        Session session = UserDAO.session;
        Store store;
        List<EmailMessage> result = new ArrayList<>();
        if (session != null) {
            try {
                store = session.getStore("imaps");
                store.connect("imap.gmail.com", user.getEmail(), user.getPassword());

                Folder emailFolder = store.getFolder(type);
                emailFolder.open(Folder.READ_ONLY);

                int startIndex = page * size + 1;
                int endIndex = startIndex + size - 1;
                int totalMessage = emailFolder.getMessageCount();
                Message[] messages = emailFolder.getMessages(startIndex, Math.min(endIndex, totalMessage));
//                Message[] messages = emailFolder.getMessages();
                ArrayUtils.reverse(messages);

                for (int i = 0; i < messages.length; i++) {
                    attachFiles.clear();
                    Message message = messages[i];
                    Address[] from = message.getFrom();
                    String tos = "";
                    Address[] to = message.getRecipients(Message.RecipientType.TO);
                    if (to != null && to.length > 0) {
                        for (Address t : to) {
                            InternetAddress iaTo = (InternetAddress) t;
                            tos += ("<" + iaTo.getAddress() + ">") + ";";
                        }
                        if (tos.trim().endsWith(";")) {
                            tos = tos.trim().substring(0, tos.length() - 1);
                        }
                    }
                    InternetAddress iaFrom = (InternetAddress) from[0];
                    String name = MimeUtility.decodeText(iaFrom.toString()).replaceAll("<" + iaFrom.getAddress() + ">", "");
                    String content = getTextFromMessage(message, 0).trim();
                    String messageId = ((MimeMessage) message).getMessageID();
                    EmailMessage emailMessage = new EmailMessage(messageId, name, message.getSubject(), iaFrom.getAddress(), tos, content);
                    result.add(emailMessage);
                }
                emailFolder.close(true);
                store.close();
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException | IOException ex) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    private String getTextFromMessage(Message message, Integer type) throws IOException, MessagingException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = MimeUtility.decodeText(part.getFileName());
                    if (type == 1) {
                        attachFiles.add(fileName);
                    }
//                    part.saveFile("D:/Nam-4/Nam-4/Lap-Trinh-Mang/Email_Client" + File.separator + fileName);
                } else {
                    result = getTextFromMimeMultipart(multiPart);
                }
            }
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            Multipart multipart) throws IOException, MessagingException {

        int count = multipart.getCount();
        if (count == 0) {
            throw new MessagingException("Multipart with no body parts not supported.");
        }
        boolean multipartAlt = new ContentType(multipart.getContentType()).match("multipart/alternative");
        if (multipartAlt) {
            return getTextFromBodyPart(multipart.getBodyPart(count - 1));
        }
        String result = "";
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            result += getTextFromBodyPart(bodyPart);
        }
        return result;
    }

    private String getTextFromBodyPart(
            BodyPart bodyPart) throws IOException, MessagingException {

        String result = "";
        if (bodyPart.isMimeType("text/plain")) {
            result = (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            Document doc = Jsoup.parse(html);
            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            result = doc.body().text().replace("\u00a0", "").replace("\\s+", "");
        } else if (bodyPart.getContent() instanceof MimeMultipart) {
            result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return result;
    }

}
