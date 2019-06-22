/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nit;

import domain.GeneralEntity;
import domain.Klijent;
import domain.Korisnik;
import domain.Ocena;
import domain.Rezervacija;
import domain.Smestaj;
import domain.VlasnikSmestaja;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
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
                        GeneralEntity korisnik = Kontroler.getInstance().login(dataKorisnik);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(korisnik);
                        break;
                    case Operacije.REGISTRACIJA:
                        Korisnik novi = (Korisnik) zahtev.getPodaci();
                        GeneralEntity klijent = Kontroler.getInstance().registruj((Korisnik) novi);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(klijent);
                        break;
                    case Operacije.SMESTAJ_KREIRANJE:
                        Smestaj s = (Smestaj) zahtev.getPodaci();
                        GeneralEntity smestaj = Kontroler.getInstance().kreirajSmestaj(s);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(smestaj);
                        break;
                    case Operacije.SMESTAJ_UCITAVANJE:
                        List<Smestaj> smestaji = Kontroler.getInstance().vratiSveSmestaje((String) zahtev.getPodaci());
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(smestaji);
                        break;
                    case Operacije.SMESTAJ_IZMENA:
                        Smestaj izmenjen = (Smestaj) zahtev.getPodaci();
                        GeneralEntity nov = Kontroler.getInstance().izmeniSmestaj(izmenjen);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(nov);
                        break;
                    case Operacije.SMESTAJ_BRISANJE:
                        Smestaj za_brisanje = (Smestaj) zahtev.getPodaci();
                        GeneralEntity deleted = Kontroler.getInstance().obrisiSmestaj(za_brisanje);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(deleted);
                        break;
                    case Operacije.REZERVACIJA_KREIRANJE:
                        Rezervacija r = (Rezervacija) zahtev.getPodaci();
                        GeneralEntity rez = Kontroler.getInstance().rezervisi(r);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(rez);
                        break;
                    case Operacije.OCENA_KREIRANJE:
                        Ocena o = (Ocena) zahtev.getPodaci();
                        GeneralEntity nova = Kontroler.getInstance().oceni(o);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(nova);
                        break;
                    case Operacije.REZERVACIJA_UCITAVANJE:
                        List<Rezervacija> rezervacije = Kontroler.getInstance().vratiSveRezervacije((Rezervacija) zahtev.getPodaci());
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(rezervacije);
                        break;
                    case Operacije.REZERVACIJA_BRISANJE:
                        Rezervacija otkaz = (Rezervacija) zahtev.getPodaci();
                        GeneralEntity otkazana = Kontroler.getInstance().obrisiRezervaciju(otkaz);
                        odgovor.setStatus(StatusOdgovora.OK);
                        odgovor.setPodaci(otkazana);
                        break;
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

    public void saljiKraj() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(soket.getOutputStream());
            out.writeObject(new Zahtev(Operacije.GASENJE, null));
        } catch (IOException ex) {
            Logger.getLogger(KlijentskaNit.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(KlijentskaNit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
