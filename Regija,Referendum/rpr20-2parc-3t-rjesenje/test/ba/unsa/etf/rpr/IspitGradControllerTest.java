package ba.unsa.etf.rpr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.vratiBazuNaDefault();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(null, dao.drzave());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
        theStage = stage;
    }

    @Test
    public void testUnosRegije(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Bihać");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("80000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");
        robot.clickOn("#fieldRegija");
        robot.write("Unsko-Sanski kanton");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Grad bihac = ctrl.getGrad();
        assertEquals("Bihać", bihac.getNaziv());
        assertEquals(80000, bihac.getBrojStanovnika());
        assertEquals("Francuska", bihac.getDrzava().getNaziv());
        assertEquals("Unsko-Sanski kanton", bihac.getRegija() );
    }
}