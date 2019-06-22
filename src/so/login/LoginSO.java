/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.login;

import domain.GeneralEntity;
import domain.Korisnik;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class LoginSO extends ApstraktnaGenerickaOperacija{
    private GeneralEntity korisnik;
    
    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Korisnik)) {
            throw new Exception("Nevalidan entity parametar!");
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        korisnik = broker.login((Korisnik) entity);
    }

    public GeneralEntity getKorisnik() {
        return korisnik;
    }
}
