/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.band;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboInteger;
import burai.app.project.editor.input.items.QEFXItem;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.app.project.editor.input.items.WarningCondition;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QECardEvent;
import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;
import burai.input.correcter.BandCorrector;
import burai.input.correcter.BrillouinPathGenerator;
import burai.input.correcter.SymmetricKPointsGenerator;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;

public class QEFXBandController extends QEFXInputController {

    private static final String UNIT_2PIA = "2PI / A";
    private static final String UNIT_CRYSTAL = "Crystal";

    public static final double PLUS_GRAPHIC_SIZE = 18.0;
    public static final String PLUS_GRAPHIC_CLASS = "piclight-button";

    private static final String MENU_TEXT_ADD = "Add";
    private static final String MENU_TEXT_DELETE = "Delete";
    private static final String MENU_TEXT_UP = "Bring up";
    private static final String MENU_TEXT_DOWN = "Bring down";

    private KPointAnsatzBinder kpointBinder;

    private QEFXTextFieldInteger nbandItem;

    private boolean busyUnitCombo;
    private boolean busyKPoints;

    /*
    * nband
    */
    @FXML
    private Label nbandLabel;

    @FXML
    private TextField nbandField;

    @FXML
    private Button nbandButton;

    /*
     * symmetry of band
     */
    @FXML
    private Label symLabel;

    @FXML
    private ToggleButton symToggle;

    @FXML
    private Button symButton;

    /*
     * spin component
     */
    @FXML
    private Label spinLabel;

    @FXML
    private ComboBox<String> spinCombo;

    @FXML
    private Button spinButton;

    /*
     * K-points
     */
    @FXML
    private ComboBox<String> unitCombo;

    @FXML
    private Button defButton;

    @FXML
    private TableView<KPointAnsatz> kpointTable;

    @FXML
    private TableColumn<KPointAnsatz, String> symbolColumn;

    @FXML
    private TableColumn<KPointAnsatz, String> kxColumn;

    @FXML
    private TableColumn<KPointAnsatz, String> kyColumn;

    @FXML
    private TableColumn<KPointAnsatz, String> kzColumn;

    @FXML
    private TableColumn<KPointAnsatz, String> nkColumn;

    public QEFXBandController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        this.kpointBinder = null;
        this.nbandItem = null;
        this.busyUnitCombo = false;
        this.busyKPoints = false;
    }

    public void updateNBandStatus() {
        if (this.nbandItem != null) {
            this.nbandItem.pullAllTriggers();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QECard card = this.input.getCard(QEKPoints.CARD_NAME);
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        QENamelist nmlBands = this.input.getNamelist(QEInput.NAMELIST_BANDS);

        if (nmlSystem != null) {
            this.setupNBandItem(nmlSystem);
        }

        if (nmlBands != null) {
            this.setupSymItem(nmlBands);
            if (nmlSystem != null) {
                this.setupSpinItem(nmlBands, nmlSystem);
            }
        }

        if (card != null && card instanceof QEKPoints) {
            this.initializeKPointBinder((QEKPoints) card);
            this.setupUnitCombo((QEKPoints) card);
            this.setupDefButton((QEKPoints) card);
        }

        this.setupSymbolColumn();
        this.setupKxColumn();
        this.setupKyColumn();
        this.setupKzColumn();
        this.setupNkColumn();
        this.setupKPointTable();
    }

    private void setupNBandItem(QENamelist nmlSystem) {
        if (this.nbandField == null) {
            return;
        }

        BandCorrector corrector = new BandCorrector(this.input);

        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlSystem.getValueBuffer("nbnd"), this.nbandField);

        if (this.nbandLabel != null) {
            item.setLabel(this.nbandLabel);
        }

        if (this.nbandButton != null) {
            item.setDefault(() -> {
                int nband = corrector.isAvailable() ? corrector.getNumBands() : 0;
                if (nband > 0) {
                    return QEValueBase.getInstance("nbnd", nband);
                } else {
                    return null;
                }

            }, this.nbandButton);
        }

        item.setLowerBound(0, QEFXTextFieldInteger.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("nbnd".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.WARNING;
                }

                int nband = corrector.isAvailable() ? corrector.getNumBands() : 0;
                if (nband > 0) {
                    if (nband != value.getIntegerValue()) {
                        return WarningCondition.WARNING;
                    } else {
                        return WarningCondition.OK;
                    }
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.nbandItem = item;
    }

    private void setupSymItem(QENamelist nmlBands) {
        if (this.symToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlBands.getValueBuffer("lsym"), this.symToggle, false);

        if (this.symLabel != null) {
            item.setLabel(this.symLabel);
        }

        if (this.symButton != null) {
            item.setDefault(false, this.symButton);
        }
    }

    private void setupSpinItem(QENamelist nmlBands, QENamelist nmlSystem) {
        if (this.spinCombo == null) {
            return;
        }

        QEValueBuffer nspinValue = nmlSystem.getValueBuffer("nspin");
        QEValueBuffer spinCompValue = nmlBands.getValueBuffer("spin_component");

        this.spinCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(spinCompValue, this.spinCombo);

        if (this.spinLabel != null) {
            item.setLabel(this.spinLabel);
        }

        if (this.spinButton != null) {
            item.setDefault(1, this.spinButton);
        }

        item.addItems("1", "2");

        item.setValueFactory(text -> {
            return Integer.parseInt(text);
        });

        item.addWarningTrigger(nspinValue);
        item.addWarningCondition((name, value) -> {
            if ("nspin".equalsIgnoreCase(name) || "spin_component".equalsIgnoreCase(name)) {
                int nspin = nspinValue.hasValue() ? nspinValue.getIntegerValue() : 1;
                int spinComp = spinCompValue.hasValue() ? spinCompValue.getIntegerValue() : 1;
                if (nspin != 2 && spinComp == 2) {
                    return WarningCondition.ERROR;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void initializeKPointBinder(QEKPoints cardKPoints) {
        if (this.kpointTable == null) {
            return;
        }

        this.kpointBinder = new KPointAnsatzBinder(this.kpointTable, cardKPoints);
    }

    private void setupUnitCombo(QEKPoints cardKPoints) {
        if (this.unitCombo == null) {
            return;
        }

        this.unitCombo.getItems().clear();
        this.unitCombo.getItems().add(UNIT_2PIA);
        this.unitCombo.getItems().add(UNIT_CRYSTAL);

        this.actionByKPoints(cardKPoints);

        cardKPoints.addListener(event -> {
            int eventType = event.getEventType();
            if (eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED) {
                this.actionByKPoints(cardKPoints);
            } else if (eventType == QECardEvent.EVENT_TYPE_NULL) {
                this.actionByKPoints(cardKPoints);
            }
        });

        this.unitCombo.setOnAction(event -> this.actionByUnitCombo(cardKPoints));
    }

    private void actionByKPoints(QEKPoints cardKPoints) {
        if (this.busyUnitCombo) {
            return;
        }

        this.busyKPoints = true;

        if (cardKPoints.isTpibaB()) {
            this.unitCombo.setValue(UNIT_2PIA);
        } else if (cardKPoints.isCrystalB()) {
            this.unitCombo.setValue(UNIT_CRYSTAL);
        }

        this.busyKPoints = false;
    }

    private void actionByUnitCombo(QEKPoints cardKPoints) {
        if (this.busyKPoints) {
            return;
        }

        this.busyUnitCombo = true;

        String value = this.unitCombo.getValue();
        if (UNIT_2PIA.equals(value)) {
            cardKPoints.setTpibaB();
        } else if (UNIT_CRYSTAL.equals(value)) {
            cardKPoints.setCrystalB();
        }

        this.busyUnitCombo = false;
    }

    private void setupDefButton(QEKPoints cardKPoints) {
        if (this.defButton == null) {
            return;
        }

        QEFXItem.setupDefaultButton(this.defButton);

        this.defButton.setOnAction(event -> {
            List<QEKPoint> kpoints = null;
            BrillouinPathGenerator generator = new BrillouinPathGenerator(this.input);
            if (generator.isAvailable()) {
                kpoints = generator.getKPoints();
            }

            if (kpoints != null && (!kpoints.isEmpty())) {
                cardKPoints.clear();
                cardKPoints.setTpibaB();
                for (QEKPoint kpoint : kpoints) {
                    if (kpoint != null) {
                        cardKPoints.addKPoint(kpoint);
                    }
                }
            }
        });
    }

    private void setupKPointTable() {
        if (this.kpointTable == null) {
            return;
        }

        if (this.kpointBinder != null) {
            this.kpointBinder.bindTable();
        }

        ContextMenu contextMenu = this.createContextMenu();
        if (contextMenu != null) {
            this.kpointTable.setContextMenu(contextMenu);
        }
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addItem = this.createAddMenuItem();
        MenuItem deleteItem = this.createDeleteMenuItem();
        MenuItem upItem = this.createUpMenuItem();
        MenuItem downItem = this.createDownMenuItem();
        contextMenu.getItems().addAll(addItem, deleteItem, upItem, downItem);
        return contextMenu;
    }

    private MenuItem createAddMenuItem() {
        MenuItem menuItem = new MenuItem(MENU_TEXT_ADD);

        menuItem.setOnAction(event -> {
            if (this.kpointTable == null || this.kpointBinder == null) {
                return;
            }

            TableViewSelectionModel<KPointAnsatz> selectionModel = this.kpointTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            int index = selectionModel.getSelectedIndex();
            KPointAnsatz kpointAnsatz = new KPointAnsatz(Math.max(0, index + 1));
            kpointAnsatz.setSymbol("");
            kpointAnsatz.setKx(0.0);
            kpointAnsatz.setKy(0.0);
            kpointAnsatz.setKz(0.0);
            kpointAnsatz.setNk(20);

            if (index > -1) {
                this.kpointBinder.addKPoint(kpointAnsatz, index);
            } else {
                this.kpointBinder.addKPoint(kpointAnsatz);
            }
        });

        return menuItem;
    }

    private MenuItem createDeleteMenuItem() {
        MenuItem menuItem = new MenuItem(MENU_TEXT_DELETE);

        menuItem.setOnAction(event -> {
            if (this.kpointTable == null || this.kpointBinder == null) {
                return;
            }

            TableViewSelectionModel<KPointAnsatz> selectionModel = this.kpointTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            KPointAnsatz kpointAnsatz = selectionModel.getSelectedItem();
            if (kpointAnsatz == null) {
                return;
            }

            this.kpointBinder.removeKPoint(kpointAnsatz);
        });

        return menuItem;
    }

    private MenuItem createUpMenuItem() {
        MenuItem menuItem = new MenuItem(MENU_TEXT_UP);

        menuItem.setOnAction(event -> {
            if (this.kpointTable == null || this.kpointBinder == null) {
                return;
            }

            TableViewSelectionModel<KPointAnsatz> selectionModel = this.kpointTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            List<KPointAnsatz> items = this.kpointTable.getItems();

            int index1 = selectionModel.getSelectedIndex();
            if (index1 < 0 || items.size() <= index1) {
                return;
            }

            int index2 = index1 - 1;
            if (index2 < 0 || items.size() <= index2) {
                return;
            }

            KPointAnsatz kpointAnsatz1 = items.get(index1);
            KPointAnsatz kpointAnsatz2 = items.get(index2);
            this.kpointBinder.swapKPoints(kpointAnsatz1, kpointAnsatz2);

            selectionModel.select(index2);
        });

        return menuItem;
    }

    private MenuItem createDownMenuItem() {
        MenuItem menuItem = new MenuItem(MENU_TEXT_DOWN);

        menuItem.setOnAction(event -> {
            if (this.kpointTable == null || this.kpointBinder == null) {
                return;
            }

            TableViewSelectionModel<KPointAnsatz> selectionModel = this.kpointTable.getSelectionModel();
            if (selectionModel == null) {
                return;
            }

            List<KPointAnsatz> items = this.kpointTable.getItems();

            int index1 = selectionModel.getSelectedIndex();
            if (index1 < 0 || items.size() <= index1) {
                return;
            }

            int index2 = index1 + 1;
            if (index2 < 0 || items.size() <= index2) {
                return;
            }

            KPointAnsatz kpointAnsatz1 = items.get(index1);
            KPointAnsatz kpointAnsatz2 = items.get(index2);
            this.kpointBinder.swapKPoints(kpointAnsatz1, kpointAnsatz2);

            selectionModel.select(index2);
        });

        return menuItem;
    }

    private void setupSymbolColumn() {
        if (this.symbolColumn == null) {
            return;
        }

        this.symbolColumn.setCellFactory(column -> {
            double width = column.getWidth();
            return new SymbolCell(this, width);
        });

        this.symbolColumn.setCellValueFactory(new PropertyValueFactory<KPointAnsatz, String>("symbol"));
    }

    private void setupKxColumn() {
        if (this.kxColumn == null) {
            return;
        }

        this.kxColumn.setCellFactory(TextFieldTableCell.<KPointAnsatz> forTableColumn());
        this.kxColumn.setCellValueFactory(new PropertyValueFactory<KPointAnsatz, String>("kx"));
    }

    private void setupKyColumn() {
        if (this.kyColumn == null) {
            return;
        }

        this.kyColumn.setCellFactory(TextFieldTableCell.<KPointAnsatz> forTableColumn());
        this.kyColumn.setCellValueFactory(new PropertyValueFactory<KPointAnsatz, String>("ky"));
    }

    private void setupKzColumn() {
        if (this.kzColumn == null) {
            return;
        }

        this.kzColumn.setCellFactory(TextFieldTableCell.<KPointAnsatz> forTableColumn());
        this.kzColumn.setCellValueFactory(new PropertyValueFactory<KPointAnsatz, String>("kz"));
    }

    private void setupNkColumn() {
        if (this.nkColumn == null) {
            return;
        }

        this.nkColumn.setCellFactory(TextFieldTableCell.<KPointAnsatz> forTableColumn());
        this.nkColumn.setCellValueFactory(new PropertyValueFactory<KPointAnsatz, String>("nk"));
    }

    private static class SymbolCell extends TableCell<KPointAnsatz, String> {
        private QEFXBandController root;

        private double width;
        private ComboBox<String> combo;

        public SymbolCell(QEFXBandController root, double width) {
            super();
            if (root == null) {
                throw new IllegalArgumentException("root is null.");
            }
            if (width <= 0.0) {
                throw new IllegalArgumentException("width is not positive.");
            }

            this.root = root;
            this.width = width;
            this.combo = null;
        }

        private ComboBox<String> getCombo() {
            if (this.combo == null) {
                this.combo = new ComboBox<String>();
                this.combo.setPrefWidth(this.width);
                this.combo.setFocusTraversable(false);
                this.combo.setOnShowing(event -> this.initializeComboItems());
                this.combo.setOnAction(event -> this.setKPointAnsatz());
            }

            return this.combo;
        }

        private void initializeComboItems() {
            List<String> items = this.getCombo().getItems();
            items.clear();
            items.add("");

            SymmetricKPointsGenerator generator = new SymmetricKPointsGenerator(this.root.input);
            List<QEKPoint> kpoints = generator.getKPoints();
            if (kpoints != null) {
                for (QEKPoint kpoint : kpoints) {
                    String symbol = null;
                    if (kpoint != null && kpoint.hasLetter()) {
                        symbol = kpoint.getLetter();
                    }
                    if (symbol != null && (!symbol.isEmpty())) {
                        items.add(symbol);
                    }
                }
            }
        }

        private void setKPointAnsatz() {
            String value = this.getCombo().getValue();
            if (value != null) {
                @SuppressWarnings("unchecked")
                TableRow<KPointAnsatz> tableRow = this.getTableRow();
                KPointAnsatz kpointAnsatz = tableRow == null ? null : tableRow.getItem();
                if (kpointAnsatz != null) {
                    if (this.root.kpointBinder != null) {
                        this.root.kpointBinder.setKPointSymbol(kpointAnsatz, value);
                    }
                }
            }
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!empty) {
                String value = item == null ? "" : item;
                this.getCombo().setValue(value);
                this.setGraphic(this.getCombo());

            } else {
                this.setGraphic(null);
            }
        }
    }
}
