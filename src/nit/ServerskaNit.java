/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Podesavanja;

/**
 *
 * @author user
 */
public class ServerskaNit extends Thread {

    private LinkedList<KlijentskaNit> listaKorisnika;
    private boolean aktivan = true;
    private ServerSocket ss;

    public ServerskaNit() {
        this.listaKorisnika = new LinkedList<KlijentskaNit>();
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(Integer.parseInt(Podesavanja.getInstance().getProperty("port")));
            System.out.println("Server je pokrenut.");
            while (aktivan) {
                Socket socket = ss.accept();
                System.out.println("Povezan");
                KlijentskaNit clientThread = new KlijentskaNit(socket);
                clientThread.start();
                listaKorisnika.add(clientThread);
            }
            //posalji svim klijentima da se gase
            for (KlijentskaNit nit : listaKorisnika) {
                nit.saljiKraj();
            }
        } catch (IOException ex) {
            System.out.println("Server je ugasen.");
        }
    }

    public void stopServer() {
        try {
            aktivan = false;
            ss.close();
            listaKorisnika.forEach(Thread::interrupt);
        } catch (IOException ex) {
            Logger.getLogger(ServerskaNit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendShutDownMessage() {
        for (KlijentskaNit kn : listaKorisnika) {
            kn.saljiKraj();
        }
    }

}
