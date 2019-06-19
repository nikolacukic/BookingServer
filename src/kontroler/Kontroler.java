/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontroler;

import domain.GeneralEntity;
import domain.Klijent;
import domain.Korisnik;
import domain.Smestaj;
import domain.VlasnikSmestaja;
import java.util.List;
import so.ApstraktnaGenerickaOperacija;
import so.login.LoginKSO;
import so.login.LoginVSO;
import so.registracija.RegistracijaKSO;
import so.registracija.RegistracijaVSO;
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

    public GeneralEntity loginK(Korisnik k) throws Exception {
        ApstraktnaGenerickaOperacija so = new LoginKSO();
        so.sablonIzvrsi(k);
        return ((LoginKSO) so).getKlijent();
    }

    public GeneralEntity loginV(VlasnikSmestaja v) throws Exception {
        ApstraktnaGenerickaOperacija so = new LoginVSO();
        so.sablonIzvrsi(v);
        return ((LoginVSO) so).getVlasnik();
    }

    public GeneralEntity registrujK(Klijent k) throws Exception {
        ApstraktnaGenerickaOperacija so = new RegistracijaKSO();
        so.sablonIzvrsi(k);
        return ((RegistracijaKSO) so).getKlijent();
    }

    public GeneralEntity registrujV(VlasnikSmestaja v) throws Exception {
        ApstraktnaGenerickaOperacija so = new RegistracijaVSO();
        so.sablonIzvrsi(v);
        return ((RegistracijaVSO) so).getVlasnik();
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
}
