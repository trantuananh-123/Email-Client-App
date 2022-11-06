/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import model.EmailMessage;
import model.User;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author tom18
 */
public class UserDAO implements IUserDAO {

    public static Session session;

    private Statement statement;
    private PreparedStatement preStatement;
    private Connection connection;
    private ResultSet rs;
    private Properties properties = new Properties();

    public UserDAO(Connection connection) {
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "25");

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
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM sys_user";
        try {
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                User user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDate(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getTimestamp(8).toLocalDateTime(),
                        rs.getTimestamp(9).toLocalDateTime());
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userList;
    }

    @Override
    public boolean register(User user) {
        String sql = "INSERT INTO sys_user "
                + "(email, "
                + "password, "
                + "name, "
                + "create_date) "
                + "VALUES (?, ?, ?, ?)";
        try {
            this.preStatement = this.connection.prepareStatement(sql);
            this.preStatement.setString(1, user.getEmail());
            this.preStatement.setString(2, user.getPassword());
            this.preStatement.setString(3, user.getName());
            this.preStatement.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            int rowCount = this.preStatement.executeUpdate();

            return rowCount != 0;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(User user) {
        try {
            Session emailSession = Session.getInstance(properties,
                    new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user.getEmail(), user.getPassword());
                }
            });
            if (emailSession != null) {
                Store store = emailSession.getStore("imaps");
                store.connect("imap.gmail.com", user.getEmail(), user.getPassword());
                if (store.isConnected()) {
                    session = emailSession;
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
