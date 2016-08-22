/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputModelController;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.AtomProperty;
import burai.atoms.model.Cell;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.com.math.Matrix3D;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QECard;
import burai.input.card.QECardEvent;

public class QEFXAtomsController extends QEFXInputModelController {

    private static final String UNIT_ALAT = "Alat";
    private static final String UNIT_BOHR = "Bohr";
    private static final String UNIT_ANGSTROM = "Angstrom";
    private static final String UNIT_CRYSTAL = "Crystal";

    public static final double PLUS_GRAPHIC_SIZE = 18.0;
    public static final String PLUS_GRAPHIC_CLASS = "piclight-button";

    private static final String CELL_STYLE_MOBILE = "-fx-text-fill: -fx-text-background-color";
    private static final String CELL_STYLE_FIXED = "-fx-text-fill: blue";

    private static final String MENU_TEXT_MOBILE = " is mobile";
    private static final String MENU_TEXT_DELETE = "Delete";

    private AtomAnsatzBinder atomBinder;

    private boolean busyUnitCombo;
    private boolean busyAtomicPositions;

    @FXML
    private ComboBox<String> unitCombo;

    @FXML
    private Button plusButton;

    @FXML
    private TableView<AtomAnsatz> atomTable;

    @FXML
    private TableColumn<AtomAnsatz, Integer> indexColumn;

    @FXML
    private TableColumn<AtomAnsatz, String> elementColumn;

    @FXML
    private TableColumn<AtomAnsatz, String> xColumn;

    @FXML
    private TableColumn<AtomAnsatz, String> yColumn;

    @FXML
    private TableColumn<AtomAnsatz, String> zColumn;

    public QEFXAtomsController(QEFXMainController mainController, QEInput input, Cell modelCell) {
        super(mainController, input, modelCell);
        this.atomBinder = null;
        this.busyUnitCombo = false;
        this.busyAtomicPositions = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initializeAtomBinder();
        this.setupUnitCombo();
        this.setupPlusButton();
        this.setupIndexColumn();
        this.setupElementColumn();
        this.setupXYZColumn("x", this.xColumn);
        this.setupXYZColumn("y", this.yColumn);
        this.setupXYZColumn("z", this.zColumn);
        this.setupAtomTable();
    }

    private void initializeAtomBinder() {
        if (this.atomTable == null) {
            return;
        }

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (!(card instanceof QEAtomicPositions)) {
            return;
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        this.atomBinder = new AtomAnsatzBinder(this.atomTable, atomicPositions);
    }

    private void setupUnitCombo() {
        if (this.unitCombo == null) {
            return;
        }

        this.unitCombo.getItems().clear();
        this.unitCombo.getItems().add(UNIT_ALAT);
        this.unitCombo.getItems().add(UNIT_BOHR);
        this.unitCombo.getItems().add(UNIT_ANGSTROM);
        this.unitCombo.getItems().add(UNIT_CRYSTAL);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (!(card instanceof QEAtomicPositions)) {
            return;
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        this.actionByAtomicPositions(atomicPositions);

        atomicPositions.addListener(event -> {
            int eventType = event.getEventType();
            if (eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED) {
                this.actionByAtomicPositions(atomicPositions);
            } else if (eventType == QECardEvent.EVENT_TYPE_NULL) {
                this.actionByAtomicPositions(atomicPositions);
            }
        });

        this.unitCombo.setOnAction(event -> this.actionByUnitCombo(atomicPositions));
    }

    private void actionByAtomicPositions(QEAtomicPositions atomicPositions) {
        if (this.busyUnitCombo) {
            return;
        }

        this.busyAtomicPositions = true;

        if (atomicPositions.isAlat()) {
            this.unitCombo.setValue(UNIT_ALAT);
        } else if (atomicPositions.isBohr()) {
            this.unitCombo.setValue(UNIT_BOHR);
        } else if (atomicPositions.isAngstrom()) {
            this.unitCombo.setValue(UNIT_ANGSTROM);
        } else if (atomicPositions.isCrystal()) {
            this.unitCombo.setValue(UNIT_CRYSTAL);
        }

        this.busyAtomicPositions = false;
    }

    private void actionByUnitCombo(QEAtomicPositions atomicPositions) {
        if (this.busyAtomicPositions) {
            return;
        }

        double[][] matrixOld = null;
        double[][] matrixNew = null;
        double[][] matrixEff = null;

        this.busyUnitCombo = true;

        matrixOld = this.input.getAngstromMatrix();

        String value = this.unitCombo.getValue();
        if (UNIT_ALAT.equals(value)) {
            atomicPositions.setAlat();
        } else if (UNIT_BOHR.equals(value)) {
            atomicPositions.setBohr();
        } else if (UNIT_ANGSTROM.equals(value)) {
            atomicPositions.setAngstrom();
        } else if (UNIT_CRYSTAL.equals(value)) {
            atomicPositions.setCrystal();
        }

        matrixNew = this.input.getAngstromInverse();
        matrixEff = null;
        if (matrixOld != null && matrixNew != null) {
            matrixEff = Matrix3D.mult(matrixNew, matrixOld);
        }

        this.busyUnitCombo = false;

        if (matrixEff == null) {
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Convert atomic positions ?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || !optButtonType.isPresent()) {
            return;
        }
        if (optButtonType.get() != ButtonType.YES) {
            return;
        }

        this.modelCell.stopBondResolving();

        int numAtoms = atomicPositions.numPositions();
        for (int i = 0; i < numAtoms; i++) {
            double[] position = atomicPositions.getPosition(i);
            if (position != null && position.length > 2) {
                position = Matrix3D.mult(matrixEff, position);
                atomicPositions.setPosition(i, position);
            }
        }

        this.modelCell.restartBondResolving();
    }

    private void setupPlusButton() {
        if (this.plusButton == null) {
            return;
        }

        this.plusButton.setText("");
        this.plusButton.getStyleClass().add(PLUS_GRAPHIC_CLASS);
        this.plusButton.setGraphic(SVGLibrary.getGraphic(SVGData.PLUS, PLUS_GRAPHIC_SIZE, null, PLUS_GRAPHIC_CLASS));

        this.plusButton.setOnAction(event -> {
            if (this.atomTable == null || this.atomBinder == null) {
                return;
            }

            int index = 0;
            for (AtomAnsatz atom : this.atomTable.getItems()) {
                if (atom != null) {
                    index = Math.max(index, atom.getIndex() + 1);
                }
            }

            QEFXAtomAddingDialog addingDialog = new QEFXAtomAddingDialog(index);
            Optional<AtomAnsatz> optAtom = addingDialog.showAndWait();
            if (optAtom == null || !optAtom.isPresent()) {
                return;
            }

            AtomAnsatz atom = optAtom.get();
            this.atomBinder.addAtom(atom);
        });
    }

    private void setupAtomTable() {
        if (this.atomTable == null) {
            return;
        }

        if (this.atomBinder != null) {
            this.atomBinder.bindTable();
        }

        ContextMenu contextMenu = this.createContextMenu();
        if (contextMenu != null) {
            this.atomTable.setContextMenu(contextMenu);
        }

        TableViewSelectionModel<AtomAnsatz> selectionModel = this.atomTable.getSelectionModel();

        if (selectionModel != null) {
            selectionModel.selectedItemProperty().addListener(o -> {
                Atom[] modelAtoms = this.modelCell.listAtoms();
                if (modelAtoms != null) {
                    for (Atom modelAtom : modelAtoms) {
                        if (modelAtom != null) {
                            modelAtom.setProperty(AtomProperty.SELECTED, false);
                        }
                    }
                }

                AtomAnsatz atom = selectionModel.getSelectedItem();
                if (atom != null) {
                    int index = atom.getIndex();
                    Atom modelAtom = QEInput.pickOutAtom(this.modelCell, index);
                    if (modelAtom != null) {
                        modelAtom.setProperty(AtomProperty.SELECTED, true);
                    }
                }
            });
        }
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        CheckMenuItem xMobileItem = this.createMobileMenuItem(0, "X");
        CheckMenuItem yMobileItem = this.createMobileMenuItem(1, "Y");
        CheckMenuItem zMobileItem = this.createMobileMenuItem(2, "Z");
        MenuItem deleteItem = this.createDeleteMenuItem();
        contextMenu.getItems().addAll(xMobileItem, yMobileItem, zMobileItem, deleteItem);

        contextMenu.setOnShowing(event -> {
            if (this.atomTable == null) {
                return;
            }

            AtomAnsatz atom = null;
            TableViewSelectionModel<AtomAnsatz> selectionModel = this.atomTable.getSelectionModel();
            if (selectionModel != null) {
                atom = selectionModel.getSelectedItem();
            }

            if (atom != null) {
                xMobileItem.setDisable(false);
                yMobileItem.setDisable(false);
                zMobileItem.setDisable(false);
                deleteItem.setDisable(false);
                xMobileItem.setSelected(atom.isXMobile(true));
                yMobileItem.setSelected(atom.isYMobile(true));
                zMobileItem.setSelected(atom.isZMobile(true));
            } else {
                xMobileItem.setDisable(true);
                yMobileItem.setDisable(true);
                zMobileItem.setDisable(true);
                deleteItem.setDisable(true);
                xMobileItem.setSelected(false);
                yMobileItem.setSelected(false);
                zMobileItem.setSelected(false);
            }
        });

        return contextMenu;
    }

    private CheckMenuItem createMobileMenuItem(int xyz, String name) {
        CheckMenuItem mobileItem = new CheckMenuItem(name + MENU_TEXT_MOBILE);

        mobileItem.setOnAction(event -> {
            if (this.atomTable == null || this.atomBinder == null) {
                return;
            }

            TableViewSelectionModel<AtomAnsatz> selectionModel = this.atomTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            AtomAnsatz atom = selectionModel.getSelectedItem();
            if (atom == null) {
                return;
            }

            boolean mobile = mobileItem.isSelected();
            if (xyz == 0) {
                atom.setXMobile(mobile);
            } else if (xyz == 1) {
                atom.setYMobile(mobile);
            } else if (xyz == 2) {
                atom.setZMobile(mobile);
            }
        });

        return mobileItem;
    }

    private MenuItem createDeleteMenuItem() {
        MenuItem deleteItem = new MenuItem(MENU_TEXT_DELETE);

        deleteItem.setOnAction(event -> {
            if (this.atomTable == null || this.atomBinder == null) {
                return;
            }

            TableViewSelectionModel<AtomAnsatz> selectionModel = this.atomTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            AtomAnsatz atom = selectionModel.getSelectedItem();
            if (atom == null) {
                return;
            }

            this.atomBinder.removeAtom(atom);
        });

        return deleteItem;
    }

    private void setupIndexColumn() {
        if (this.indexColumn == null) {
            return;
        }

        this.indexColumn.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, Integer>("index"));
    }

    private void setupElementColumn() {
        if (this.elementColumn == null) {
            return;
        }

        this.elementColumn.setCellFactory(TextFieldTableCell.<AtomAnsatz> forTableColumn());
        this.elementColumn.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, String>("element"));
        this.elementColumn.setComparator((String element1, String element2) -> {
            int atomNum1 = ElementUtil.getAtomicNumber(element1);
            int atomNum2 = ElementUtil.getAtomicNumber(element2);
            if (atomNum1 < atomNum2) {
                return -1;
            } else if (atomNum1 > atomNum2) {
                return 1;
            }
            return 0;
        });
    }

    private void setupXYZColumn(String name, TableColumn<AtomAnsatz, String> xyzColumn) {
        if (name == null || name.isEmpty()) {
            return;
        }

        if (xyzColumn == null) {
            return;
        }

        xyzColumn.setCellFactory(new XYZCellFactory());
        xyzColumn.setCellValueFactory(new PropertyValueFactory<AtomAnsatz, String>(name));
    }

    private static class XYZCellFactory implements
            Callback<TableColumn<AtomAnsatz, String>, TableCell<AtomAnsatz, String>> {

        private Callback<TableColumn<AtomAnsatz, String>, TableCell<AtomAnsatz, String>> callback;

        public XYZCellFactory() {
            this.callback = TextFieldTableCell.<AtomAnsatz> forTableColumn();
        }

        @Override
        public TableCell<AtomAnsatz, String> call(TableColumn<AtomAnsatz, String> param) {
            TableCell<AtomAnsatz, String> cell = this.callback.call(param);

            cell.setStyle(CELL_STYLE_MOBILE);

            cell.itemProperty().addListener(o -> {
                String item = cell.getItem();
                if (item == null) {
                    return;
                }

                String strPosition = AtomAnsatz.extractPosition(item);
                if (strPosition != null) {
                    cell.setItem(strPosition);
                }

                if (AtomAnsatz.containsMobile(item)) {
                    if (AtomAnsatz.extractMobile(item)) {
                        cell.setStyle(CELL_STYLE_MOBILE);
                    } else {
                        cell.setStyle(CELL_STYLE_FIXED);
                    }
                }
            });

            return cell;
        }
    }
}
