/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste.baza;

import domain.GeneralEntity;
import domain.Klijent;
import domain.Korisnik;
import domain.VlasnikSmestaja;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class DatabaseBroker implements IDatabaseBroker {
    
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
            String query = "SELECT * FROM " + entity.getTableName() + " WHERE korisnicko_ime='"
                    + ((Korisnik) entity).getKorisnickoIme() + "' AND lozinka='"
                    + ((Korisnik) entity).getLozinka() + "'";
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
    
    @Override
    public GeneralEntity registracija(GeneralEntity entity) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            switch (entity.getTableName()) {
                case "klijent":
                    Klijent k = (Klijent) entity;
                    String sql = "INSERT INTO " + entity.getTableName() + " (ime_prezime, korisnicko_ime, lozinka, jmbg, e_posta, broj_odsedanja, stanje_na_racunu) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1, k.getImePrezime());
                    ps.setString(2, k.getKorisnickoIme());
                    ps.setString(3, k.getLozinka());
                    ps.setString(4, k.getJmbg());
                    ps.setString(5, k.getePosta());
                    ps.setInt(6, k.getBrojOdsedanja());
                    ps.setDouble(7, k.getStanjeNaRacunu());
                    ps.executeUpdate();
                    ps.close();
                    return entity;
                case "vlasnik_smestaja":
                    VlasnikSmestaja v = (VlasnikSmestaja) entity;
                    String sql2 = "INSERT INTO " + entity.getTableName() + " (ime_prezime, korisnicko_ime, lozinka, jmbg, e_posta, broj_lk, kontakt_telefon, ocena_usluge) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps2 = connection.prepareStatement(sql2);
                    ps2.setString(1, v.getImePrezime());
                    ps2.setString(2, v.getKorisnickoIme());
                    ps2.setString(3, v.getLozinka());
                    ps2.setString(4, v.getJmbg());
                    ps2.setString(5, v.getePosta());
                    ps2.setString(6, v.getBrojLicneKarte());
                    ps2.setString(7, v.getKontaktTelefon());
                    ps2.setDouble(8, v.getOcenaUsluge());
                    ps2.executeUpdate();
                    ps2.close();
                    return entity;
            }
            return null;
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
}
