/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import javax.mail.Message;
import javax.mail.Session;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public interface IUserDAO {
    
    List<User> getAll();
    
    boolean register(User user);
    
    boolean login(User user);
}
