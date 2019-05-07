/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontroler;

import domain.GeneralEntity;
import domain.Korisnik;
import domain.VlasnikSmestaja;
import so.ApstraktnaGenerickaOperacija;
import so.login.LoginKSO;
import so.login.LoginVSO;

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
}
