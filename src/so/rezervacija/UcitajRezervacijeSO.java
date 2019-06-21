/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.rezervacija;

import domain.Rezervacija;
import java.util.List;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class UcitajRezervacijeSO extends ApstraktnaGenerickaOperacija{

    private List<Rezervacija> rezervacije;
    
    @Override
    protected void validacija(Object entity) throws Exception {
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        rezervacije = broker.vratiSveRezervacije((Rezervacija) entity);
    }

    public List<Rezervacija> getRezervacije() {
        return rezervacije;
    }
    
    
    
}
