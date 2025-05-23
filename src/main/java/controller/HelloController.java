package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.HelloApplication;

import java.io.IOException;

public class HelloController {

    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    @FXML
    private Text txtMessage;

    private void load(String form) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
        try {
            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btreeOperationOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void graphicBtreeOnAction(ActionEvent actionEvent) {
        load("graphic-btree.fxml");
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.txtMessage.setText("Laboratory 7");
        this.bp.setCenter(ap);
    }

    @FXML
    public void btreeTourOnAction(ActionEvent actionEvent) {
        load("TreeView.fxml");
    }

    @FXML
    public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }
}