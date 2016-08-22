/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app;

import java.io.File;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import burai.app.explorer.QEFXExplorer;
import burai.app.icon.web.WebEngineFactory;
import burai.app.proxy.ProxyServer;
import burai.com.env.Environments;
import burai.com.file.FileTools;
import burai.pseudo.PseudoLibrary;

public class QEFXMain extends Application {

    private static Stage mainStage = null;

    private static final String[] STYLE_SHEET_NAMES = {
            "QEFXApplication.css",
            "QEFXAppText.css",
            "QEFXAppButton.css",
            "QEFXAppToggle.css",
            "QEFXAppMenuItem.css",
            "QEFXAppExplorer.css",
            "QEFXAppIcon.css",
            "QEFXAppViewer.css",
            "QEFXAppResult.css"
    };

    public static void initializeStyleSheets(List<String> stylesheets) {
        if (stylesheets != null) {
            stylesheets.clear();

            for (String styleSheetName : STYLE_SHEET_NAMES) {
                URL url = null;
                if (styleSheetName != null) {
                    url = QEFXMain.class.getResource(styleSheetName);
                }

                String cssName = null;
                if (url != null) {
                    cssName = url.toExternalForm();
                }
                if (cssName != null) {
                    cssName = cssName.trim();
                }

                if (cssName != null && (!cssName.isEmpty())) {
                    stylesheets.add(cssName);
                }
            }
        }
    }

    public static void initializeTitleBarIcon(Stage stage) {
        if (stage != null) {
            URL url = QEFXMain.class.getResource("resource/image/icon_048.png");
            String iconName = url == null ? null : url.toExternalForm();
            if (iconName != null && (!iconName.isEmpty())) {
                Image icon = new Image(iconName);
                stage.getIcons().add(icon);
            }
        }
    }

    public static void initializeDialogOwner(Dialog<?> dialog) {
        if (dialog != null) {
            dialog.initOwner(mainStage);
        }
    }

    @Override
    public void init() {
        if (!Environments.existsProjectsPath()) {
            this.copyPseudos();
            this.copyExamples();
        }

        PseudoLibrary.getInstance().touch();

        ProxyServer.initProxyServer();
    }

    private void copyPseudos() {
        String pseudoPath = Environments.getPseudosPath();
        if (pseudoPath == null || pseudoPath.isEmpty()) {
            return;
        }

        File srcFile = new File("pseudopot");
        File dstFile = new File(pseudoPath);

        Thread thread = new Thread(() -> {
            FileTools.copyAllFiles(srcFile, dstFile, false);
        });

        thread.start();
    }

    private void copyExamples() {
        String projectPath = Environments.getProjectsPath();
        if (projectPath == null || projectPath.isEmpty()) {
            return;
        }

        File srcFile = new File("examples");
        File dstFile = new File(projectPath, "Examples");
        FileTools.copyAllFiles(srcFile, dstFile, false);
    }

    @Override
    public void start(Stage stage) {
        try {
            // initial operation
            mainStage = stage;
            WebEngineFactory.getInstance().touchAllWebEngines();

            // create QEFXMainController
            QEFXMainController controller = new QEFXMainController();

            // create Root (load FXML)
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXMain.fxml"));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            // create Scene
            Scene scene = new Scene(root);
            setUserAgentStylesheet(STYLESHEET_MODENA);
            initializeStyleSheets(scene.getStylesheets());
            mainStage.setScene(scene);

            // setup QEFXMainController
            controller.setStage(mainStage);
            controller.setMaximized(Environments.isWindows());
            controller.setFullScreen(false);
            controller.setResizable(true);
            controller.setExplorer(new QEFXExplorer(controller));

            // show Stage
            initializeTitleBarIcon(mainStage);
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
