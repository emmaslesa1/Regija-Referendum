package ba.unsa.etf.rpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GeografijaDAOTest {
    private GeografijaDAO dao = GeografijaDAO.getInstance();

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @Test
    void regenerateFile() {
        // Testiramo da li će fajl ponovo biti kreiran nakon brisanja
        // Ovaj test može padati na Windowsu zbog lockinga, u tom slučaju pokrenite ovaj test
        // odvojeno od ostalih
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        this.dao = GeografijaDAO.getInstance();
        ArrayList<Grad> gradovi = dao.gradovi();
        assertEquals("London", gradovi.get(0).getNaziv());
        assertEquals("Francuska", gradovi.get(1).getDrzava().getNaziv());
    }

    @Test
    void glavniGrad() {
        Grad nepoznat = dao.glavniGrad("Bosna i Hercegovina");
        assertNull(nepoznat);
        Grad bech = dao.glavniGrad("Austrija");
        assertEquals("Beč", bech.getNaziv());
    }

    @Test
    void obrisiDrzavu() {
        // Nepostojeća država, neće se desiti ništa
        dao.obrisiDrzavu("Kina");
        ArrayList<Grad> gradovi = dao.gradovi();
        assertEquals("Pariz", gradovi.get(1).getNaziv());
        assertEquals("Austrija", gradovi.get(2).getDrzava().getNaziv());
    }

    @Test
    void obrisiDrzavu2() {
        // Nakon brisanja Austrije neće više biti gradova Beč i Graz
        dao.obrisiDrzavu("Austrija");

        ArrayList<Grad> gradovi = dao.gradovi();
        assertEquals(3, gradovi.size());
        assertEquals("London", gradovi.get(0).getNaziv());
        assertEquals("Pariz", gradovi.get(1).getNaziv());
        assertEquals("Manchester", gradovi.get(2).getNaziv());
    }

    @Test
    void dodajGrad() {
        Drzava francuska = dao.nadjiDrzavu("Francuska");

        Grad grad = new Grad();
        grad.setNaziv("Marseille");
        grad.setBrojStanovnika(869815);
        grad.setDrzava(francuska);
        dao.dodajGrad(grad);

        // Marsej je veći od Manchestera i Graza, ali manji od Pariza, Londona i Beča
        ArrayList<Grad> gradovi = dao.gradovi();
        assertEquals("Marseille", gradovi.get(3).getNaziv());
    }

    @Test
    void dodajDrzavu() {
        Grad sarajevo = new Grad();
        sarajevo.setNaziv("Sarajevo");
        sarajevo.setBrojStanovnika(500000);
        // Privremeno stavljamo Francusku jer BiH još uvijek nije dodana
        sarajevo.setDrzava(dao.nadjiDrzavu("Francuska"));
        dao.dodajGrad(sarajevo);

        // Vršimo upit nadjiGrad da dobijemo validan ID grada
        sarajevo = dao.nadjiGrad("Sarajevo");

        // Dodajemo državu u bazu
        Drzava bih = new Drzava();
        bih.setNaziv("Bosna i Hercegovina");
        bih.setGlavniGrad(sarajevo);
        dao.dodajDrzavu(bih);

        // Vršimo upit nadjiDrzavu da dobijemo validan ID države
        bih = dao.nadjiDrzavu("Bosna i Hercegovina");

        // Ažuriramo grad
        sarajevo.setDrzava(bih);
        dao.izmijeniGrad(sarajevo);

        // Provjera
        Grad proba = dao.glavniGrad("Bosna i Hercegovina");
        assertEquals("Sarajevo", proba.getNaziv());
        assertEquals(500000, proba.getBrojStanovnika());
        assertEquals("Bosna i Hercegovina", proba.getDrzava().getNaziv());
    }

    @Test
    void izmijeniGrad() {
        Grad bech = dao.glavniGrad("Austrija");
        bech.setNaziv("Vienna");
        dao.izmijeniGrad(bech);

        ArrayList<Grad> gradovi = dao.gradovi();
        assertEquals("Vienna", gradovi.get(2).getNaziv());
    }
}
