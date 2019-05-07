/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.login;

import domain.GeneralEntity;
import domain.VlasnikSmestaja;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class LoginVSO extends ApstraktnaGenerickaOperacija{
    private GeneralEntity vlasnik;
    
    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof VlasnikSmestaja)) {
            throw new Exception("Nevalidan entity parametar!");
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        vlasnik = broker.login((VlasnikSmestaja) entity);
    }

    public GeneralEntity getVlasnik() {
        return vlasnik;
    }
   
}
