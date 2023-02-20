package ba.unsa.etf.rpr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IspitGradTest {
    @Test
    public void testRegija() {
        Grad g = new Grad(1, "Sarajevo", 350000, null);
        g.setRegija("Sarajevski Kanton");
        assertEquals("Sarajevski Kanton", g.getRegija());
    }

    @Test
    public void testCtor() {
        Grad g = new Grad(1, "Visoko", 45000, null, "Zeničko-Dobojski Kanton");
        assertEquals("Zeničko-Dobojski Kanton", g.getRegija());
    }
}
