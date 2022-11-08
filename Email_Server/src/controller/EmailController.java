/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import connection.DBConnection;
import dao.EmailDAO;
import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public class EmailController {

    private EmailDAO emailDAO;

    public EmailController() {
        emailDAO = new EmailDAO();
    }

    public List<EmailMessage> getMessage(User user, Integer page, Integer size, Integer type) {
        return emailDAO.getMessage(user, page, size, type);

    }

    public boolean sendEmail(User user, EmailMessage emailMessage) {
        return emailDAO.sendEmail(user, emailMessage);
    }

    public List<EmailMessage> getDetailEmail(User user, EmailMessage emailMessage) {
        return emailDAO.getDetailEmail(user, emailMessage.getId());
    }

    public Boolean downloadAttachment(User user, EmailMessage emailMessage) {
        return emailDAO.downloadAttachment(user, emailMessage.getId());
    }

    public Boolean deleteMail(User user, EmailMessage emailMessage) {
        return emailDAO.deleteMail(user, emailMessage.getId());
    }

    public Boolean forwardEmail(User user, EmailMessage emailMessage) {
        return emailDAO.forwardEmail(user, emailMessage);
    }

    public Boolean replyMail(User user, EmailMessage emailMessage) {
        return emailDAO.replyEmail(user, emailMessage);
    }

}
