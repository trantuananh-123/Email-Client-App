/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author tom18
 */
public class SocketConnection {

    public SocketConnection() {

    }

    public Socket getConnection(String HOST, int PORT) throws IOException {
        return new Socket(HOST, PORT);
    }

}
