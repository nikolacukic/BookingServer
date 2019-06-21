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
public class KreirajRezervacijuSO extends ApstraktnaGenerickaOperacija {

    private GeneralEntity rezervacija;

    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Rezervacija)) {
            throw new Exception("Nevalidan entity parametar!");
        }
        Rezervacija r = (Rezervacija) entity;
        if (r.getDatumOd() == null || r.getDatumDo() == null) {
            throw new Exception("Morate uneti datume dolaska i odlaska!");
        }
        if (r.getDatumOd().compareTo(r.getDatumDo()) >= 0) {
            throw new Exception("Datum odlaska mora biti posle datuma dolaska!");
        }
        if (r.getKlijent().getStanjeNaRacunu() < r.getUkupanIznos()) {
            throw new Exception("Nemate dovoljno sredstava na racunu! Vase stanje: " + r.getKlijent().getStanjeNaRacunu());
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        rezervacija = broker.kreiraj((Rezervacija) entity);
    }

    public GeneralEntity getRezervacija() {
        return rezervacija;
    }

}
