/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import connection.DBConnection;
import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public class UserController {

    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    public List<User> getAll() {
        return userDAO.getAll();
    }

    public boolean register(User user) {
        return userDAO.register(user);
    }

    public boolean login(User user) {
        return userDAO.login(user);
    }

}
