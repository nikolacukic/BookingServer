/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.smestaj;

import domain.GeneralEntity;
import domain.Smestaj;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class ObrisiSmestajSO extends ApstraktnaGenerickaOperacija{

    private GeneralEntity s;
    
    @Override
    protected void validacija(Object entity) throws Exception {
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        s = broker.obrisi((Smestaj) entity);
    }

    public GeneralEntity getSmestaj() {
        return s;
    }
   
}
