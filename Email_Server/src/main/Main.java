/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import connection.SocketConnection;
import controller.UserController;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import model.DataRequest;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SocketConnection connection = new SocketConnection();
        ServerSocket serverSocket = connection.getConnection(HOST, PORT);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, new UserController()).start();
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private static class ClientHandler extends Thread {

        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private UserController userController;

        public ClientHandler(Socket socket, UserController userController) {
            this.socket = socket;
            this.userController = userController;
        }

        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    try {
                        DataRequest dataRequest = (DataRequest) ois.readObject();
                        if (dataRequest != null) {
                            switch (dataRequest.getMethodName()) {
                                case "login":
                                    boolean isLogin = userController.login((User) dataRequest.getData());
                                    oos.writeBoolean(isLogin);
                                    oos.flush();
                                    break;
                                case "getPreviewEmailList":
                                    List<EmailMessage> previewEmailList = userController.getPreviewEmailList((User) dataRequest.getData());
                                    oos.writeObject(previewEmailList);
                                    oos.flush();
                                    break;
                            }
                        }
                    } catch (EOFException e) {
                        System.out.println(socket.getRemoteSocketAddress().toString().split("/")[1] + " disconnected");
                        if (oos != null) {
                            oos.flush();
                            oos.close();
                        }
                        if (ois != null) {
                            ois.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    socket.close();
                    oos.close();
                    ois.close();
                } catch (IOException e) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
