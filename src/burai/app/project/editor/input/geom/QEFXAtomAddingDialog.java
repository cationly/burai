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
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import burai.app.QEFXMain;
import burai.app.project.editor.input.items.QEFXItem;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.exception.IncorrectAtomNameException;
import burai.atoms.viewer.operation.editor.ElementButton;
import burai.atoms.viewer.operation.editor.PeriodicTable;
import burai.com.graphic.ToggleGraphics;
import burai.com.math.Calculator;

public class QEFXAtomAddingDialog extends Dialog<AtomAnsatz> implements Initializable {

    private static final String TOGGLE_STYLE = "-fx-base: transparent";
    private static final double GRAPHIC_WIDTH = 130.0;
    private static final double GRAPHIC_HEIGHT = 24.0;
    private static final String GRAPHIC_TEXT_MOBILE = "mobile";
    private static final String GRAPHIC_TEXT_FIXED = "fixed";
    private static final String GRAPHIC_STYLE_MOBILE = "toggle-graphic-mobile";
    private static final String GRAPHIC_STYLE_FIXED = "toggle-graphic-fixed";

    private int index;

    private AtomAnsatz atomToAdd;

    private String orgElemSelectButton;

    private String orgXField;

    private String orgYField;

    private String orgZField;

    @FXML
    private Button elemSelectButtton;

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;

    @FXML
    private TextField zField;

    @FXML
    private ToggleButton xToggle;

    @FXML
    private ToggleButton yToggle;

    @FXML
    private ToggleButton zToggle;

    public QEFXAtomAddingDialog(int index) {
        super();

        this.index = index;
        this.atomToAdd = null;
        this.orgElemSelectButton = null;
        this.orgXField = null;
        this.orgYField = null;
        this.orgZField = null;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Add an atom");
        this.setHeaderText("Set propeties of an atom to add.");
        this.setupButtonTypes(false);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXAtomAddingDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                return this.atomToAdd;
            }
            return null;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXAtomAddingDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    private void setupButtonTypes(boolean withOK) {
        DialogPane dialogPane = this.getDialogPane();
        if (dialogPane == null) {
            return;
        }

        dialogPane.getButtonTypes().clear();
        if (withOK) {
            dialogPane.getButtonTypes().add(ButtonType.OK);
        }
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
    }

    private boolean isCorrectElement() {
        if (this.elemSelectButtton != null) {
            String text = this.elemSelectButtton.getText();
            try {
                ElementUtil.obtainAtomicNumber(text);
            } catch (IncorrectAtomNameException e) {
                return false;
            }
        }

        return true;
    }

    private boolean isCorrectXYZ(TextField xyzField, ToggleButton xyzToggle) {
        if (xyzField != null && xyzToggle != null) {
            try {
                Calculator.expr(xyzField.getText());
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private AtomAnsatz createAtom() {
        AtomAnsatz atom = new AtomAnsatz(this.index);

        if (this.elemSelectButtton != null) {
            String text = this.elemSelectButtton.getText();
            try {
                ElementUtil.obtainAtomicNumber(text);
            } catch (IncorrectAtomNameException e) {
                return null;
            }
            atom.setElement(text);
        }

        if (this.xField != null && this.xToggle != null) {
            double value = 0.0;
            try {
                value = Calculator.expr(this.xField.getText());
            } catch (Exception e) {
                return null;
            }
            boolean mobile = !this.xToggle.isSelected();
            atom.setX(value, mobile);
        }

        if (this.yField != null && this.yToggle != null) {
            double value = 0.0;
            try {
                value = Calculator.expr(this.yField.getText());
            } catch (Exception e) {
                return null;
            }
            boolean mobile = !this.yToggle.isSelected();
            atom.setY(value, mobile);
        }

        if (this.zField != null && this.zToggle != null) {
            double value = 0.0;
            try {
                value = Calculator.expr(this.zField.getText());
            } catch (Exception e) {
                return null;
            }
            boolean mobile = !this.zToggle.isSelected();
            atom.setZ(value, mobile);
        }

        return atom;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.createOrgStyles();
        this.setupElementButton();
        this.setupXYZ(this.xField, this.xToggle, this.orgXField);
        this.setupXYZ(this.yField, this.yToggle, this.orgYField);
        this.setupXYZ(this.zField, this.zToggle, this.orgZField);
    }

    private void createOrgStyles() {
        if (this.elemSelectButtton != null) {
            this.orgElemSelectButton = this.elemSelectButtton.getStyle();
        }

        if (this.xField != null) {
            this.orgXField = this.xField.getStyle();
        }

        if (this.yField != null) {
            this.orgYField = this.yField.getStyle();
        }

        if (this.zField != null) {
            this.orgZField = this.zField.getStyle();
        }
    }

    private void setupElementButton() {
        if (this.elemSelectButtton == null) {
            return;
        }

        if (this.isCorrectElement()) {
            this.elemSelectButtton.setStyle(this.orgElemSelectButton);
        } else {
            this.elemSelectButtton.setStyle(QEFXItem.ERROR_STYLE);
        }

        this.elemSelectButtton.setOnAction(event -> {
            PeriodicTable periodicTable = new PeriodicTable();
            Optional<ElementButton> optElementButton = periodicTable.showAndWait();
            if (optElementButton == null || !optElementButton.isPresent()) {
                return;
            }

            ElementButton elementButton = optElementButton.get();
            String elementName = elementButton.getText();
            if (elementName == null || elementName.isEmpty()) {
                return;
            }

            this.elemSelectButtton.setText(elementName);

            if (this.isCorrectElement()) {
                this.elemSelectButtton.setStyle(this.orgElemSelectButton);
            } else {
                this.elemSelectButtton.setStyle(QEFXItem.ERROR_STYLE);
            }

            this.atomToAdd = this.createAtom();
            this.setupButtonTypes(this.atomToAdd != null);
        });
    }

    private void setupXYZ(TextField xyzField, ToggleButton xyzToggle, String orgField) {
        if (xyzField == null) {
            return;
        }

        if (xyzToggle == null) {
            return;
        }

        if (this.isCorrectXYZ(xyzField, xyzToggle)) {
            xyzField.setStyle(orgField);
        } else {
            xyzField.setStyle(QEFXItem.ERROR_STYLE);
        }

        xyzField.textProperty().addListener(o -> {
            if (this.isCorrectXYZ(xyzField, xyzToggle)) {
                xyzField.setStyle(orgField);
            } else {
                xyzField.setStyle(QEFXItem.ERROR_STYLE);
            }

            this.atomToAdd = this.createAtom();
            this.setupButtonTypes(this.atomToAdd != null);
            xyzField.requestFocus();
        });

        xyzToggle.setText("");
        xyzToggle.setStyle(TOGGLE_STYLE);
        this.updateXYZToggle(xyzToggle);

        xyzToggle.selectedProperty().addListener(o -> {
            this.updateXYZToggle(xyzToggle);
            this.atomToAdd = this.createAtom();
            this.setupButtonTypes(this.atomToAdd != null);
        });
    }

    private void updateXYZToggle(ToggleButton xyzToggle) {
        if (xyzToggle == null) {
            return;
        }

        if (xyzToggle.isSelected()) {
            xyzToggle.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, false, GRAPHIC_TEXT_FIXED, GRAPHIC_STYLE_FIXED));
        } else {
            xyzToggle.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, true, GRAPHIC_TEXT_MOBILE, GRAPHIC_STYLE_MOBILE));
        }
    }
}
