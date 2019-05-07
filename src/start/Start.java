/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import nit.KlijentskaNit;
import util.Podesavanja;

/**
 *
 * @author user
 */
public class Start {

    private boolean aktivan = true;

    public void startServer() throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(Integer.parseInt(Podesavanja.getInstance().getProperty("port")));
        System.out.println("Server je pokrenut");
        while (aktivan) {
            Socket socket = ss.accept();
            System.out.println("Povezan");
            KlijentskaNit clientThread = new KlijentskaNit(socket);
            clientThread.start();
        }

    }

    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Start().startServer();
    }

}
