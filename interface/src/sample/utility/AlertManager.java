package sample.utility;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import sample.annotation.DocumentationAnnotation;
import sample.controller.MainFrameController;
import sample.controller.PluginController;
import sample.controller.SuggestionController;
import sample.model.StreamMedia;

/**
 * Created by Thomas on 23/07/2016.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "23/07/2016", description = "This is the class to create alert to inform the user about result of certain action.")
public class AlertManager {

    private Alert alert;
    private int code;

    public AlertManager(Class source, int code, String... information) {
        this.alert = new Alert((code < 0) ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION);
        this.code = code;

        this.alert.initStyle(StageStyle.UTILITY);

        if(source.equals(PluginController.class)) {
            showPluginAlert(information);
        } else if(source.equals(SuggestionController.class)) {
            showSuggestionAlert();
        } else if(source.equals(MainFrameController.class)) {
            showMainFrameAlert();
        } else if(source.equals(StreamMedia.class)) {
            showStreamingAlert(information);
        }
    }

    /**
     * Show an alert accordingly to the code of the alert
     */
    private void showPluginAlert(String[] information) {

        alert.setTitle("Plugin message");

        switch (code) {
            case -7:
                alert.setHeaderText("Plugin validity error");
                alert.setContentText("The selected file doesn't exist.");
                break;
            case -6:
                alert.setHeaderText("Plugin validity error");
                alert.setContentText("The selected file is not a jar file (.jar extension).");
                break;
            case -5:
                alert.setHeaderText("Plugin load error");
                alert.setContentText("The pane could not be load. Check your fxml file or the path to its attached controller.");
                break;
            case -4:
                alert.setHeaderText("Plugin load error");
                alert.setContentText("The main view of the '" + information[0] + "' plugin hasn't could be found.");
                break;
            case -3:
                alert.setHeaderText("Path to plugin");
                alert.setContentText("The path to directory containing all plugins hasn't been found...");
                break;
            case -2:
                alert.setHeaderText("Plugin upload error");
                alert.setContentText("The plugin could not be uploaded. Maybe a plugin with the same name already exists in the server.");
                break;
            case -1:
                alert.setHeaderText("Plugin not deleted");
                alert.setContentText("An error occurred ! The plugin was not deleted !");
                break;
            case 1:
                alert.setHeaderText("Plugin downloaded");
                alert.setContentText("The plugin was downloaded with success ! You can now configure where you want to place it via Interface -> Configure interface.");
                break;
            case 2:
                alert.setHeaderText("Plugin deleted");
                alert.setContentText("The plugin was deleted without trouble !");
                break;
            case 3:
                alert.setHeaderText("Plugin not valid");
                alert.setContentText("The downloaded plugin is not valid. It has been removed from your computer.");
                break;
            case 4:
                alert.setHeaderText("Plugin load success");
                alert.setContentText("The plugin has been validated ! Well done !");
                break;
            case 5:
                alert.setHeaderText("How to develop a plugin ?");
                String s =  "A plugin in MyCast is a JavaFX Pane. You need to develop it and export it to a .jar. Your plugin must respect the following properties :\n" +
                            "       - be a jar file (.jar extension)\n" +
                            "       - contain the package name \"plugin\"\n" +
                            "       - contain a main view name \"mainPluginView.fxml\" inside the \"plugin\" package, and with an AnchorPane as root pane\n" +
                            "       - if you want to add a controller to your .fxml, add a tag fx:controller to your root pane and link it to your controller path";
                alert.setContentText(s);
        }

        alert.showAndWait();
    }

    private void showSuggestionAlert() {

        alert.setTitle("Suggestion message");

        switch (code) {
            case -1:
                alert.setHeaderText("Plugin not deleted");
                alert.setContentText("An error occurred ! The plugin was not deleted !");
                break;
            case 1:
                alert.setHeaderText("Plugin downloaded");
                alert.setContentText("The plugin was downloaded with success ! You can now configure where you want to place it via Interface -> Configure interface.");
                break;
            case 2:
                alert.setHeaderText("Plugin deleted");
                alert.setContentText("The plugin was deleted without trouble !");
                break;
            case 3:
                alert.setHeaderText("Plugin not valid");
                alert.setContentText("The downloaded plugin is not valid. It has been removed from your computer.");
        }

        alert.showAndWait();
    }

    private void showMainFrameAlert() {

        alert.setTitle("Interface");

        switch (code) {
            case 1:
                alert.setHeaderText("Empty configuration");
                alert.setContentText("Interface configuration not found... Empty interface will load. You can configure the interface with interface -> configure interface menu");
                break;
        }

        alert.showAndWait();
    }

    private void showStreamingAlert(String[] information) {

        alert.setTitle("Connection information");

        switch (code) {
            case -4:
                alert.setHeaderText("Connection fail");
                alert.setContentText("Invalid Address !");
                break;
            case -3:
                alert.setHeaderText("Connection fail");
                alert.setContentText("The connection to the client has been failed ! Make sure the client is already started !");
                break;
            case -2:
                alert.setHeaderText("Connection fail");
                alert.setContentText("An error occurred during making the connection to the client ! Please try later !");
                break;
            case -1:
                alert.setHeaderText("Client disconnected");
                alert.setContentText("The connection has been lost with the client. Check if the client is still opened or check your network connection.");
                break;
            case 1:
                alert.setHeaderText("Connection done");
                alert.setContentText("The connection with '" + information[0] + "' has been successfully done !");
                break;
        }

        alert.showAndWait();
    }
}
