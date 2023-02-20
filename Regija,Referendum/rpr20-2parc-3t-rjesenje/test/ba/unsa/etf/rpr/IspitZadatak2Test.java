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
public class IspitZadatak2Test {
    Stage theStage;
    GlavnaController ctrl;
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @Start
    public void start(Stage stage) throws Exception {
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
    public void testNijeUnesenaRegija(FxRobot robot) {
        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Provjeravamo da je polje Regija prazno
        TextField fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertNotNull(fieldRegija);
        assertEquals("", fieldRegija.getText());

        // Pokrećemo referendum
        robot.clickOn("#btnReferendum");

        // Čekamo da dijalog postane vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Provjera teksta
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane.lookupAll("Niste unijeli naziv regije"));

        // Klik na dugme Ok
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);
    }

    @Test
    public void testReferendumIzmjena(FxRobot robot) {
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        robot.clickOn("#fieldRegija");
        robot.write("Engleska");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        robot.clickOn("#fieldNaziv");
        robot.write("n"); // Dodajemo slovo n

        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Dugme referendum treba imati ponašanje kao dugme Ok
        Grad london = dao.nadjiGrad("Londonn");
        assertNotNull(london);

        // Kreirana nova država
        assertEquals("Engleska", london.getDrzava().getNaziv());
        // Regija je sada prazna jer je Engleska sada država
        assertEquals("", london.getRegija());
    }

    @Test
    public void testDodavanjeDrzave(FxRobot robot) {
        // Da li se pozivom referenduma dodala nova država?
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        robot.clickOn("#fieldRegija");
        robot.write("Engleska");
        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean postojiBritanija = false, postojiEngleska = false;
        for (Drzava d : dao.drzave()) {
            if (d.getNaziv().equals("Velika Britanija"))
                postojiBritanija = true;
            if (d.getNaziv().equals("Engleska"))
                postojiEngleska = true;
        }
        assertTrue(postojiBritanija);
        assertTrue(postojiEngleska);

        // Tražimo na drugi način
        Drzava engleska = dao.nadjiDrzavu("Engleska");
        assertEquals("London", engleska.getGlavniGrad().getNaziv());
    }

    @Test
    public void testAzuriranjeListView(FxRobot robot) {
        // Da li se nakon referenduma ažurirao list view?
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("n"); // Dodajemo slovo n

        robot.clickOn("#fieldRegija");
        robot.write("Engleska");
        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Klikamo na ime države koje bi sada trebalo biti vidljivo
        robot.clickOn("Engleska");
        robot.clickOn("#btnIzmijeniGrad");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        TextField fieldNaziv = robot.lookup("#fieldNaziv").queryAs(TextField.class);
        assertEquals("Londonn", fieldNaziv.getText());

        // Zatvaramo prozor
        robot.clickOn("#btnCancel");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReferendumDodavanje(FxRobot robot) {
        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Glasgow");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Velika Britanija");
        robot.clickOn("#fieldRegija");
        robot.write("Škotska");

        robot.clickOn("#btnReferendum");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Dugme btnReferendum se treba ponašati kao btnOk, treba biti dodan Glasgow
        robot.clickOn("Glasgow");
        robot.clickOn("#btnIzmijeniGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Država je Škotska
        ChoiceBox<Drzava> choiceDrzava = robot.lookup("#choiceDrzava").queryAs(ChoiceBox.class);
        assertEquals("Škotska", choiceDrzava.getValue().getNaziv());

        // Regija je prazna, jer je Škotska sada država a ne regija
        TextField fieldRegija = robot.lookup("#fieldRegija").queryAs(TextField.class);
        assertEquals("", fieldRegija.getText());
    }

    @Test
    public void testReferendumDodavanjeBaza(FxRobot robot) {
        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Glasgow");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Velika Britanija");
        robot.clickOn("#fieldRegija");
        robot.write("Škotska");

        robot.clickOn("#btnReferendum");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Grad glasgow = dao.nadjiGrad("Glasgow");
        assertEquals("Škotska", glasgow.getDrzava().getNaziv());
        assertEquals("", glasgow.getRegija());

        Drzava skotska = dao.nadjiDrzavu("Škotska");
        assertEquals("Glasgow", skotska.getGlavniGrad().getNaziv());
        Drzava vbr = dao.nadjiDrzavu("Velika Britanija");
        assertNotNull(vbr);

        // London se nije promijenio
        Grad london = dao.nadjiGrad("London");
        assertEquals("Velika Britanija", london.getDrzava().getNaziv());
    }

    @Test
    public void testPromjenaViseGradova(FxRobot robot) {
        // Da li se pozivom referenduma dodala nova država?
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

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

        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Leoben");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("25000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("#fieldRegija");
        robot.write("Štajerska");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sada pozivamo referendum
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Leoben");
        robot.clickOn("#btnIzmijeniGrad");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Država je Štajerska
        ChoiceBox<Drzava> choiceDrzava = robot.lookup("#choiceDrzava").queryAs(ChoiceBox.class);
        assertEquals("Štajerska", choiceDrzava.getValue().getNaziv());
    }



    @Test
    public void testPromjenaViseGradovaBaza(FxRobot robot) {
        // Da li se pozivom referenduma dodala nova država?
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

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

        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Leoben");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("25000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("#fieldRegija");
        robot.write("Štajerska");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sada pozivamo referendum
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Grad graz = dao.nadjiGrad("Graz");
        assertEquals("Štajerska", graz.getDrzava().getNaziv());
        assertEquals("", graz.getRegija());

        Grad leoben = dao.nadjiGrad("Leoben");
        assertEquals("Štajerska", leoben.getDrzava().getNaziv());
        assertEquals("", leoben.getRegija());

        Drzava stajerska = dao.nadjiDrzavu("Štajerska");
        assertEquals("Graz", stajerska.getGlavniGrad().getNaziv());
        Drzava austrija = dao.nadjiDrzavu("Austrija");
        assertNotNull(austrija);
    }

    @Test
    public void testDodajDrzavuReferendum(FxRobot robot) {
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Španija");

        // Glavni grad će biti automatski izabran kao prvi

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Barcelona");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("1600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Španija");
        robot.clickOn("#fieldRegija");
        robot.write("Katalonija");
        robot.clickOn("#btnOk");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Referendum
        robot.clickOn("Barcelona");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da prozor postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();
        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Referendum
        robot.clickOn("Barcelona");
        robot.clickOn("#btnIzmijeniGrad");

        // Država je Katalonija
        ChoiceBox<Drzava> choiceDrzava = robot.lookup("#choiceDrzava").queryAs(ChoiceBox.class);
        assertEquals("Katalonija", choiceDrzava.getValue().getNaziv());
    }



    @Test
    public void testDodajDrzavuReferendumBaza(FxRobot robot) {
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Španija");

        // Glavni grad će biti automatski izabran kao prvi

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("#btnDodajGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Barcelona");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("1600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Španija");
        robot.clickOn("#fieldRegija");
        robot.write("Katalonija");
        robot.clickOn("#btnOk");

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Referendum
        robot.clickOn("Barcelona");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da prozor postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();
        robot.clickOn("#btnReferendum");

        // Čekamo da prozor prestane biti vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Grad barcelona = dao.nadjiGrad("Barcelona");
        assertEquals("Katalonija", barcelona.getDrzava().getNaziv());
        assertEquals("", barcelona.getRegija());

        Drzava katalonija = dao.nadjiDrzavu("Katalonija");
        assertEquals("Barcelona", katalonija.getGlavniGrad().getNaziv());
        // Španija i dalje postoji, iako u njoj sada nema nijedan grad
        Drzava spanija = dao.nadjiDrzavu("Španija");
        assertNotNull(spanija);
    }
}