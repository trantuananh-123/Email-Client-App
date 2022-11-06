/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import connection.SocketConnection;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.LoginView;

/**
 *
 * @author tom18
 */
public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Get conenction to server
        SocketConnection connection = new SocketConnection();
        Socket socket = connection.getConnection(HOST, PORT);
        
        LoginView loginView = new LoginView(socket);
        loginView.setVisible(true);
    }
}
