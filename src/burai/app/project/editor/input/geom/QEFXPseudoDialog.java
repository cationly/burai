/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.fileview.QEFXFileViewDialog;
import burai.app.icon.QEFXUPFIcon;
import burai.atoms.element.ElementUtil;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.pseudo.PseudoData;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEFXPseudoDialog extends Dialog<PseudoPotential> implements Initializable {

    private static final double FILE_GRAPHIC_SIZE = 26.5;
    private static final String FILE_GRAPHIC_CLASS = "picheavy-button";

    private static final double DL_GRAPHIC_SIZE = 20.0;
    private static final String DL_GRAPHIC_CLASS = "piclight-button";

    private static final String[] PSEUDO_TYPE_ITEMS = {
            "All",
            "Norm-Conserving",
            "Ultrasoft",
            "PAW"
    };

    private static final Map<String, Integer> PSEUDO_TYPE_MAP = new HashMap<String, Integer>();

    static {
        PSEUDO_TYPE_MAP.put(PSEUDO_TYPE_ITEMS[0], PseudoData.PSEUDO_TYPE_UNKNOWN);
        PSEUDO_TYPE_MAP.put(PSEUDO_TYPE_ITEMS[1], PseudoData.PSEUDO_TYPE_NC);
        PSEUDO_TYPE_MAP.put(PSEUDO_TYPE_ITEMS[2], PseudoData.PSEUDO_TYPE_US);
        PSEUDO_TYPE_MAP.put(PSEUDO_TYPE_ITEMS[3], PseudoData.PSEUDO_TYPE_PAW);
    }

    private static final String[] FUNCTIONAL_ITEMS = {
            "All",
            "PZ",
            "PW91",
            "PBE",
            "revPBE",
            "PBEsol",
            "BLYP"
    };

    private static final Map<String, Integer> FUNCTIONAL_MAP = new HashMap<String, Integer>();

    static {
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[0], PseudoData.FUNCTIONAL_UNKNOWN);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[1], PseudoData.FUNCTIONAL_PZ);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[2], PseudoData.FUNCTIONAL_PW91);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[3], PseudoData.FUNCTIONAL_PBE);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[4], PseudoData.FUNCTIONAL_REVPBE);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[5], PseudoData.FUNCTIONAL_PBESOL);
        FUNCTIONAL_MAP.put(FUNCTIONAL_ITEMS[6], PseudoData.FUNCTIONAL_BLYP);
    }

    private QEFXAppController controller;

    private String element;

    private String initPseudo;

    @FXML
    private ComboBox<String> ppTypeCombo;

    @FXML
    private ComboBox<String> xcFuncCombo;

    @FXML
    private ListView<PseudoPotential> pseudoList;

    @FXML
    private Button downloadButton;

    public QEFXPseudoDialog(QEFXAppController controller, String element, String initPseudo) {
        super();
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        if (element == null || element.trim().isEmpty()) {
            throw new IllegalArgumentException("element is null.");
        }

        this.controller = controller;

        this.element = ElementUtil.toElementName(element);
        if (this.element == null || this.element.trim().isEmpty()) {
            throw new IllegalArgumentException("cannot get element name.");
        }

        this.initPseudo = initPseudo;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Select a pseudopotential");
        dialogPane.setHeaderText("Select a pseudopotential of " + this.element + ".");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXPseudoDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return null;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXPseudoDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupPPTypeCombo();
        this.setupXCFuncCombo();
        this.setupPseudoList();
        this.setupDownloadButton();
    }

    private void setupPPTypeCombo() {
        if (this.ppTypeCombo == null) {
            return;
        }

        this.ppTypeCombo.getItems().clear();
        this.ppTypeCombo.getItems().addAll(PSEUDO_TYPE_ITEMS);
        this.ppTypeCombo.setValue(PSEUDO_TYPE_ITEMS[0]);
        this.ppTypeCombo.setOnAction(event -> this.updatePseudoList());
    }

    private void setupXCFuncCombo() {
        if (this.xcFuncCombo == null) {
            return;
        }

        this.xcFuncCombo.getItems().clear();
        this.xcFuncCombo.getItems().addAll(FUNCTIONAL_ITEMS);
        this.xcFuncCombo.setValue(FUNCTIONAL_ITEMS[0]);
        this.xcFuncCombo.setOnAction(event -> this.updatePseudoList());
    }

    private void setupPseudoList() {
        if (this.pseudoList == null) {
            return;
        }

        Label emptyLabel = new Label("No pseudopotentials, please download files.");
        emptyLabel.getStyleClass().add("italic-text");
        this.pseudoList.setPlaceholder(emptyLabel);

        this.pseudoList.setCellFactory(listView -> {
            return new PseudoCell(this);
        });

        this.pseudoList.setOnKeyPressed(event -> {
            KeyCode code = null;
            if (event != null) {
                code = event.getCode();
            }
            if (code == null) {
                return;
            }

            if (code.equals(KeyCode.ENTER)) {
                MultipleSelectionModel<PseudoPotential> selectionModel = this.pseudoList.getSelectionModel();
                if (selectionModel != null) {
                    PseudoPotential pseudoPot = selectionModel.getSelectedItem();
                    this.setResult(pseudoPot);
                    this.close();
                }
            }
        });

        this.updatePseudoList();

        PseudoPotential initPseudoPot = null;
        if (this.initPseudo != null) {
            initPseudoPot = PseudoLibrary.getInstance().peekPseudoPotential(this.initPseudo);
        }
        if (initPseudoPot != null && this.pseudoList.getItems().contains(initPseudoPot)) {
            this.pseudoList.scrollTo(initPseudoPot);
            MultipleSelectionModel<PseudoPotential> selectionModel = this.pseudoList.getSelectionModel();
            if (selectionModel != null) {
                selectionModel.select(initPseudoPot);
            }
        }
    }

    private void updatePseudoList() {
        if (this.pseudoList == null) {
            return;
        }

        this.pseudoList.getItems().clear();

        int pseudoType = PSEUDO_TYPE_MAP.get(PSEUDO_TYPE_ITEMS[0]);
        if (this.ppTypeCombo != null) {
            String value = this.ppTypeCombo.getValue();
            if (value != null && PSEUDO_TYPE_MAP.containsKey(value)) {
                pseudoType = PSEUDO_TYPE_MAP.get(value);
            }
        }

        int functional = FUNCTIONAL_MAP.get(FUNCTIONAL_ITEMS[0]);
        if (this.xcFuncCombo != null) {
            String value = this.xcFuncCombo.getValue();
            if (value != null && FUNCTIONAL_MAP.containsKey(value)) {
                functional = FUNCTIONAL_MAP.get(value);
            }
        }

        PseudoPotential[] pseudoPots =
                PseudoLibrary.getInstance().listPseudoPotentials(this.element, pseudoType, functional);

        if (pseudoPots != null && pseudoPots.length > 0) {
            for (PseudoPotential pseudoPot : pseudoPots) {
                this.pseudoList.getItems().add(pseudoPot);
            }
        }
    }

    private static class PseudoCell extends ListCell<PseudoPotential> {
        private QEFXPseudoDialog dialog;

        private BorderPane basePane;
        private PseudoPotential pseudoPot;

        public PseudoCell(QEFXPseudoDialog dialog) {
            super();
            if (dialog == null) {
                throw new IllegalArgumentException("dialogis null.");
            }

            this.dialog = dialog;
            this.basePane = null;
            this.pseudoPot = null;
            this.setupMouseDoubleClicked();
        }

        private void setupMouseDoubleClicked() {
            this.setOnMouseClicked(event -> {
                if (event != null && event.getClickCount() >= 2) {
                    this.dialog.setResult(this.pseudoPot);
                    this.dialog.close();
                }
            });
        }

        @Override
        protected void updateItem(PseudoPotential pseudoPot, boolean empty) {
            super.updateItem(pseudoPot, empty);
            if (!empty) {
                this.setGraphic(this.getBasePane(pseudoPot));
            } else {
                this.setGraphic(null);
            }
        }

        private BorderPane getBasePane(PseudoPotential pseudoPot) {
            if (pseudoPot == null) {
                return null;
            }

            if (this.basePane == null || this.pseudoPot == null || this.pseudoPot != pseudoPot) {
                this.pseudoPot = pseudoPot;
                this.basePane = new BorderPane(this.getVBox());
                this.basePane.setRight(this.getButton());
            }

            return this.basePane;
        }

        private VBox getVBox() {
            String name = this.pseudoPot.getName();
            Label nameLabel = null;
            if (name != null && !name.isEmpty()) {
                nameLabel = new Label(name);
                nameLabel.getStyleClass().add("list-caption-head");
            }

            String path = this.pseudoPot.getPath();
            QEFXUPFIcon icon = null;
            if (path != null && !path.isEmpty()) {
                icon = new QEFXUPFIcon(path);
            }

            String prop = icon == null ? null : icon.getPropertiesCaption(false);
            Label propLabel = null;
            if (prop != null && !prop.isEmpty()) {
                propLabel = new Label(prop);
                propLabel.getStyleClass().add("list-caption");
            }

            VBox vbox = new VBox();
            vbox.getChildren().clear();
            if (nameLabel != null) {
                vbox.getChildren().add(nameLabel);
            }
            if (propLabel != null) {
                vbox.getChildren().add(propLabel);
            }

            return vbox;
        }

        private Button getButton() {
            Button button = new Button();
            button.getStyleClass().add(FILE_GRAPHIC_CLASS);
            button.setGraphic(SVGLibrary.getGraphic(SVGData.FILE, FILE_GRAPHIC_SIZE, null, FILE_GRAPHIC_CLASS));
            BorderPane.setAlignment(button, Pos.CENTER);

            button.setOnAction(event -> {
                if (this.pseudoPot != null && this.pseudoPot.exists()) {
                    QEFXFileViewDialog fileDialog = new QEFXFileViewDialog(this.pseudoPot.getFile());
                    fileDialog.show();
                }
            });

            return button;
        }
    }

    private void setupDownloadButton() {
        if (this.downloadButton == null) {
            return;
        }

        this.downloadButton.getStyleClass().add(DL_GRAPHIC_CLASS);

        this.downloadButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.DOWNLOAD, DL_GRAPHIC_SIZE, null, DL_GRAPHIC_CLASS));

        String text = this.downloadButton.getText();
        if (text != null) {
            this.downloadButton.setText(text + " ");
        }

        this.downloadButton.setOnAction(event -> {
            this.setResult(null);
            this.close();

            QEFXMainController mainController = this.controller.getMainController();
            if (mainController != null) {
                String url = Environments.getPseudoWebsite();
                mainController.showWebPage(url);
            }
        });
    }
}
