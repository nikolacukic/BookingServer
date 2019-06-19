/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste;

import domain.GeneralEntity;
import domain.Smestaj;
import java.util.List;

/**
 *
 * @author user
 */
public interface IDatabaseBroker {
    
    public List<GeneralEntity> vratiSve(GeneralEntity entity) throws Exception;
    public GeneralEntity login(GeneralEntity entity) throws Exception;
    public GeneralEntity registracija(GeneralEntity entity) throws Exception;
    public GeneralEntity kreiraj(GeneralEntity entity) throws Exception;
    public GeneralEntity pronadjiPoId(GeneralEntity entity) throws Exception;
    public List<Smestaj> vratiSveSmestaje(String kriterijum) throws Exception;
    public GeneralEntity izmeni(GeneralEntity entity) throws Exception;
    public GeneralEntity obrisi(GeneralEntity entity) throws Exception;
}
