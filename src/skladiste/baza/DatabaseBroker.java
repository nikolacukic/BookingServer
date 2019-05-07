/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste.baza;

import domain.GeneralEntity;
import domain.Korisnik;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import skladiste.IDatabaseBroker;
import skladiste.baza.konekcija.KonekcijaSaBazom;

/**
 *
 * @author user
 */
public class DatabaseBroker implements IDatabaseBroker{

    //ovde treba metoda login odnosno treba u IDatabaseBrokeru pa ovde da se implementira, s tim sto su parametri user i pass pa to proveriti pre nego sto se posalje u GeneralEntity.getOne(rs)
    
    @Override
    public List<GeneralEntity> getAll(GeneralEntity entity) throws Exception {
        List<GeneralEntity> list;
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            System.out.println("Uspostavljena je konekcija na bazu");
            String query = "SELECT * FROM " + entity.getTableName();
            System.out.println(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            list = entity.getList(resultSet);
            resultSet.close();
            statement.close();
            return list;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public GeneralEntity login(GeneralEntity entity) throws Exception {
        GeneralEntity entitet;
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            System.out.println("Uspostavljena je konekcija na bazu");
            String query = "SELECT * FROM " + entity.getTableName() + " WHERE korisnicko_ime=" 
                    + ((Korisnik) entity).getKorisnickoIme() + " AND lozinka=" + ((Korisnik) entity).getLozinka();
            System.out.println(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            entitet = entity.getOne(resultSet);
            resultSet.close();
            statement.close();
            return entitet;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
