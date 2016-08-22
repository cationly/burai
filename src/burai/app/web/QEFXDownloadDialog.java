/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.fileview.QEFXFileViewDialog;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXUPFIcon;
import burai.com.env.Environments;
import burai.project.Project;
import burai.pseudo.PseudoLibrary;

public class QEFXDownloadDialog extends Dialog<File> {

    private static final int BYTE_BUFFER = 1024;

    private static final String PSEUDOS_EXPS = ".upf";

    private static final String[] DOWNLOAD_EXPS = { ".xyz", ".cif", ".cube", ".cub", ".in", PSEUDOS_EXPS };

    private static final String CAPTION_CLASS1 = "list-caption-head";
    private static final String CAPTION_CLASS2 = "list-caption";

    public static boolean isToDownload(String location) {
        if (location == null || location.trim().isEmpty()) {
            return false;
        }

        boolean toDownload = false;
        String locationLower = location.toLowerCase();
        for (String downloadExp : DOWNLOAD_EXPS) {
            if (locationLower.endsWith(downloadExp)) {
                toDownload = true;
                break;
            }
        }

        return toDownload;
    }

    private String downloadURL;

    private String downloadName;

    private String downloadDir;

    private boolean downloadPseudo;

    private QEFXMainController mainController;

    @FXML
    private StackPane figurePane;

    @FXML
    private StackPane captionPane;

    public QEFXDownloadDialog(String downloadURL) {
        super();

        if (downloadURL == null || downloadURL.trim().isEmpty()) {
            throw new IllegalArgumentException("downloadURL is empty.");
        }

        this.downloadURL = downloadURL.trim();
        this.createDownloadFile();

        this.mainController = null;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        String title = "Download";
        String header = "Download a file.";
        if (this.downloadName != null && !this.downloadName.trim().isEmpty()) {
            title = this.downloadName;
            header = "Download " + this.downloadName + ".";
        }

        this.setTitle(title);
        this.setResizable(false);
        this.initModality(Modality.NONE);
        dialogPane.setHeaderText(header);
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXDownloadDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return null;
        });
    }

    private void createDownloadFile() {
        String[] downloadSubs = this.downloadURL.split("/+");
        if (downloadSubs != null && downloadSubs.length > 0) {
            this.downloadName = downloadSubs[downloadSubs.length - 1];
        } else {
            this.downloadName = null;
        }

        if (this.downloadName != null) {
            this.downloadName = this.downloadName.trim();
        }

        if (this.downloadName != null && this.downloadName.toLowerCase().endsWith(PSEUDOS_EXPS)) {
            this.downloadDir = Environments.getPseudosPath();
            this.downloadPseudo = true;
        } else {
            this.downloadDir = Environments.getDownloadsPath();
            this.downloadPseudo = false;
        }
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXDownloadDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    public void setMainController(QEFXMainController mainController) {
        this.mainController = mainController;
    }

    public void showAndDownload() {
        this.show();

        Thread thread = new Thread(() -> {
            try {
                this.copyFileFromURL();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (this.downloadPseudo) {
                PseudoLibrary.getInstance().reload();
            }

            Platform.runLater(() -> this.showDownloadFile());
        });

        thread.start();
    }

    private void showDownloadFile() {
        File downloadFile = new File(this.downloadDir, this.downloadName);
        QEFXIcon icon = QEFXIcon.getInstance(downloadFile);
        if (icon != null) {
            this.updateFigurePane(icon);
            this.updateCaptionPane(icon);
        }
    }

    private void updateFigurePane(QEFXIcon icon) {
        if (this.figurePane == null) {
            return;
        }

        double width = this.figurePane.getWidth();
        double height = this.figurePane.getHeight();
        double size = Math.min(width, height);
        Node figure = icon.getFigure(size);
        if (figure == null) {
            return;
        }

        figure.setOnMouseClicked(event -> {
            if (event != null && event.getClickCount() >= 2) {
                if (icon instanceof QEFXUPFIcon) {
                    this.openUPFFile((QEFXUPFIcon) icon);
                } else if (icon instanceof QEFXProjectIcon) {
                    this.openTabProject((QEFXProjectIcon) icon);
                }
            }
        });

        StackPane.setAlignment(figure, Pos.CENTER);
        this.figurePane.getChildren().clear();
        this.figurePane.getChildren().add(figure);
    }

    private void updateCaptionPane(QEFXIcon icon) {
        if (this.captionPane == null) {
            return;
        }

        VBox vbox = new VBox();

        String caption1 = icon.getCaption();
        Label captionLabel1 = caption1 == null ? null : new Label(caption1);
        if (captionLabel1 != null) {
            captionLabel1.getStyleClass().add(CAPTION_CLASS1);
            vbox.getChildren().add(captionLabel1);
        }

        String caption2 = icon.getSubCaption();
        Label captionLabel2 = caption2 == null ? null : new Label(caption2);
        if (captionLabel2 != null) {
            captionLabel2.getStyleClass().add(CAPTION_CLASS2);
            vbox.getChildren().add(captionLabel2);
        }

        StackPane.setAlignment(vbox, Pos.CENTER);
        this.captionPane.getChildren().clear();
        this.captionPane.getChildren().add(vbox);
    }

    private void openUPFFile(QEFXUPFIcon icon) {
        String filePath = icon.getContent();
        if (filePath != null && (!filePath.trim().isEmpty())) {
            QEFXFileViewDialog fileDialog = new QEFXFileViewDialog(filePath);
            fileDialog.show();
        }
    }

    private void openTabProject(QEFXProjectIcon icon) {
        Project project = icon.getContent();
        if (project != null && project.isValid()) {
            if (this.mainController != null) {
                this.mainController.showProject(project);
                this.hide();
            }
        }
    }

    private void copyFileFromURL() throws IOException {
        if (this.downloadName == null || this.downloadName.trim().isEmpty()) {
            throw new IOException("downloadName is empty.");
        }

        if (this.downloadDir == null || this.downloadDir.trim().isEmpty()) {
            throw new IOException("downloadDir is empty.");
        }

        URL url = new URL(this.downloadURL);
        URLConnection urlConnection = url.openConnection();
        if (urlConnection == null) {
            throw new IOException("urlConnection is null.");
        }
        InputStream input = urlConnection.getInputStream();
        input = input == null ? null : new BufferedInputStream(input);
        if (input == null) {
            throw new IOException("input is null.");
        }

        File downloadFile = new File(this.downloadDir, this.downloadName);
        OutputStream output = new FileOutputStream(downloadFile, false);
        output = new BufferedOutputStream(output);

        byte[] byteBuffer = new byte[BYTE_BUFFER];

        while (true) {
            int nbyte = input.read(byteBuffer);
            if (nbyte < 0) {
                break;
            }
            output.write(byteBuffer, 0, nbyte);
        }

        input.close();
        output.close();
    }
}
