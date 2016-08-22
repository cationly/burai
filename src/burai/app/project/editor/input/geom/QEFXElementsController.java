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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputModelController;
import burai.app.project.editor.input.items.QEFXItem;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Cell;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.input.QEInput;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.correcter.CutoffCorrector;
import burai.pseudo.PseudoData;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEFXElementsController extends QEFXInputModelController {

    private static final String PSEUDO_BUTTON_STYLE = "-fx-font-size: 0.916667em";

    private static final double CHECK_GRAPHIC_SIZE = 10.0;
    private static final String CHECK_GRAPHIC_CLASS = "pictured-button";

    private static final String XCFUNC_ERROR_LABEL = "NOT-CONSISTENT";
    private static final String XCFUNC_ERROR_STYLE = "-fx-text-fill: red; -fx-font-weight: bold";

    private static final double DEFAULT_ECUTWFC = 25.0; // 25Ry
    private static final double DEFAULT_ECUTRHO = 225.0; // 225Ry

    private ElementAnsatzBinder elementBinder;

    private CutoffCorrector cutoffCorrector;

    @FXML
    private Button defButton;

    @FXML
    private TableView<ElementAnsatz> elementTable;

    @FXML
    private TableColumn<ElementAnsatz, Integer> indexColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> nameColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> massColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> pseudoColumn;

    @FXML
    private Label ppTypePoint;

    @FXML
    private Label ppTypeLabel;

    @FXML
    private Label xcFuncPoint;

    @FXML
    private Label xcFuncLabel;

    @FXML
    private Label ecutwfcPoint;

    @FXML
    private Label ecutwfcLabel;

    @FXML
    private Label ecutrhoPoint;

    @FXML
    private Label ecutrhoLabel;

    public QEFXElementsController(QEFXMainController mainController, QEInput input, Cell modelCell) {
        super(mainController, input, modelCell);
        this.elementBinder = null;
        this.cutoffCorrector = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initializeElementBinder();
        this.setupDefButton();
        this.setupIndexColumn();
        this.setupNameColumn();
        this.setupMassColumn();
        this.setupPseudoColumn();
        this.setupElementTable();
        this.setupPseudoConditions();
        this.setupAtomicSpecies();
    }

    private void initializeElementBinder() {
        if (this.elementTable == null) {
            return;
        }

        QECard card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicSpecies)) {
            return;
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;

        this.elementBinder = new ElementAnsatzBinder(this.elementTable, atomicSpecies);
    }

    private void setupDefButton() {
        if (this.defButton == null) {
            return;
        }

        QEFXItem.setupDefaultButton(this.defButton);

        this.defButton.setOnAction(event -> {
            if (this.elementTable == null) {
                return;
            }

            List<ElementAnsatz> elements = this.elementTable.getItems();
            if (elements == null) {
                return;
            }

            for (ElementAnsatz element : elements) {
                String name = element.getName();
                String elementName = ElementUtil.toElementName(name);

                if (elementName != null) {
                    element.setMass(ElementUtil.getMass(elementName));

                    PseudoPotential pseudoPot = PseudoLibrary.getInstance().getPseudoPotential(elementName);
                    String pseudoName = null;
                    if (pseudoPot != null) {
                        pseudoName = pseudoPot.getName();
                    }
                    if (pseudoName != null) {
                        element.setPseudo(pseudoName);
                    }
                }
            }
        });
    }

    private void setupElementTable() {
        if (this.elementTable == null) {
            return;
        }

        if (this.elementBinder != null) {
            this.elementBinder.bindTable();
        }
    }

    private void setupIndexColumn() {
        if (this.indexColumn == null) {
            return;
        }

        this.indexColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, Integer>("index"));
    }

    private void setupNameColumn() {
        if (this.nameColumn == null) {
            return;
        }

        this.nameColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("name"));
        this.nameColumn.setComparator((String name1, String name2) -> {
            int atomNum1 = ElementUtil.getAtomicNumber(name1);
            int atomNum2 = ElementUtil.getAtomicNumber(name2);
            if (atomNum1 < atomNum2) {
                return -1;
            } else if (atomNum1 > atomNum2) {
                return 1;
            }
            return 0;
        });
    }

    private void setupMassColumn() {
        if (this.massColumn == null) {
            return;
        }

        this.massColumn.setCellFactory(TextFieldTableCell.<ElementAnsatz> forTableColumn());
        this.massColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("mass"));
    }

    private void setupPseudoColumn() {
        if (this.pseudoColumn == null) {
            return;
        }

        this.pseudoColumn.setCellFactory(column -> {
            double width = column.getWidth();
            return new PseudoCell(this, width);
        });

        this.pseudoColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("pseudo"));
    }

    private static class PseudoCell extends TableCell<ElementAnsatz, String> {
        private QEFXElementsController root;

        private double width;
        private Button button;

        public PseudoCell(QEFXElementsController root, double width) {
            super();
            if (root == null) {
                throw new IllegalArgumentException("root is null.");
            }
            if (width <= 0.0) {
                throw new IllegalArgumentException("width is not positive.");
            }

            this.root = root;
            this.width = width;
            this.button = null;
        }

        private Button getButton() {
            if (this.button == null) {
                this.button = new Button();
                this.button.setPrefWidth(this.width);
                this.button.setFocusTraversable(false);
                this.button.setOnAction(event -> this.showPseudoDialog());
            }

            return this.button;
        }

        private void showPseudoDialog() {
            @SuppressWarnings("unchecked")
            TableRow<ElementAnsatz> tableRow = this.getTableRow();
            ElementAnsatz element = tableRow == null ? null : tableRow.getItem();
            String elementName = element == null ? null : element.getName();
            String elementPseudo = element == null ? null : element.getPseudo();
            if (elementName == null) {
                return;
            }

            QEFXPseudoDialog dialog = new QEFXPseudoDialog(this.root, elementName, elementPseudo);
            Optional<PseudoPotential> optPseudoPot = dialog.showAndWait();
            if (optPseudoPot == null || !optPseudoPot.isPresent()) {
                return;
            }
            PseudoPotential pseudoPot = optPseudoPot.get();
            if (pseudoPot == null) {
                return;
            }
            String pseudoName = pseudoPot.getName();
            if (pseudoName != null && !pseudoName.trim().isEmpty()) {
                if (element != null) {
                    element.setPseudo(pseudoName);
                }
            }
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!empty) {
                String pseudoPath = null;
                PseudoPotential pseudoPot = null;
                if (item != null && !item.isEmpty()) {
                    pseudoPot = PseudoLibrary.getInstance().peekPseudoPotential(item);
                }
                if (pseudoPot != null && pseudoPot.isAvairable()) {
                    pseudoPath = pseudoPot.getPath();
                    this.getButton().setStyle(PSEUDO_BUTTON_STYLE);
                } else {
                    this.getButton().setStyle(PSEUDO_BUTTON_STYLE + ";" + QEFXItem.ERROR_STYLE);
                }
                this.getButton().setText(item);
                this.getButton().setTooltip(pseudoPath == null ? null : new Tooltip(pseudoPath));
                this.setGraphic(this.getButton());

            } else {
                this.setGraphic(null);
            }
        }
    }

    private void setupPseudoConditions() {
        if (this.ppTypePoint != null) {
            this.ppTypePoint.setText("");
            this.ppTypePoint.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CHECK, CHECK_GRAPHIC_SIZE, null, CHECK_GRAPHIC_CLASS));
        }

        if (this.xcFuncPoint != null) {
            this.xcFuncPoint.setText("");
            this.xcFuncPoint.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CHECK, CHECK_GRAPHIC_SIZE, null, CHECK_GRAPHIC_CLASS));
        }

        if (this.ecutwfcPoint != null) {
            this.ecutwfcPoint.setText("");
            this.ecutwfcPoint.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CHECK, CHECK_GRAPHIC_SIZE, null, CHECK_GRAPHIC_CLASS));
        }

        if (this.ecutrhoPoint != null) {
            this.ecutrhoPoint.setText("");
            this.ecutrhoPoint.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CHECK, CHECK_GRAPHIC_SIZE, null, CHECK_GRAPHIC_CLASS));
        }

        this.updatePseudoConditions();
    }

    private void setupAtomicSpecies() {
        QECard card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        if (!(card instanceof QEAtomicSpecies)) {
            return;
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;
        atomicSpecies.addListener(event -> this.updatePseudoConditions());
    }

    private void updatePseudoConditions() {
        QECard card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        if (!(card instanceof QEAtomicSpecies)) {
            return;
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;

        boolean hasPAW = false;
        boolean hasUS = false;
        boolean hasNC = false;
        String xcName = null;

        int numElems = atomicSpecies.numSpecies();
        for (int i = 0; i < numElems; i++) {
            PseudoPotential pseudoPot = atomicSpecies.getPseudoPotential(i);

            int pseudoType = PseudoData.PSEUDO_TYPE_UNKNOWN;
            String xcName2 = XCFUNC_ERROR_LABEL;
            if (pseudoPot != null && pseudoPot.isAvairable()) {
                pseudoType = pseudoPot.getData().getPseudoType();
                xcName2 = pseudoPot.getData().getFunctionalName();
            }

            if (pseudoType == PseudoData.PSEUDO_TYPE_PAW) {
                hasPAW = true;
            } else if (pseudoType == PseudoData.PSEUDO_TYPE_US) {
                hasUS = true;
            } else if (pseudoType == PseudoData.PSEUDO_TYPE_NC) {
                hasNC = true;
            }

            if (xcName == null) {
                xcName = xcName2;
            } else if (!xcName.equals(xcName2)) {
                xcName = XCFUNC_ERROR_LABEL;
            }
        }

        if (this.ppTypeLabel != null) {
            String text = null;
            if (hasPAW) {
                text = "PAW";
            }
            if (hasUS) {
                if (text == null) {
                    text = "USPP";
                } else {
                    text = text + " & USPP";
                }
            }
            if (hasNC) {
                if (text == null) {
                    text = "NCPP";
                } else {
                    text = text + " & NCPP";
                }
            }
            if (text != null) {
                this.ppTypeLabel.setText(text);
            } else {
                this.ppTypeLabel.setText("");
            }
        }

        if (this.xcFuncLabel != null) {
            if (!XCFUNC_ERROR_LABEL.equals(xcName)) {
                xcFuncLabel.setStyle(null);
            } else {
                xcFuncLabel.setStyle(XCFUNC_ERROR_STYLE);
            }
            if (xcName != null) {
                this.xcFuncLabel.setText(xcName);
            } else {
                this.xcFuncLabel.setText("");
            }
        }

        if (this.cutoffCorrector == null) {
            this.cutoffCorrector = new CutoffCorrector(this.input);
        }
        double ecutWfc = cutoffCorrector.isAvailable() ? cutoffCorrector.getCutoffOfWF() : DEFAULT_ECUTWFC;
        double ecutRho = cutoffCorrector.isAvailable() ? cutoffCorrector.getCutoffOfCharge() : DEFAULT_ECUTRHO;

        if (this.ecutwfcLabel != null) {
            this.ecutwfcLabel.setText(String.format("%9.3f", ecutWfc) + " Ry");
        }
        if (this.ecutrhoLabel != null) {
            this.ecutrhoLabel.setText(String.format("%9.3f", ecutRho) + " Ry");
        }
    }
}
