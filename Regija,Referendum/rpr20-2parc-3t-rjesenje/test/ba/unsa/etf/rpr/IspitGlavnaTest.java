package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @Start
    public void start (Stage stage) throws Exception {
        dao.vratiBazuNaDefault();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Gradovi svijeta");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @AfterEach
    public void zatvoriProzor(FxRobot robot) {
        if (robot.lookup("#btnCancel").tryQuery().isPresent())
            robot.clickOn("#btnCancel");
    }

    @Test
    public void testIzmijeniGrad(FxRobot robot) {
        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        TextField fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("", fieldRegija.getText());

        // Upisujemo regiju
        robot.clickOn("#fieldRegija");
        robot.write("Pariška regija");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vrijednost polja regija za Graz
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("", fieldRegija.getText());

        // Zatvaramo prozor
        robot.clickOn("#btnCancel");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ponovo uzimamo Pariz
        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("Pariška regija", fieldRegija.getText());
    }

    @Test
    public void testIzmijeniGradBaza(FxRobot robot) {
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        TextField fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("", fieldRegija.getText());

        // Upisujemo regiju
        robot.clickOn("#fieldRegija");
        robot.write("Štajerska");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Grad graz = dao.nadjiGrad("Graz");
        assertEquals("Štajerska", graz.getRegija());
    }


    @Test
    public void testDodajGrad(FxRobot robot) {
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        // Upisujemo regiju
        robot.clickOn("#fieldRegija");
        robot.write("Sarajevski kanton");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        TextField fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("", fieldRegija.getText());

        // Zatvaramo prozor
        robot.clickOn("#btnCancel");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sada uzimamo Pariz
        robot.clickOn("Sarajevo");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("Sarajevski kanton", fieldRegija.getText());
    }


    @Test
    public void testDodajGradBaza(FxRobot robot) {
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        // Upisujemo regiju
        robot.clickOn("#fieldRegija");
        robot.write("Sarajevski kanton");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Da li je Sarajevo dodano u bazu?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        assertEquals(6, dao.gradovi().size());

        boolean pronadjeno = false;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Sarajevo") && grad.getBrojStanovnika() == 350000 && grad.getRegija().equals("Sarajevski kanton")) {
                pronadjeno = true;
                break;
            }
        assertTrue(pronadjeno);
    }

}

