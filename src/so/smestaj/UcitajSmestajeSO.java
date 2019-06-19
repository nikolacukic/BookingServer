/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.smestaj;

import domain.Smestaj;
import java.util.List;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class UcitajSmestajeSO extends ApstraktnaGenerickaOperacija{

    List<Smestaj> smestaji;
    String kriterijum;

    public UcitajSmestajeSO(String kriterijum) {
        this.kriterijum = kriterijum;
    }
    
    @Override
    protected void validacija(Object entity) throws Exception {
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        smestaji = broker.vratiSveSmestaje(kriterijum);
    }

    public List<Smestaj> getSmestaji() {
        return smestaji;
    }
    
}
