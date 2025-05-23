package util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ucr.lab.HelloApplication;

import java.io.IOException;
import java.util.Optional;

public class FXUtility {

    public static void loadPage(String className, String page, BorderPane bp) {
        try {
            Class cl = Class.forName(className);
            FXMLLoader fxmlLoader = new FXMLLoader(cl.getResource(page));
            cl.getResource("bp");
            bp.setCenter(fxmlLoader.load());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Alert alert(String title, String headerText){
        Alert myalert = new Alert(Alert.AlertType.INFORMATION);
        myalert.setTitle(title);
        myalert.setHeaderText(headerText);
        DialogPane dialogPane = myalert.getDialogPane();
        String css = HelloApplication.class.getResource("alert-style.css").toExternalForm();
        dialogPane.getStylesheets().add(css);
        dialogPane.getStyleClass().add("myDialog");
        return myalert;
    }

    public static TextInputDialog dialog(String title, String headerText){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        return dialog;
    }

    public static String alertYesNo(String title, String headerText, String contextText){
        Alert myalert = new Alert(Alert.AlertType.CONFIRMATION);
        myalert.setTitle(title);
        myalert.setHeaderText(headerText);
        myalert.setContentText(contextText);
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        myalert.getDialogPane().getButtonTypes().clear(); //quita los botones defaults
        myalert.getDialogPane().getButtonTypes().add(buttonTypeYes);
        myalert.getDialogPane().getButtonTypes().add(buttonTypeNo);
        //dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        DialogPane dialogPane = myalert.getDialogPane();
        String css = HelloApplication.class.getResource("dialog.css").toExternalForm();
        dialogPane.getStylesheets().add(css);
        Optional<ButtonType> result = myalert.showAndWait();
        //if((result.isPresent())&&(result.get()== ButtonType.OK)) {
        if((result.isPresent())&&(result.get()== buttonTypeYes))
            return "YES";
        else return "NO";
    }

    // Versión alternativa usando la clase FXUtility modificada
    public static Alert customTourInfoAlert(String title, String headerText) {
        Alert customAlert = new Alert(Alert.AlertType.INFORMATION);
        customAlert.setTitle(title);
        customAlert.setHeaderText(null); // Quitamos el headerText por defecto

        DialogPane dialogPane = customAlert.getDialogPane();

        // Crear contenido personalizado
        VBox customContent = new VBox(10);
        customContent.setStyle("-fx-padding: 0;");

        // Header personalizado
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #00BCD4; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label infoIcon = new Label("ⓘ");
        infoIcon.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, infoIcon);

        customContent.getChildren().add(header);

        // Establecer el contenido personalizado
        dialogPane.setContent(customContent);

        // Aplicar estilos CSS si existen
        try {
            String css = HelloApplication.class.getResource("alert-style.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
        } catch (Exception e) {
            // Si no se encuentra el CSS, usar estilos inline
            dialogPane.setStyle("-fx-background-color: #E0F7FA;");
        }

        return customAlert;
    }
}
