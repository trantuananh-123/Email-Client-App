/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 *
 * @author tom18
 */
public class SocketConnection {

    ServerSocket serverSocket = null;
    InetAddress ip = null;

    public SocketConnection() {
    }

    public ServerSocket getConnection(String HOST, int PORT) throws IOException {
        return new ServerSocket(PORT);
    }

}
