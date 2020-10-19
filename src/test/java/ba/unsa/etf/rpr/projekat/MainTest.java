package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.controller.Controller;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAOBase;
import ba.unsa.etf.rpr.projekat.dto.Location;
import ba.unsa.etf.rpr.projekat.dto.Owner;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(ApplicationExtension.class)
public class MainTest {

    Stage theStage;
    VehiclesDAO dao;
    Controller controller;

    @Start
    public void start(Stage stage) throws Exception {
        File dbfile = new File("vehicle.db");
        ClassLoader classLoader = getClass().getClassLoader();
        File srcfile = new File(classLoader.getResource("db/vehicle.db").getFile());
        try {
            dbfile.delete();
            Files.copy(srcfile.toPath(), dbfile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Ne mogu kreirati bazu");
        }

        /*try {
            initFile("locations.xml");
            initFile("brands.xml");
            initFile("owners.xml");
            initFile("vehicles.xml");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Ne mogu kreirati datoteku");
        }*/


        dao = new VehiclesDAOBase();

        // Ovo bi trebalo da iskopira fajl iz resources u test-resources, a ipak radi i sa mavenom
        /*File fxml = new File("resources/fxml/screen.fxml");

        if (fxml.exists()) {
            File rsrc = new File("test-resources/fxml/screen.fxml");
            if (rsrc.exists()) rsrc.delete();
            Files.copy(fxml.toPath(), rsrc.toPath());
        }*/

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/screen.fxml"));
        stage.setTitle("Auto-moto klub");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
        stage.toFront();

        theStage = stage;
    }

    private void initFile(String file) throws IOException {
        File dbfile = new File(file);
        ClassLoader classLoader = getClass().getClassLoader();
        File srcfile = new File(classLoader.getResource("xml/" + file).getFile());
        dbfile.delete();
        Files.copy(srcfile.toPath(), dbfile.toPath());
    }

    @Test
    public void testRemoveVlasnik(FxRobot robot) {
        robot.clickOn("#ownersTab");
        robot.clickOn("#tableOwners");

        robot.clickOn("John Swift");

        robot.clickOn("#tbRemoveOwner");

        // Waiting for dialog
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);


        // Čekamo da se pojavi novi dijalog koji kaže da nije moguće brisati
        robot.lookup(".dialog-pane").tryQuery().isPresent();
        dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        System.out.println(dialogPane.getGraphic());

        // Klik na dugme Ok
        dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Not deleted
        ObservableList<Owner> vlasnici = dao.getOwners();
        assertEquals(3, vlasnici.size());
    }

    @Test
    public void testAddRemoveVlasnik(FxRobot robot) {
        robot.clickOn("#ownersTab");
        robot.clickOn("#tbAddOwner");

        // Čekamo da prozor postane vidljiv
        robot.lookup("#firstNameField").tryQuery().isPresent();
        robot.clickOn("#firstNameField");
        robot.write("abc");
        robot.clickOn("#lastNameField");
        robot.write("d");
        robot.clickOn("#parentNameField");
        robot.write("e");
        robot.clickOn("#addressField");
        robot.write("f");
        robot.clickOn("#dateOfBirthPicker");
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#nationalIdNumberField");
        robot.write("080100350");

        ComboBox mjestoRodjenja = robot.lookup("#placeOfBirthCombo").queryAs(ComboBox.class);
        Platform.runLater(() -> mjestoRodjenja.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("New York");

        robot.clickOn("#placeOfResidenceCombo");
        robot.write("Istanbul");

        robot.clickOn("#postalCodeField");
        robot.write("88000");

        // Sve validno, prozor se zatvara
        robot.clickOn("#okButton");

        // Čekamo da se doda korisnik
        try {
            // Povećao na 5 sekundi zbog spore konekcije
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ObservableList<Owner> vlasnici = dao.getOwners();
        assertEquals(3, vlasnici.size());
        assertEquals(2, vlasnici.get(1).getId());
        assertEquals("abc", vlasnici.get(1).getFirstName());
        assertEquals("New York", vlasnici.get(1).getPlaceOfResidence().getName());

        // Brišemo vlasnika
        robot.clickOn("#ownersTab");
        robot.clickOn("#tableOwners");

        // Selektujemo Mehu Mehića
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.clickOn("#tableOwners");

        robot.clickOn("#tbRemoveOwner");

        // Čekamo da dijalog postane vidljiv
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Čekamo da se obriše korisnik
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Obrisan
        ObservableList<Owner> vlasnici2 = dao.getOwners();
        assertEquals(1, vlasnici2.size());
        assertEquals("Meho", vlasnici2.get(0).getFirstName());

        // Mostar je i dalje u bazi
        ObservableList<Location> mjesta = dao.getLocations();
        assertEquals(3, mjesta.size());
        // Ovo će vratiti abecedno, tako da će Mostar biti na indeksu 0 (prije Tuzle i Sarajeva)
        assertEquals("New York", mjesta.get(0).getName());
        assertEquals("88000", mjesta.get(0).getPostalCode());
    }
/*
    @Test
    public void testAddRemoveVlasnikXml(FxRobot robot) {
        // Prebacujemo na Xml
        robot.clickOn("#menuOpcije");
        robot.clickOn("#menuXml");

        robot.clickOn("#vlasniciTab");
        robot.clickOn("#tbAddVlasnik");

        // Čekamo da prozor postane vidljiv
        robot.lookup("#imeField").tryQuery().isPresent();
        robot.clickOn("#imeField");
        robot.write("abc");
        robot.clickOn("#prezimeField");
        robot.write("d");
        robot.clickOn("#imeRoditeljaField");
        robot.write("e");
        robot.clickOn("#adresaField");
        robot.write("f");
        robot.clickOn("#datumField");
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#jmbgField");
        robot.write("0801003500007");

        ComboBox mjestoRodjenja = robot.lookup("#mjestoRodjenja").queryAs(ComboBox.class);
        Platform.runLater(() -> mjestoRodjenja.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Sarajevo");

        robot.clickOn("#adresaMjesto");
        robot.write("Mostar");

        robot.clickOn("#postanskiBrojField");
        robot.write("88000");

        // Sve validno, prozor se zatvara
        robot.clickOn("#okButton");

        // Čekamo da se doda korisnik
        try {
            // Povećao na 5 sekundi zbog spore konekcije
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Provjerićemo broj vlasnika tako što ćemo napraviti posebnu instancu dao klase
        // (u nekim implementacijama bi ovo moglo pasti, ali ne pada mi na pamet kojim)
        VozilaDAO mydao = new VozilaDAOXML();

        ObservableList<Vlasnik> vlasnici = mydao.getVlasnici();
        assertEquals(2, vlasnici.size());
        assertEquals(2, vlasnici.get(1).getId());
        assertEquals("abc", vlasnici.get(1).getIme());
        assertEquals("Mostar", vlasnici.get(1).getMjestoPrebivalista().getNaziv());
        mydao.close();

        // Brišemo vlasnika
        robot.clickOn("#vlasniciTab");
        robot.clickOn("#tabelaVlasnici");

        // Selektujemo Mehu Mehića
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.clickOn("#tabelaVlasnici");

        robot.clickOn("#tbRemoveVlasnik");

        // Čekamo da dijalog postane vidljiv
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Čekamo da se obriše korisnik
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Obrisan
        mydao = new VozilaDAOXML();
        ObservableList<Vlasnik> vlasnici2 = mydao.getVlasnici();
        assertEquals(1, vlasnici2.size());
        assertEquals("Meho", vlasnici2.get(0).getIme());

        // Mostar je i dalje u bazi
        ObservableList<Mjesto> mjesta = mydao.getMjesta();
        assertEquals(3, mjesta.size());
        // Ovo će vratiti abecedno, tako da će Mostar biti na indeksu 0 (prije Tuzle i Sarajeva)
        assertEquals("Mostar", mjesta.get(0).getNaziv());
        assertEquals("88000", mjesta.get(0).getPostanskiBroj());
        mydao.close();

        // Vraćam se na Db
        robot.clickOn("#menuOpcije");
        robot.clickOn("#menuDb");
    }

    @Test
    public void testRemoveVozilo(FxRobot robot) {
        dao.close();

        robot.clickOn("#vozilaTab");

        // Dodajemo vozilo
        robot.clickOn("#tbAddVozilo");

        // Čekamo da se pojavi prozor
        robot.lookup("#proizvodjacCombo").tryQuery().isPresent();

        robot.clickOn("#proizvodjacCombo");
        robot.write("Skoda");
        robot.clickOn("#modelField");
        robot.write("Fabia");
        robot.clickOn("#brojSasijeField");
        robot.write("1234193459845");
        robot.clickOn("#brojTablicaField");
        robot.write("M23-K-456");

        ComboBox vlasnikCombo = robot.lookup("#vlasnikCombo").queryAs(ComboBox.class);
        Platform.runLater(() -> vlasnikCombo.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Mehic Meho");

        robot.clickOn("#okButton");

        // Čekamo da se doda vozilo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Da li je dodano
        dao = new VozilaDAOBaza();
        ObservableList<Vozilo> vozila = dao.getVozila();
        dao.close();
        assertEquals(2, vozila.size());
        assertEquals("Skoda", vozila.get(1).getProizvodjac().getNaziv());
        assertEquals("Fabia", vozila.get(1).getModel());
        assertEquals("1234193459845", vozila.get(1).getBrojSasije());
        assertEquals("M23-K-456", vozila.get(1).getBrojTablica());
        assertEquals("Mehic", vozila.get(1).getVlasnik().getPrezime());

        robot.clickOn("#tabelaVozila");

        // Selektujemo Škodu
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.clickOn("#tabelaVozila");

        robot.clickOn("#tbRemoveVozilo");

        // Čekamo da dijalog postane vidljiv
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Čekamo da se obriše vozilo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Nije obrisan
        dao = new VozilaDAOBaza();
        ObservableList<Vozilo> vozila2 = dao.getVozila();
        assertEquals(1, vozila2.size());
        assertEquals("Golf", vozila.get(0).getModel());
    }
    */

}
