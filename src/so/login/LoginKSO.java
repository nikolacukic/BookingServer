/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.login;

import domain.GeneralEntity;
import domain.Klijent;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class LoginKSO extends ApstraktnaGenerickaOperacija{

    private GeneralEntity klijent;
    
    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Klijent)) {
            throw new Exception("Nevalidan entity parametar!");
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        klijent = broker.login((Klijent) entity);
    }

    public GeneralEntity getKlijent() {
        return klijent;
    }
   
}
