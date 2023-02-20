package ba.unsa.etf.rpr;

public class Grad {
    private int id;
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;
    private String regija;

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.id = id;
        this.naziv = naziv;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
        this.regija = "";
    }


    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava, String regija) {
        this.id = id;
        this.naziv = naziv;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
        this.regija = regija;
    }

    public Grad() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public String getRegija() {
        return regija;
    }

    public void setRegija(String regija) {
        this.regija = regija;
    }

    @Override
    public String toString() { return naziv; }
}
