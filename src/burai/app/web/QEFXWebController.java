/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXWebController extends QEFXAppController {

    private static final int MAX_LEN_URL = 128;

    private static final double MIN_PROGRESS = 0.002;

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";
    private static final String FAVORITE_CLASS = "picfavo-button";

    private static final String PROGRESS_STYLE = "-fx-background-color: -fx-focus-color";
    private static final String PROGR100_STYLE = "-fx-background-color: derive(-fx-base-enh2, 50.0%)";

    private String initialUrl;

    @FXML
    private Button backwardButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button favoriteButton;

    @FXML
    private TextField urlField;

    @FXML
    private GridPane progressGrid;

    @FXML
    private Pane progressPane1;

    @FXML
    private Pane progressPane2;

    @FXML
    private WebView webView;

    public QEFXWebController(QEFXMainController mainController, String url) {
        super(mainController);

        if (url == null) {
            throw new IllegalArgumentException("url is null.");
        }

        this.initialUrl = url;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBackwardButton();
        this.setupForwardButton();
        this.setupReloadButton();
        this.setupFavoriteButton();
        this.setupUrlField();
        this.setupProgressGrid();
        this.setupWebEngine();
    }

    public WebEngine getEngine() {
        if (this.webView == null) {
            return null;
        }

        return this.webView.getEngine();
    }

    private void setupBackwardButton() {
        if (this.backwardButton == null) {
            return;
        }

        this.backwardButton.setText("");

        this.backwardButton.setTooltip(new Tooltip("backward"));

        this.backwardButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_LEFT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.backwardButton.setOnAction(event -> this.goBackward());
    }

    private void setupForwardButton() {
        if (this.forwardButton == null) {
            return;
        }

        this.forwardButton.setText("");

        this.forwardButton.setTooltip(new Tooltip("forward"));

        this.forwardButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_RIGHT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.forwardButton.setOnAction(event -> this.goForward());
    }

    private void setupReloadButton() {
        if (this.reloadButton == null) {
            return;
        }

        this.reloadButton.setText("");

        this.reloadButton.setTooltip(new Tooltip("reload"));

        this.reloadButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.reloadButton.setOnAction(event -> this.updateWebEngine());
    }

    private void setupFavoriteButton() {
        if (this.favoriteButton == null) {
            return;
        }

        this.favoriteButton.setText("");

        this.favoriteButton.setTooltip(new Tooltip("favorite"));

        this.favoriteButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.HEART, GRAPHIC_SIZE, null, FAVORITE_CLASS));

        this.favoriteButton.setOnAction(event -> {
            WebEngine engine = this.getEngine();
            String title = engine == null ? null : engine.getTitle();
            String location = engine == null ? null : engine.getLocation();
            if (location != null) {
                this.registerLocation(title, location);
            }
        });
    }

    private void setupUrlField() {
        if (this.urlField == null) {
            this.urlField = new TextField();
        }

        this.urlField.setText(this.initialUrl);

        this.urlField.setOnAction(event -> {
            this.updateWebEngine();
        });
    }

    private void setupProgressGrid() {
        if (this.progressGrid == null) {
            return;
        }

        List<ColumnConstraints> colmuns = this.progressGrid.getColumnConstraints();
        final ColumnConstraints colmun1;
        final ColumnConstraints colmun2;
        if (colmuns != null && colmuns.size() > 1) {
            colmun1 = colmuns.get(0);
            colmun2 = colmuns.get(1);
        } else {
            colmun1 = null;
            colmun2 = null;
        }

        if (colmun1 != null && colmun2 != null) {
            this.updateProgress(colmun1, colmun2);

            WebEngine engine = this.getEngine();
            Worker<Void> worker = engine == null ? null : engine.getLoadWorker();
            if (worker != null) {
                worker.progressProperty().addListener(o -> {
                    this.updateProgress(colmun1, colmun2);
                });
            }
        }
    }

    private void setupWebEngine() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        String webdataPath = Environments.getWebdataPath();
        File webdataFile = webdataPath == null ? null : new File(webdataPath);
        if (webdataFile != null) {
            engine.setUserDataDirectory(webdataFile);
        }

        engine.locationProperty().addListener(o -> {
            String location = engine.getLocation();
            if (location == null || location.trim().isEmpty()) {
                return;
            }

            if (QEFXDownloadDialog.isToDownload(location)) {
                QEFXDownloadDialog dialog = new QEFXDownloadDialog(location);
                dialog.setMainController(this.mainController);
                Stage stage = this.getStage();
                if (stage != null) {
                    dialog.initOwner(stage);
                }

                dialog.showAndDownload();
            }

            this.urlField.setText(location);
        });

        engine.setCreatePopupHandler(config -> {
            WebEngine engine2 = null;
            if (config != null && config.hasMenu()) {
                engine2 = this.mainController.showWebPage("about:blank");

            } else {
                try {
                    QEFXWebPopup webPopup = new QEFXWebPopup(this.mainController);
                    webPopup.show();
                    engine2 = webPopup.getEngine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (engine2 == null) {
                engine2 = new WebEngine();
            }

            return engine2;
        });

        this.updateWebEngine();
    }

    private void updateWebEngine() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        String url = this.urlField.getText();
        if (url != null && (!url.trim().isEmpty())) {
            engine.load(url);
        }
    }

    private void goBackward() {
        WebEngine engine = this.getEngine();
        WebHistory history = engine == null ? null : engine.getHistory();

        if (history != null) {
            try {
                history.go(-1);
            } catch (IndexOutOfBoundsException e) {
                // NOP
            }
        }
    }

    private void goForward() {
        WebEngine engine = this.getEngine();
        WebHistory history = engine == null ? null : engine.getHistory();

        if (history != null) {
            try {
                history.go(1);
            } catch (IndexOutOfBoundsException e) {
                // NOP
            }
        }
    }

    private void registerLocation(String title, String location) {
        if (location == null || location.trim().isEmpty()) {
            return;
        }

        String titleTrimed = title == null ? null : title.trim();

        String locationTrimed = location.trim();
        String locationShowing = locationTrimed;
        if (locationShowing.length() > (MAX_LEN_URL + 3)) {
            locationShowing = locationShowing.substring(0, MAX_LEN_URL) + "...";
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        DialogPane dialogPane = alert.getDialogPane();
        if (dialogPane != null) {
            dialogPane.setHeaderText("This page will be registered as a favorite.");
            dialogPane.setContentText("URL: " + locationShowing);
        }

        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || !optButtonType.isPresent()) {
            return;
        }

        if (optButtonType.get() == ButtonType.OK) {
            Environments.addWebsite(titleTrimed, locationTrimed);
            this.mainController.offerOnHomeTabSelected(explorerFacade -> {
                if (explorerFacade != null && explorerFacade.isWebMode()) {
                    explorerFacade.reloadLocation();
                }
            });
        }
    }

    private void updateProgress(ColumnConstraints colmun1, ColumnConstraints colmun2) {
        if (colmun1 == null || colmun2 == null) {
            return;
        }

        WebEngine engine = this.getEngine();
        Worker<Void> worker = engine == null ? null : engine.getLoadWorker();

        if (worker != null) {
            double progress = worker.getProgress();
            progress = Math.max(MIN_PROGRESS, progress);
            progress = Math.min(1.0, progress);

            if (this.progressPane1 != null) {
                if (progress < 1.0) {
                    this.progressPane1.setStyle(PROGRESS_STYLE);
                } else {
                    this.progressPane1.setStyle(PROGR100_STYLE);
                }
            }

            colmun1.setPercentWidth(100.0 * progress);
            colmun2.setPercentWidth(100.0 * (1.0 - progress));
        }
    }
}
