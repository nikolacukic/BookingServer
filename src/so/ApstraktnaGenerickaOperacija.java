/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import skladiste.IDatabaseBroker;
import skladiste.baza.DatabaseBroker;
import skladiste.baza.konekcija.KonekcijaSaBazom;

/**
 *
 * @author user
 */
public abstract class ApstraktnaGenerickaOperacija {
    
    protected IDatabaseBroker broker;

    public ApstraktnaGenerickaOperacija() {
        this.broker = new DatabaseBroker();
    }

    public final void sablonIzvrsi(Object entity) throws Exception {
        try {
            validacija(entity);
            zapocniTransakciju();
            izvrsi(entity);
            commitujTransakciju();
        } catch (Exception ex) {
            ex.printStackTrace();
            rollbackujTransakciju();
            throw ex;
        }
    }

    protected abstract void validacija(Object entity) throws Exception;

    protected abstract void izvrsi(Object entity) throws Exception;

    private void zapocniTransakciju() throws Exception {
        KonekcijaSaBazom.getInstance().getConnection().setAutoCommit(false);
    }

    private void commitujTransakciju() throws Exception {
        KonekcijaSaBazom.getInstance().commit();
    }

    private void rollbackujTransakciju() throws Exception {
        KonekcijaSaBazom.getInstance().rollback();
    }

}
