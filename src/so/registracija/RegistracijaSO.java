/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.registracija;

import domain.GeneralEntity;
import domain.Korisnik;
import domain.VlasnikSmestaja;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class RegistracijaSO extends ApstraktnaGenerickaOperacija {

    private GeneralEntity korisnik;

    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof Korisnik)) {
            throw new Exception("Nevalidan entity parametar!");
        }
        Korisnik k = (Korisnik) entity;
        //validacija imena i prezimena
        for (char ch : k.getImePrezime().toCharArray()) {
            if (!Character.isAlphabetic(ch) && ch != ' ') {
                throw new Exception("Ime sme sadrzati samo slova!");
            }
        }

        //validacija JMBG-a
        if (k.getJmbg().length() != 13) {
            throw new Exception("JMBG mora imati 13 cifara! Proverite vas JMBG i probajte ponovo!");
        }
        for (char ch : k.getJmbg().toCharArray()) {
            if (!Character.isDigit(ch)) {
                throw new Exception("JMBG sme sadrzati samo cifre!");
            }
        }

        //validacija lozinke
        if (k.getLozinka().length() < 7) {
            throw new Exception("Lozinka mora sadrzati minimum 7 karaktera!");
        }
        boolean slovo = false;
        boolean broj = false;
        for (char ch : k.getLozinka().toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) {
                throw new Exception("Lozinka moze sadrzati samo slova i brojeve!");
            }
            if (Character.isAlphabetic(ch)) {
                slovo = true;
                continue;
            }
            if (Character.isDigit(ch)) {
                broj = true;
            }

        }

        //validacija emaila
        if (!k.getePosta().contains("@")) {
            throw new Exception("E-mail adresa mora sadrzati \"@\" znak!");
        }

        //validacija korisnickog imena
        if (k.getKorisnickoIme().length() < 4) {
            throw new Exception("Korisnicko ime mora biti dugacko minimum 4 karaktera!");
        }

        if (k instanceof VlasnikSmestaja) {
            VlasnikSmestaja v = (VlasnikSmestaja) k;
            //validacija broja licne karte
            for (char ch : v.getBrojLicneKarte().toCharArray()) {
                if (!Character.isDigit(ch)) {
                    throw new Exception("Broj licne karte sme sadrzati samo cifre!");
                }
            }

            //validacija broja telefona
            for (int i = 0; i < v.getKontaktTelefon().length(); i++) {
                if (!Character.isDigit(v.getKontaktTelefon().toCharArray()[i])) {
                    if (v.getKontaktTelefon().toCharArray()[i] == '+' && i == 0) {
                        continue;
                    }
                    throw new Exception("Telefon moze sadrzati samo cifre i + na pocetku!");
                }
            }
        }
    }

    @Override
    protected void izvrsi(Object entity) throws Exception {
        korisnik = broker.registracija((Korisnik) entity);
    }

    public GeneralEntity getKorisnik() {
        return korisnik;
    }

}
