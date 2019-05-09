/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste;

import domain.GeneralEntity;
import java.util.List;

/**
 *
 * @author user
 */
public interface IDatabaseBroker {
    
    List<GeneralEntity> getAll(GeneralEntity entity) throws Exception;
    GeneralEntity login(GeneralEntity entity) throws Exception;
    GeneralEntity registracija(GeneralEntity entity) throws Exception;
}
