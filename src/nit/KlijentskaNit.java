/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nit;

import domain.GeneralEntity;
import domain.Klijent;
import domain.Korisnik;
import domain.VlasnikSmestaja;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import kontroler.Kontroler;
import transfer.Odgovor;
import transfer.Zahtev;
import transfer.util.Operacije;
import transfer.util.StatusOdgovora;

/**
 *
 * @author user
 */
public class KlijentskaNit extends Thread {

    private Socket soket;

    public KlijentskaNit(Socket socket) {
        this.soket = socket;
    }

    @Override
    public void run() {
        try {
            obradiZahtev();
        } catch (IOException ex) {
            //Logger.getLogger(KlijentskaNit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(KlijentskaNit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradiZahtev() throws IOException, ClassNotFoundException {
        while (!isInterrupted()) {
            ObjectInputStream inSocket = new ObjectInputStream(soket.getInputStream());
            Zahtev zahtev = (Zahtev) inSocket.readObject();
            Odgovor odgovor = new Odgovor();
            try {
                int operacija = zahtev.getOperacija();
                System.out.println("Operation: " + operacija);
                switch (operacija) {
                    case Operacije.LOGIN:
                        Korisnik dataKorisnik = (Korisnik) zahtev.getPodaci();
                        if (dataKorisnik instanceof Klijent) {
                            GeneralEntity klijent = Kontroler.getInstance().loginK((Klijent) dataKorisnik);
                            odgovor.setStatus(StatusOdgovora.OK);
                            odgovor.setPodaci(klijent);
                            break;
                        } else {
                            GeneralEntity vlasnik = Kontroler.getInstance().loginV((VlasnikSmestaja) dataKorisnik);
                            odgovor.setStatus(StatusOdgovora.OK);
                            odgovor.setPodaci(vlasnik);
                            break;
                        }
                    case Operacije.REGISTRACIJA:
                        Korisnik novi = (Korisnik) zahtev.getPodaci();
                        if (novi instanceof Klijent) {
                            GeneralEntity klijent = Kontroler.getInstance().registrujK((Klijent) novi);
                            odgovor.setStatus(StatusOdgovora.OK);
                            odgovor.setPodaci(klijent);
                            break;
                        } else {
                            GeneralEntity vlasnik = Kontroler.getInstance().registrujV((VlasnikSmestaja) novi);
                            odgovor.setStatus(StatusOdgovora.OK);
                            odgovor.setPodaci(vlasnik);
                            break;
                        }
                    /*case Operacije.OPERATION_GET_ALL_PRODUCTS:
                        List<Product> products = Controler.getInstance().getAllProducts();
                        odgovor.setStatus(ResponseStatus.OK);
                        odgovor.setData(products);*/
                }

            } catch (Exception ex) {
                odgovor.setStatus(StatusOdgovora.ERROR);
                odgovor.setError(ex);
            }
            vratiOdgovor(odgovor);
        }
    }

    private void vratiOdgovor(Odgovor odgovor) throws IOException {
        ObjectOutputStream outSocket = new ObjectOutputStream(soket.getOutputStream());
        outSocket.writeObject(odgovor);
    }

}
