/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste.baza;

import domain.GeneralEntity;
import domain.Klijent;
import domain.Korisnik;
import domain.Ocena;
import domain.Rezervacija;
import domain.Smestaj;
import domain.VlasnikSmestaja;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import skladiste.IDatabaseBroker;
import skladiste.baza.konekcija.KonekcijaSaBazom;

/**
 *
 * @author user
 */
public class DatabaseBroker implements IDatabaseBroker {

    @Override
    public List<GeneralEntity> vratiSve(GeneralEntity entity) throws Exception {
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
            String sql = "INSERT INTO " + entity.getTableName() + entity.getColumns() + " VALUES " + entity.getValues();
            PreparedStatement ps = connection.prepareStatement(sql);
            switch (entity.getTableName()) {
                case "klijent":
                    Klijent k = (Klijent) entity;

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

                    ps.setString(1, v.getImePrezime());
                    ps.setString(2, v.getKorisnickoIme());
                    ps.setString(3, v.getLozinka());
                    ps.setString(4, v.getJmbg());
                    ps.setString(5, v.getePosta());
                    ps.setString(6, v.getBrojLicneKarte());
                    ps.setString(7, v.getKontaktTelefon());
                    ps.setDouble(8, v.getOcenaUsluge());
                    ps.executeUpdate();
                    ps.close();
                    return entity;
            }
            return null;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public GeneralEntity kreiraj(GeneralEntity entity) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();

            String sql = "INSERT INTO " + entity.getTableName() + entity.getColumns() + " VALUES " + entity.getValues();
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            switch (entity.getTableName()) {
                case "smestaj":
                    Smestaj s = (Smestaj) entity;
                    ps.setString(1, s.getNazivSmestaja());
                    ps.setDouble(2, s.getCenaPrenocista());
                    ps.setInt(3, s.getBrojKreveta());
                    ps.setDouble(4, s.getProsecnaOcena());
                    ps.setString(5, s.getOpis());
                    ps.setString(6, s.getVlasnik().getKorisnickoIme());
                    ps.executeUpdate();

                    ResultSet rsKeys = ps.getGeneratedKeys();
                    long id = 0;
                    if (rsKeys.next()) {
                        id = rsKeys.getLong(1);
                        s.setSifraSmestaja(id);
                    } else {
                        throw new Exception("Invalid id!");
                    }
                    return s;
                case "rezervacija":
                    Rezervacija r = (Rezervacija) entity;
                    ps.setString(1, r.getKlijent().getKorisnickoIme());
                    ps.setLong(2, r.getSmestaj().getSifraSmestaja());
                    ps.setDate(3, new java.sql.Date(r.getDatumOd().getTime()));
                    ps.setDate(4, new java.sql.Date(r.getDatumDo().getTime()));
                    ps.setDouble(5, r.getUkupanIznos());
                    ps.executeUpdate();

                    return r;
                case "ocena":
                    Ocena o = (Ocena) entity;
                    ps.setLong(1, o.getSmestaj().getSifraSmestaja());
                    ps.setString(2, o.getKlijent().getKorisnickoIme());
                    ps.setInt(3, o.getOcena());
                    ps.setString(4, o.getOpis());
                    ps.executeUpdate();

                    return o;
                default:
                    return null;
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public GeneralEntity pronadjiPoId(GeneralEntity entity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Smestaj> vratiSveSmestaje(String kriterijum) throws Exception {
        List<Smestaj> smestaji = new LinkedList<Smestaj>();
        try {
            String dodatni;
            if (kriterijum == "") {
                dodatni = kriterijum;
            } else {
                try {
                    double br = Double.parseDouble(kriterijum);
                    if (br <= 10) {
                        dodatni = "WHERE s.broj_kreveta = " + br + " OR s.prosecna_ocena BETWEEN " + (br - 0.5) + " AND " + (br + 0.5);
                    } else {
                        dodatni = "WHERE s.cena_prenocista < " + br;
                    }
                } catch (NumberFormatException ex) {
                    dodatni = "WHERE s.naziv_smestaja LIKE '%" + kriterijum + "%' OR s.opis LIKE '%" + kriterijum + "%'";
                }
            }
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String query = "select s.sifra_smestaja, s.naziv_smestaja, s.cena_prenocista, s.broj_kreveta, s.prosecna_ocena, s.opis,"
                    + " v.korisnicko_ime as username, v.lozinka as lozinka, v.ime_prezime as imePrezime, v.jmbg as jmbg, v.e_posta as ePosta,"
                    + " v.broj_lk as brLicne, v.ocena_usluge as ocena, v.kontakt_telefon as telefon "
                    + "from smestaj s "
                    + "inner join vlasnik_smestaja v on s.vlasnik_id = v.korisnicko_ime " + dodatni;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Long id = resultSet.getLong("sifra_smestaja");
                String naziv = resultSet.getString("naziv_smestaja");
                double cena = resultSet.getDouble("cena_prenocista");
                int kreveti = resultSet.getInt("broj_kreveta");
                double ocena = resultSet.getDouble("prosecna_ocena");
                String opis = resultSet.getString("opis");

                String username = resultSet.getString("username");
                String lozinka = resultSet.getString("lozinka");
                String ime = resultSet.getString("imePrezime");
                String jmbg = resultSet.getString("jmbg");
                String ePosta = resultSet.getString("ePosta");
                String brlk = resultSet.getString("brLicne");
                double ocenaU = resultSet.getDouble("ocena");
                String telefon = resultSet.getString("telefon");

                VlasnikSmestaja v = new VlasnikSmestaja(brlk, telefon, ocenaU, username, lozinka, ime, jmbg, ePosta);

                Smestaj s = new Smestaj(id, naziv, kreveti, cena, opis, ocena);
                s.setVlasnik(v);

                smestaji.add(s);
            }
            resultSet.close();
            statement.close();

            return smestaji;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public GeneralEntity izmeni(GeneralEntity entity) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            Smestaj smestaj = (Smestaj) entity;
            String sql = "UPDATE " + entity.getTableName() + " SET naziv_smestaja = '"
                    + smestaj.getNazivSmestaja() + "', broj_kreveta = " + smestaj.getBrojKreveta()
                    + ", cena_prenocista = " + smestaj.getCenaPrenocista() + ", opis = '" + smestaj.getOpis()
                    + "' WHERE sifra_smestaja = " + smestaj.getSifraSmestaja();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return smestaj;

        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public GeneralEntity obrisi(GeneralEntity entity) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String idName = entity.getIdName();
            String sql = "";
            if (!idName.equals("slozen")) {
                try {
                    int a = Integer.parseInt(entity.getId());
                    sql = "DELETE FROM " + entity.getTableName() + " WHERE " + idName + " = " + a;
                } catch (NumberFormatException ex) {
                    sql = "DELETE FROM " + entity.getTableName() + " WHERE " + idName + " = '" + entity.getId() + "'";
                } finally {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(sql);
                }
            } else {
                switch (entity.getTableName()) {
                    case "ocena":
                        /*sql = "DELETE FROM " + entity.getTableName() + " WHERE " + entity.getIdName() + " = " + entity.getId();
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(sql);*/
                        break;
                    case "rezervacija":
                        break;
                    default:
                        break;
                }
            }
            return entity;
        } catch (SQLException ex) {
            throw ex;
        }
    }

}
