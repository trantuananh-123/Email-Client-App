/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public interface IEmailDAO {

    List<EmailMessage> getMessage(User user, Integer page, Integer size, Integer type);

    Boolean sendEmail(User user, EmailMessage emailMessage);

    List<EmailMessage> getDetailEmail(User user, String messageId);

    Boolean downloadAttachment(User user, String messageId);

    Boolean deleteMail(User user, String messageId);

    Boolean forwardEmail(User user, EmailMessage emailMessage);

    Boolean replyEmail(User user, EmailMessage emailMessage);
}
