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
public class KreirajSmestajSO extends ApstraktnaGenerickaOperacija {

    private GeneralEntity smestaj;

    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Smestaj)) {
            throw new Exception("Nevalidan entity parametar!");
        }
        Smestaj s = (Smestaj) entity;
        if (s.getBrojKreveta() < 1) {
            throw new Exception("Broj kreveta ne sme biti manji od 1");
        }
        if (s.getCenaPrenocista() < 0) {
            throw new Exception("Cena smestaja ne sme biti negativna");
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        smestaj = broker.kreiraj((Smestaj) entity);
    }

    public GeneralEntity getSmestaj() {
        return smestaj;
    }

}
