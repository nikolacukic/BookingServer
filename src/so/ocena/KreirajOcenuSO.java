/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.ocena;

import domain.GeneralEntity;
import domain.Ocena;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class KreirajOcenuSO extends ApstraktnaGenerickaOperacija{

    private GeneralEntity ocena;
    
    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Ocena)) {
            throw new Exception("Nevalidan entity parametar!");
        }
        Ocena o = (Ocena) entity;
        if (o.getOcena() < 1 || o.getOcena() > 5) {
            throw new Exception("Ocena smestaja mora biti izmedju 1 i 5");
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        ocena = broker.kreiraj((Ocena) entity);
    }

    public GeneralEntity getOcena() {
        return ocena;
    }
    
    
    
}
