/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.registracija;

import domain.GeneralEntity;
import domain.VlasnikSmestaja;
import so.ApstraktnaGenerickaOperacija;

/**
 *
 * @author user
 */
public class RegistracijaVSO extends ApstraktnaGenerickaOperacija {

    private GeneralEntity vlasnik;

    @Override
    protected void validacija(Object entity) throws Exception {
        if (!(entity instanceof VlasnikSmestaja)) {
            throw new Exception("Nevalidan entity parametar!");
        }
        VlasnikSmestaja v = (VlasnikSmestaja) entity;
        //validacija imena i prezimena
        for (char ch : v.getImePrezime().toCharArray()) {
            if (!Character.isAlphabetic(ch) && ch != ' ') {
                throw new Exception("Ime sme sadrzati samo slova!");
            }
        }

        //validacija JMBG-a
        if (v.getJmbg().length() != 13) {
            throw new Exception("JMBG mora imati 13 cifara! Proverite vas JMBG i probajte ponovo!");
        }
        for (char ch : v.getJmbg().toCharArray()) {
            if (!Character.isDigit(ch)) {
                throw new Exception("JMBG sme sadrzati samo cifre!");
            }
        }

        //validacija lozinke
        if (v.getLozinka().length() < 7) {
            throw new Exception("Lozinka mora sadrzati minimum 7 karaktera!");
        }
        boolean slovo = false;
        boolean broj = false;
        for (char ch : v.getLozinka().toCharArray()) {
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
        if (!slovo || !broj) {
            throw new Exception("Lozinka mora sadrzati barem jedno slovo i barem jedan broj!");
        }

        //validacija emaila
        if (!v.getePosta().contains("@")) {
            throw new Exception("E-mail adresa mora sadrzati \"@\" znak!");
        }

        //validacija korisnickog imena
        if (v.getKorisnickoIme().length() < 4) {
            throw new Exception("Korisnicko ime mora biti dugacko minimum 4 karaktera!");
        }

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

    @Override
    protected void izvrsi(Object entity) throws Exception {
        vlasnik = broker.registracija((VlasnikSmestaja) entity);
    }

    public GeneralEntity getVlasnik() {
        return vlasnik;
    }

}
