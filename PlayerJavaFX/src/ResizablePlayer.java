import com.sun.jna.Memory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import sun.applet.Main;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Created by thomasfouan on 16/03/2016.
 */
public class ResizablePlayer {

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private WritableImage writableImage;

    private Pane playerHolder;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private FloatProperty videoSourceRatioProperty;

    public ResizablePlayer(AnchorPane playerContainer) {

        // Initialisation of the components
        playerHolder = new Pane();
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);

        initializeImageView();

        mediaPlayerComponent = new CanvasPlayerComponent(writableImage, pixelFormat, videoSourceRatioProperty);

        // Add the player pane in the playerContainer
        VBox vBox = (VBox) playerContainer.lookup("#playerContainer");
        BorderPane playerPane = new BorderPane(playerHolder);
        playerPane.setStyle("-fx-background-color: black");
        vBox.getChildren().add(0, playerPane);
        VBox.setVgrow(playerPane, Priority.ALWAYS);
    }

    public DirectMediaPlayerComponent getMediaPlayerComponent() {
        return mediaPlayerComponent;
    }

    public Pane getPlayerHolder() {
        return playerHolder;
    }

    /**
     * initialize the type of image (size, ratio) to write in the player, accordingly with :
     *      - the dimensions of the screen
     *      - and the ratio of the video source
     *
     * Add listeners on the screen and on the ratio fo the current media.
     */
    private void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());

        // Add an imageView in the playerHolder to display each frame of the media
        imageView = new ImageView(writableImage);
        playerHolder.getChildren().add(imageView);

        playerHolder.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight());
        });

        playerHolder.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue());
        });

        videoSourceRatioProperty.addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight());
        });
    }

    /**
     * Set image dimensions to write in the player with the new values width and height.
     * @param width
     * @param height
     */
    private void fitImageViewSize(float width, float height) {
        Platform.runLater(() -> {
            float fitHeight = videoSourceRatioProperty.get() * width;
            if (fitHeight > height) {
                imageView.setFitHeight(height);
                double fitWidth = height / videoSourceRatioProperty.get();
                imageView.setFitWidth(fitWidth);
                imageView.setX((width - fitWidth) / 2);
                imageView.setY(0);
            }
            else {
                imageView.setFitWidth(width);
                imageView.setFitHeight(fitHeight);
                imageView.setY((height - fitHeight) / 2);
                imageView.setX(0);
            }
        });
    }
}
