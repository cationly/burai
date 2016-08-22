/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import burai.com.consts.ConstantStyles;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;
import burai.input.namelist.QEValueWrapper;

public abstract class QEFXItem<C extends Control> {

    public static final String ERROR_STYLE = ConstantStyles.ERROR_COLOR;

    public static final String WARNING_STYLE = ConstantStyles.WARNING_COLOR;

    public static final double HELP_GRAPHIC_SIZE = 20.0;

    public static final String HELP_GRAPHIC_CLASS = "help-button";

    public static void setupDefaultButton(Button button) {
        if (button != null) {
            button.setText("");
            button.getStyleClass().add(HELP_GRAPHIC_CLASS);
            button.setGraphic(SVGLibrary.getGraphic(SVGData.INFO, HELP_GRAPHIC_SIZE, null, HELP_GRAPHIC_CLASS));
        }
    }

    private boolean editting;

    protected QEValueBuffer valueBuffer;

    protected C controlItem;

    protected Label label;

    protected QEValueWrapper defaultValue;

    protected Button defaultButton;

    private List<EnabledCondition> enabledConditions;

    private List<QEValueBuffer> enablingTriggers;

    private List<WarningCondition> warningConditions;

    private List<QEValueBuffer> warningTriggers;

    private String originalStyle;

    private int warningStatus;

    protected QEFXItem(QEValueBuffer valueBuffer, C controlItem) {
        if (valueBuffer == null) {
            throw new IllegalArgumentException("valueBuffer is null.");
        }

        if (controlItem == null) {
            throw new IllegalArgumentException("controlItem is null.");
        }

        this.editting = false;
        this.valueBuffer = valueBuffer;
        this.valueBuffer.addListener(value -> {
            if (!this.editting) {
                this.editting = true;
                this.onValueChanged(value);
                this.editting = false;
            }
        });

        this.controlItem = controlItem;

        this.label = null;
        this.defaultValue = null;
        this.defaultButton = null;
        this.enabledConditions = null;
        this.enablingTriggers = null;
        this.warningConditions = null;
        this.warningTriggers = null;
        this.originalStyle = this.controlItem.getStyle();
        this.warningStatus = WarningCondition.OK;

        this.addWarningTrigger(this.valueBuffer);
    }

    protected abstract void onValueChanged(QEValue value);

    public boolean hasValue() {
        return this.valueBuffer.hasValue();
    }

    public QEValue getValue() {
        return this.valueBuffer.getValue();
    }

    public void setValue(QEValue value) {
        this.valueBuffer.setValue(value);
    }

    public void setValue(int i) {
        this.valueBuffer.setValue(i);
    }

    public void setValue(double x) {
        this.valueBuffer.setValue(x);
    }

    public void setValue(boolean b) {
        this.valueBuffer.setValue(b);
    }

    public void setValue(String s) {
        this.valueBuffer.setValue(s);
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setDefault(int i, Button button) {
        this.setDefault(QEValueBase.getInstance(this.valueBuffer.getName(), i), button);
    }

    public void setDefault(double x, Button button) {
        this.setDefault(QEValueBase.getInstance(this.valueBuffer.getName(), x), button);
    }

    public void setDefault(boolean b, Button button) {
        this.setDefault(QEValueBase.getInstance(this.valueBuffer.getName(), b), button);
    }

    public void setDefault(String s, Button button) {
        this.setDefault(QEValueBase.getInstance(this.valueBuffer.getName(), s), button);
    }

    public void setDefault(QEValue value, Button button) {
        QEValueWrapper valueWrapper = () -> {
            return value;
        };

        this.setDefault(valueWrapper, button);
    }

    public void setDefault(QEValueWrapper valueWrapper, Button button) {
        this.defaultValue = valueWrapper;
        this.defaultButton = button;

        if (this.defaultButton != null) {
            setupDefaultButton(this.defaultButton);

            this.defaultButton.setOnAction(event -> {
                if (this.defaultValue != null) {
                    QEValue value = this.defaultValue.getQEValue();
                    if (value != null) {
                        this.valueBuffer.setValue(value);
                    } else {
                        this.valueBuffer.removeValue();
                    }
                }

                if (this.controlItem != null) {
                    this.controlItem.requestFocus();
                }
            });
        }
    }

    public void pullAllTriggers() {
        if (this.enablingTriggers != null) {
            for (QEValueBuffer trigger : this.enablingTriggers) {
                if (trigger != null) {
                    this.updateDisable(trigger.getName(), trigger.getValue());
                }
            }
        }

        if (this.warningConditions != null) {
            for (QEValueBuffer trigger : this.warningTriggers) {
                if (trigger != null) {
                    this.updateWarningStyle(trigger.getName(), trigger.getValue());
                }
            }
        }
    }

    public void addEnabledCondition(EnabledCondition condition) {
        if (condition == null) {
            return;
        }

        if (this.enabledConditions == null) {
            this.enabledConditions = new ArrayList<EnabledCondition>();
        }

        this.enabledConditions.add(condition);

        if (this.enablingTriggers != null) {
            for (QEValueBuffer trigger : this.enablingTriggers) {
                if (trigger != null) {
                    this.updateDisable(trigger.getName(), trigger.getValue());
                }
            }
        }
    }

    public void addEnablingTrigger(QEValueBuffer trigger) {
        if (trigger == null) {
            return;
        }

        if (this.enablingTriggers == null) {
            this.enablingTriggers = new ArrayList<QEValueBuffer>();
        }

        this.enablingTriggers.add(trigger);

        this.updateDisable(trigger.getName(), trigger.getValue());

        trigger.addListener(value -> {
            this.updateDisable(trigger.getName(), value);
        });
    }

    public void addWarningCondition(WarningCondition condition) {
        if (condition == null) {
            return;
        }

        if (this.warningConditions == null) {
            this.warningConditions = new ArrayList<WarningCondition>();
        }

        this.warningConditions.add(condition);

        if (this.warningConditions != null) {
            for (QEValueBuffer trigger : this.warningTriggers) {
                if (trigger != null) {
                    this.updateWarningStyle(trigger.getName(), trigger.getValue());
                }
            }
        }
    }

    public void addWarningTrigger(QEValueBuffer trigger) {
        if (trigger == null) {
            return;
        }

        if (this.warningTriggers == null) {
            this.warningTriggers = new ArrayList<QEValueBuffer>();
        }

        this.warningTriggers.add(trigger);

        this.updateWarningStyle(trigger.getName(), trigger.getValue());

        trigger.addListener(value -> {
            this.updateWarningStyle(trigger.getName(), value);
        });
    }

    private void updateDisable(String name, QEValue value) {
        if (this.enabledConditions == null) {
            return;
        }

        for (EnabledCondition enabledCondition : this.enabledConditions) {
            if (enabledCondition == null) {
                continue;
            }

            if (!enabledCondition.isEnabled(name, value)) {
                this.setDisable(true);
                return;
            }
        }

        this.setDisable(false);
    }

    private void updateWarningStyle(String name, QEValue value) {
        if (this.warningConditions == null) {
            return;
        }

        String currentStyle = this.controlItem.getStyle();
        if ((!ERROR_STYLE.equals(currentStyle)) && (!WARNING_STYLE.equals(currentStyle))) {
            this.originalStyle = currentStyle;
        }

        this.warningStatus = WarningCondition.OK;

        for (WarningCondition warningCondition : this.warningConditions) {
            if (warningCondition == null) {
                continue;
            }

            int status = warningCondition.getStatus(name, value);

            if (this.warningStatus == WarningCondition.ERROR) {
                break;
            } else if (this.warningStatus == WarningCondition.WARNING) {
                if (status == WarningCondition.ERROR) {
                    this.warningStatus = status;
                    break;
                }
            } else {
                if (status == WarningCondition.ERROR) {
                    this.warningStatus = status;
                    break;
                } else if (status == WarningCondition.WARNING) {
                    this.warningStatus = status;
                }
            }
        }

        if (this.warningStatus == WarningCondition.ERROR) {
            this.controlItem.setStyle(ERROR_STYLE);
        } else if (this.warningStatus == WarningCondition.WARNING) {
            this.controlItem.setStyle(WARNING_STYLE);
        } else {
            this.controlItem.setStyle(this.originalStyle);
        }
    }

    public boolean isDisable() {
        if (this.controlItem != null) {
            return this.controlItem.isDisable();
        }

        return false;
    }

    public void setDisable(boolean disable) {
        if (this.controlItem != null) {
            this.controlItem.setDisable(disable);
        }

        if (this.label != null) {
            this.label.setDisable(disable);
        }

        if (this.defaultButton != null) {
            this.defaultButton.setDisable(disable);
        }
    }

    public int getWarningStatus() {
        return this.warningStatus;
    }
}
