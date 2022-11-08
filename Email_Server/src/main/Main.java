/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import connection.SocketConnection;
import controller.EmailController;
import controller.UserController;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
                new ClientHandler(socket, new UserController(), new EmailController()).start();
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
        private EmailController emailController;

        public ClientHandler(Socket socket, UserController userController, EmailController emailController) {
            this.socket = socket;
            this.userController = userController;
            this.emailController = emailController;
        }

        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    try {
                        DataRequest dataRequest = (DataRequest) ois.readObject();
                        if (dataRequest != null) {
                            List<EmailMessage> emailList = new ArrayList<>();
                            switch (dataRequest.getMethodName()) {
                                case "login":
                                    boolean isLogin = userController.login((User) dataRequest.getUser());
                                    oos.writeBoolean(isLogin);
                                    oos.flush();
                                    break;
                                case "getMessage":
                                    emailList = emailController.getMessage((User) dataRequest.getUser(), dataRequest.getPage(), dataRequest.getSize(), dataRequest.getType());
                                    oos.writeObject(emailList);
                                    oos.flush();
                                    break;
                                case "sendEmail":
                                    boolean isSuccess = emailController.sendEmail((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeBoolean(isSuccess);
                                    oos.flush();
                                    break;
                                case "getDetailEmail":
                                    List<EmailMessage> list = emailController.getDetailEmail((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeObject(list);
                                    oos.reset();
                                    oos.flush();
                                    break;
                                case "downloadAttachment":
                                    Boolean isDownloadSuccess = emailController.downloadAttachment((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeBoolean(isDownloadSuccess);
                                    oos.flush();
                                    break;
                                case "deleteMail":
                                    Boolean isDelete = emailController.deleteMail((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeBoolean(isDelete);
                                    oos.flush();
                                    break;
                                case "forwardMail":
                                    Boolean isForward = emailController.forwardEmail((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeBoolean(isForward);
                                    oos.flush();
                                    break;
                                case "replyMail":
                                    Boolean isReply = emailController.replyMail((User) dataRequest.getUser(), (EmailMessage) dataRequest.getData());
                                    oos.writeBoolean(isReply);
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
