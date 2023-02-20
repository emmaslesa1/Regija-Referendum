package ba.unsa.etf.rpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class IspitGeografijaDAOTest {
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @Test
    void testIzmijeniGrad() {
        Grad london = dao.nadjiGrad("London");
        london.setRegija("Londonska regija");
        dao.izmijeniGrad(london);

        // Uzimam drugu verziju
        Grad london2 = dao.nadjiGrad("London");
        assertEquals("Londonska regija", london2.getRegija());
    }

    @Test
    void testDodajGrad() {
        Drzava d = dao.nadjiDrzavu("Francuska");
        Grad tuzla = new Grad(0, "Tuzla", 120000, d);
        tuzla.setRegija("Tuzlanski kanton");
        dao.dodajGrad(tuzla);

        Grad tuzla2 = dao.nadjiGrad("Tuzla");
        assertEquals("Tuzlanski kanton", tuzla2.getRegija());
    }

}
