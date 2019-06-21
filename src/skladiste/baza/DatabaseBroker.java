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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            if (entitet instanceof Klijent) {
                Klijent k = (Klijent) entitet;
                k.setOcene(vratiSveOceneZaKorisnika(k, connection));
                k.setRezervacije(vratiSveRezZaKorisnika(k, connection));
            }
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
                    ps.setDouble(6, k.getStanjeNaRacunu());
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
                    ps.setLong(1, r.getSmestaj().getSifraSmestaja());
                    ps.setString(2, r.getKlijent().getKorisnickoIme());
                    ps.setDate(3, new java.sql.Date(r.getDatumOd().getTime()));
                    ps.setDate(4, new java.sql.Date(r.getDatumDo().getTime()));
                    ps.setDouble(5, r.getUkupanIznos());
                    ps.executeUpdate();
                    skiniPareSaRacuna(r.getKlijent(), r.getUkupanIznos());
                    return r;
                case "ocena":
                    Ocena o = (Ocena) entity;
                    ps.setLong(1, o.getSmestaj().getSifraSmestaja());
                    ps.setString(2, o.getKlijent().getKorisnickoIme());
                    ps.setInt(3, o.getOcena());
                    ps.setString(4, o.getOpis());
                    ps.executeUpdate();
                    azurirajProsecnuOcenu(o.getSmestaj());
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
        GeneralEntity entitet;
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String query;
            if (entity instanceof Korisnik) {
                query = "SELECT * FROM " + entity.getTableName() + " WHERE korisnicko_ime='"
                        + ((Korisnik) entity).getKorisnickoIme() + "'";
            } else {
                query = "SELECT * FROM " + entity.getTableName() + " WHERE sifra_smestaja="
                        + ((Smestaj) entity).getSifraSmestaja();
            }
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
                    + " v.korisnicko_ime as username "
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

                Korisnik k1 = new VlasnikSmestaja();
                k1.setKorisnickoIme(username);
                VlasnikSmestaja v = (VlasnikSmestaja) pronadjiPoId(k1);

                Smestaj s = new Smestaj(id, naziv, kreveti, cena, opis, ocena);
                s.setVlasnik(v);
                smestaji.add(s);
            }
            resultSet.close();
            statement.close();

            for (Smestaj s : smestaji) {
                s.setRezervacije(vratiRezZaSmestaj(s, connection));
                s.setOcene(vratiOceneZaSmestaj(s, connection));
            }

            return smestaji;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<Rezervacija> vratiRezZaSmestaj(Smestaj s, Connection kon) throws Exception {
        try {
            Connection connection = kon;
            List<Rezervacija> rezervacije = new LinkedList<Rezervacija>();
            String query = "SELECT sifra_smestaja, klijent_id, datum_od, datum_do, ukupan_iznos FROM rezervacija WHERE sifra_smestaja = " + s.getSifraSmestaja();
            Statement statement = kon.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String klijent = resultSet.getString("klijent_id");
                java.util.Date datumOd = new java.util.Date(resultSet.getDate("datum_od").getTime());
                java.util.Date datumDo = new java.util.Date(resultSet.getDate("datum_do").getTime());
                double ukupanIznos = resultSet.getDouble("ukupan_iznos");
                Korisnik k1 = new Klijent();
                k1.setKorisnickoIme(klijent);
                Klijent k = (Klijent) pronadjiPoId(k1);

                rezervacije.add(new Rezervacija(s, k, datumOd, datumDo, ukupanIznos));
            }
            resultSet.close();
            statement.close();
            return rezervacije;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<Ocena> vratiOceneZaSmestaj(Smestaj s, Connection kon) throws Exception {
        try {
            Connection connection = kon;
            List<Ocena> ocene = new LinkedList<Ocena>();
            String query = "SELECT smestaj_id, klijent_id, konacna_ocena, opis FROM ocena WHERE smestaj_id = " + s.getSifraSmestaja();
            Statement statement = kon.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String klijent = resultSet.getString("klijent_id");
                int ocena = resultSet.getInt("konacna_ocena");
                String opis = resultSet.getString("opis");
                Korisnik k1 = new Klijent();
                k1.setKorisnickoIme(klijent);
                Klijent k = (Klijent) pronadjiPoId(k1);

                ocene.add(new Ocena(k, s, ocena, opis));
            }
            resultSet.close();
            statement.close();
            return ocene;
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
                        Rezervacija r = (Rezervacija) entity;
                        java.sql.Date datum = new java.sql.Date(r.getDatumOd().getTime());
                        sql = "DELETE FROM " + entity.getTableName() + " WHERE sifra_smestaja = " + r.getSmestaj().getSifraSmestaja()
                                + " AND klijent_id = '" + r.getKlijent().getKorisnickoIme() + "' AND datum_od = '" + datum + "'";
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(sql);
                        vratiPareNaRacun(r.getKlijent(), r.getUkupanIznos());
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

    private void skiniPareSaRacuna(Klijent klijent, double iznos) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String sql = "UPDATE " + klijent.getTableName()
                    + " SET stanje_na_racunu = stanje_na_racunu - " + iznos
                    + " WHERE korisnicko_ime = '" + klijent.getKorisnickoIme() + "'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    private void vratiPareNaRacun(Klijent klijent, double iznos) throws Exception {
        try {
            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String sql = "UPDATE " + klijent.getTableName()
                    + " SET stanje_na_racunu = stanje_na_racunu + " + iznos
                    + " WHERE korisnicko_ime = '" + klijent.getKorisnickoIme() + "'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException ex) {
            throw ex;
        }
    }

    private List<Ocena> vratiSveOceneZaKorisnika(Klijent k, Connection connection) throws Exception {
        try {
            Connection conn = connection;
            List<Ocena> ocene = new LinkedList<Ocena>();
            String query = "SELECT smestaj_id, klijent_id, konacna_ocena, opis FROM ocena WHERE klijent_id = '" + k.getKorisnickoIme() + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                long sifra = resultSet.getLong("smestaj_id");
                int ocena = resultSet.getInt("konacna_ocena");
                String opis = resultSet.getString("opis");
                Smestaj s = new Smestaj();
                s.setSifraSmestaja(sifra);
                s = (Smestaj) pronadjiPoId(s);

                ocene.add(new Ocena(k, s, ocena, opis));
            }
            resultSet.close();
            statement.close();
            return ocene;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<Rezervacija> vratiSveRezZaKorisnika(Klijent k, Connection connection) throws Exception {
        try {
            Connection conn = connection;
            List<Rezervacija> rezervacije = new LinkedList<Rezervacija>();
            String query = "SELECT sifra_smestaja, klijent_id, datum_od, datum_do, ukupan_iznos FROM rezervacija WHERE klijent_id = '" + k.getKorisnickoIme() + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                long sifra = resultSet.getLong("sifra_smestaja");
                java.util.Date datumOd = new java.util.Date(resultSet.getDate("datum_od").getTime());
                java.util.Date datumDo = new java.util.Date(resultSet.getDate("datum_do").getTime());
                double ukupanIznos = resultSet.getDouble("ukupan_iznos");
                Smestaj s = new Smestaj();
                s.setSifraSmestaja(sifra);
                s = (Smestaj) pronadjiPoId(s);

                rezervacije.add(new Rezervacija(s, k, datumOd, datumDo, ukupanIznos));
            }
            resultSet.close();
            statement.close();
            return rezervacije;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void azurirajProsecnuOcenu(Smestaj s) throws Exception {
        try {
            Connection kon = KonekcijaSaBazom.getInstance().getConnection();
            List<Ocena> lista = vratiOceneZaSmestaj(s, kon);
            int suma = 0;
            for (Ocena o : lista) {
                suma += o.getOcena();
            }
            double prosek = (double) suma / lista.size();
            String sql = "UPDATE " + s.getTableName()
                    + " SET prosecna_ocena =  " + prosek
                    + " WHERE sifra_smestaja = " + s.getSifraSmestaja();
            Statement statement = kon.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public List<Rezervacija> vratiSveRezervacije(GeneralEntity entity) throws Exception {
        List<Rezervacija> rezervacije = new LinkedList<Rezervacija>();
        try {
            String dodatni="";
            Rezervacija dobijena = (Rezervacija) entity;
            String id_korisnika = dobijena.getKlijent().getKorisnickoIme();
            //DEO KAD VLASNIK POZIVA
            if (dobijena.getKlijent().getImePrezime() == null) {
                //kriterijum je smesten u lozinku
                //ima i kriterijum i datum
                if (dobijena.getKlijent().getLozinka() != null && dobijena.getDatumOd() != null) {
                    String pattern = "yyyy-MM-dd";
                    String kriterijum = dobijena.getKlijent().getLozinka();
                    DateFormat df = new SimpleDateFormat(pattern);

                    String datum = df.format(dobijena.getDatumOd());
                    dodatni = "WHERE s.vlasnik_id = '" + id_korisnika + "' AND r.datum_od <= '" + datum + "' AND r.datum_do >= '" + datum + "' AND s.naziv_smestaja LIKE '%" + kriterijum + "%'";
                }
                //ima samo datum
                else if(dobijena.getKlijent().getLozinka() == null && dobijena.getDatumOd() != null){
                    String pattern = "yyyy-MM-dd";
                    DateFormat df = new SimpleDateFormat(pattern);

                    String datum = df.format(dobijena.getDatumOd());
                    dodatni = "WHERE s.vlasnik_id = '" + id_korisnika + "' AND r.datum_od <= '" + datum + "' AND r.datum_do >= '" + datum + "'";
                }
                //ima samo kriterijum
                else if(dobijena.getKlijent().getLozinka() != null && dobijena.getDatumOd() == null){
                    String kriterijum = dobijena.getKlijent().getLozinka();
                    dodatni = "WHERE s.vlasnik_id = '" + id_korisnika + "' AND s.naziv_smestaja LIKE '%" + kriterijum + "%'";
                }
                else {
                    dodatni = "WHERE s.vlasnik_id = '" + id_korisnika + "'";
                }
            } else {
                String lozinka = dobijena.getKlijent().getLozinka();
                boolean jesteLozinka= lozinka.matches((".*\\d.*"));
               //DEO KAD KLIJENT POZIVA
               if ((lozinka != null && !jesteLozinka) && dobijena.getDatumOd() != null) {
                    String pattern = "yyyy-MM-dd";
                    String kriterijum = dobijena.getKlijent().getLozinka();
                    DateFormat df = new SimpleDateFormat(pattern);

                    String datum = df.format(dobijena.getDatumOd());
                    dodatni = "WHERE r.klijent_id = '" + id_korisnika + "' AND r.datum_od <= '" + datum + "' AND r.datum_do >= '" + datum + "' AND s.naziv_smestaja LIKE '%" + kriterijum + "%'";
                }
                //ima samo datum
                else if((lozinka == null && !jesteLozinka) && dobijena.getDatumOd() != null){
                    String pattern = "yyyy-MM-dd";
                    DateFormat df = new SimpleDateFormat(pattern);

                    String datum = df.format(dobijena.getDatumOd());
                    dodatni = "WHERE r.klijent_id = '" + id_korisnika + "' AND r.datum_od <= '" + datum + "' AND r.datum_do >= '" + datum + "'";
                }
                //ima samo kriterijum
                else if((lozinka != null && !jesteLozinka) && dobijena.getDatumOd() == null){
                    String kriterijum = dobijena.getKlijent().getLozinka();
                    dodatni = "WHERE r.klijent_id = '" + id_korisnika + "' AND s.naziv_smestaja LIKE '%" + kriterijum + "%'";
                }
                else{
                    dodatni = "WHERE r.klijent_id = '" + id_korisnika + "'";
                }
            }

            Connection connection = KonekcijaSaBazom.getInstance().getConnection();
            String query = "SELECT s.sifra_smestaja, s.naziv_smestaja, s.vlasnik_id, r.datum_od, r.datum_do, r.ukupan_iznos, r.klijent_id \nFROM smestaj s \nINNER JOIN rezervacija r on s.sifra_smestaja = r.sifra_smestaja\n"
                    //+ "from smestaj s "
                    /*+ "inner join rezervacija r on s.sifra_smestaja = r.sifra_smestaja "*/ + dodatni;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Long id = resultSet.getLong("sifra_smestaja");
                String naziv = resultSet.getString("naziv_smestaja");
                String usernameVlasnik = resultSet.getString("vlasnik_id");
                java.util.Date datumOd = new java.util.Date(resultSet.getDate("datum_od").getTime());
                java.util.Date datumDo = new java.util.Date(resultSet.getDate("datum_do").getTime());
                double ukupno = resultSet.getDouble("ukupan_iznos");
                String klijent = resultSet.getString("klijent_id");

                Smestaj s = new Smestaj();
                s.setSifraSmestaja(id);
                s = (Smestaj) pronadjiPoId(s);

                Korisnik k1 = new Klijent();
                k1.setKorisnickoIme(klijent);
                Klijent k = (Klijent) pronadjiPoId(k1);

                Rezervacija r = new Rezervacija(s, k, datumOd, datumDo, ukupno);
                rezervacije.add(r);
            }
            resultSet.close();
            statement.close();

            return rezervacije;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
