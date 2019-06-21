/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.rezervacija;

import domain.GeneralEntity;
import domain.Rezervacija;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class ObrisiRezervacijuSO extends ApstraktnaGenerickaOperacija {

    private GeneralEntity rezervacija;
    
    @Override
    protected void validacija(Object entity) throws Exception {
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        rezervacija = broker.obrisi((Rezervacija) entity);
    }

    public GeneralEntity getRezervacija() {
        return rezervacija;
    }
    
}
