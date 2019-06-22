/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontroler;

import domain.GeneralEntity;
import domain.Korisnik;
import domain.Ocena;
import domain.Rezervacija;
import domain.Smestaj;
import java.util.List;
import so.ApstraktnaGenerickaOperacija;
import so.login.LoginSO;
import so.ocena.KreirajOcenuSO;
import so.registracija.RegistracijaSO;
import so.rezervacija.KreirajRezervacijuSO;
import so.rezervacija.ObrisiRezervacijuSO;
import so.rezervacija.UcitajRezervacijeSO;
import so.smestaj.IzmeniSmestajSO;
import so.smestaj.KreirajSmestajSO;
import so.smestaj.ObrisiSmestajSO;
import so.smestaj.UcitajSmestajeSO;

/**
 *
 * @author user
 */
public class Kontroler {

    private static Kontroler instance;

    private Kontroler() {
    }

    public static Kontroler getInstance() {
        if (instance == null) {
            instance = new Kontroler();
        }
        return instance;
    }

    public GeneralEntity login(Korisnik k) throws Exception {
        ApstraktnaGenerickaOperacija so = new LoginSO();
        so.sablonIzvrsi(k);
        return ((LoginSO) so).getKorisnik();
    }

    public GeneralEntity registruj(Korisnik k) throws Exception {
        ApstraktnaGenerickaOperacija so = new RegistracijaSO();
        so.sablonIzvrsi(k);
        return ((RegistracijaSO) so).getKorisnik();
    }

    public GeneralEntity kreirajSmestaj(Smestaj s) throws Exception {
        ApstraktnaGenerickaOperacija so = new KreirajSmestajSO();
        so.sablonIzvrsi(s);
        return ((KreirajSmestajSO) so).getSmestaj();
    }

    public List<Smestaj> vratiSveSmestaje(String kriterijum) throws Exception {
        ApstraktnaGenerickaOperacija so = new UcitajSmestajeSO(kriterijum);
        so.sablonIzvrsi(new Smestaj());
        return ((UcitajSmestajeSO) so).getSmestaji();
    }

    public GeneralEntity izmeniSmestaj(Smestaj s) throws Exception {
        ApstraktnaGenerickaOperacija so = new IzmeniSmestajSO();
        so.sablonIzvrsi(s);
        return ((IzmeniSmestajSO) so).getSmestaj();
    }

    public GeneralEntity obrisiSmestaj(Smestaj s) throws Exception {
        ApstraktnaGenerickaOperacija so = new ObrisiSmestajSO();
        so.sablonIzvrsi(s);
        return ((ObrisiSmestajSO) so).getSmestaj();
    }

    public GeneralEntity rezervisi(Rezervacija r) throws Exception {
        ApstraktnaGenerickaOperacija so = new KreirajRezervacijuSO();
        so.sablonIzvrsi(r);
        return ((KreirajRezervacijuSO) so).getRezervacija();
    }

    public GeneralEntity oceni(Ocena o) throws Exception {
        ApstraktnaGenerickaOperacija so = new KreirajOcenuSO();
        so.sablonIzvrsi(o);
        return ((KreirajOcenuSO) so).getOcena();
    }

    public List<Rezervacija> vratiSveRezervacije(Rezervacija rezervacija) throws Exception{
        ApstraktnaGenerickaOperacija so = new UcitajRezervacijeSO();
        so.sablonIzvrsi(rezervacija);
        return ((UcitajRezervacijeSO) so).getRezervacije();
    }

    public GeneralEntity obrisiRezervaciju(Rezervacija r) throws Exception {
        ApstraktnaGenerickaOperacija so = new ObrisiRezervacijuSO();
        so.sablonIzvrsi(r);
        return ((ObrisiRezervacijuSO) so).getRezervacija();
    }
}
