/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import burai.app.icon.web.WebEngineFactory;
import burai.app.icon.web.WebEngineWrapper;
import burai.app.icon.web.WebLoader;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXWebIcon extends QEFXIconBase<WebEngineWrapper> {

    private static final long SLEEP_TIME_TO_RELOAD = 3L * 60L * 1000L;

    private static final String FIGURE_CLASS = "icon-web";
    private static final double FIGURE_FONT_SIZE1 = 0.150;
    private static final double FIGURE_FONT_SIZE2 = 0.117;
    private static final int FIGURE_FONT_MAX_LEN1 = 8;
    private static final int FIGURE_FONT_MAX_LEN2 = 12;

    private String initialURL;

    private File htmlFile;

    private boolean detached;

    private List<InvalidationListener> titleListeners;
    private List<InvalidationListener> locationListeners;

    public QEFXWebIcon(String url) {
        this(url, null);
    }

    public QEFXWebIcon(File htmlFile) {
        this(htmlFile == null || htmlFile.getPath() == null || htmlFile.getPath().trim().isEmpty() ?
                null : "file:///" + htmlFile.getPath(), htmlFile);
    }

    private QEFXWebIcon(String url, File htmlFile) {
        super(htmlFile == null ? WebEngineFactory.getInstance().getWebEngine(url) : new WebEngineWrapper());
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("url is empty.");
        }

        this.initialURL = url;
        this.htmlFile = htmlFile;

        this.detached = false;

        this.titleListeners = new ArrayList<InvalidationListener>();
        this.locationListeners = new ArrayList<InvalidationListener>();

        if (this.htmlFile == null) {
            this.runReloadingThread();
        } else {
            this.content.load(this.initialURL);
        }

        this.setupCaption();
        this.setupSubCaption();
    }

    private void runReloadingThread() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                while (!this.detached) {
                    try {
                        this.wait(SLEEP_TIME_TO_RELOAD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (this.detached) {
                        break;
                    }

                    WebLoader.getInstance().loadWebEngine(this.content);
                }
            }
        });

        thread.start();
    }

    private void setupCaption() {
        StringProperty caption = this.captionProperty();

        InvalidationListener titleListener = o -> {
            caption.set(this.initCaption());
        };

        this.titleListeners.add(titleListener);
        this.content.titleProperty().addListener(titleListener);

        InvalidationListener locationListener = o -> {
            caption.set(this.initCaption());
        };

        this.locationListeners.add(locationListener);
        this.content.locationProperty().addListener(locationListener);
    }

    private void setupSubCaption() {
        StringProperty caption = this.subCaptionProperty();

        InvalidationListener titleListener = o -> {
            caption.set(this.initSubCaption());
        };

        this.titleListeners.add(titleListener);
        this.content.titleProperty().addListener(titleListener);

        InvalidationListener locationListener = o -> {
            caption.set(this.initSubCaption());
        };

        this.locationListeners.add(locationListener);
        this.content.locationProperty().addListener(locationListener);
    }

    public String getInitialURL() {
        return this.initialURL;
    }

    public boolean isHTMLFile() {
        return this.htmlFile != null;
    }

    public File getHTMLFile() {
        return this.htmlFile;
    }

    @Override
    public synchronized void detach() {
        this.detached = true;
        if (this.htmlFile == null) {
            this.notifyAll();
        }

        //WebLoader.getInstance().removeWebEngine(this.content);

        for (InvalidationListener titleListener : this.titleListeners) {
            this.content.titleProperty().removeListener(titleListener);
        }

        for (InvalidationListener locationListener : this.locationListeners) {
            this.content.locationProperty().removeListener(locationListener);
        }

        this.content = null;
    }

    @Override
    public Node getFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        StackPane stackPane = new StackPane();

        Node figure = null;
        if (this.htmlFile != null || (this.initialURL != null && this.initialURL.trim().startsWith("file"))) {
            figure = SVGLibrary.getGraphic(SVGData.HTML, scaledSize, null, FIGURE_CLASS);
        } else {
            figure = SVGLibrary.getGraphic(SVGData.EARTH, scaledSize, null, FIGURE_CLASS);
        }
        if (figure != null) {
            StackPane.setAlignment(figure, Pos.CENTER);
            stackPane.getChildren().add(figure);
        }

        Label label = new Label();
        label.getStyleClass().add(FIGURE_CLASS);
        this.updateFigureLabel(size, label);

        InvalidationListener titleListener = o -> {
            this.updateFigureLabel(size, label);
        };

        this.titleListeners.add(titleListener);
        this.content.titleProperty().addListener(titleListener);

        InvalidationListener locationListener = o -> {
            this.updateFigureLabel(size, label);
        };

        this.locationListeners.add(titleListener);
        this.content.locationProperty().addListener(locationListener);

        StackPane.setAlignment(label, Pos.CENTER);
        stackPane.getChildren().add(label);

        Group group = new Group(stackPane);
        BorderPane.setMargin(group, new Insets(insetsSize));
        return group;
    }

    private void updateFigureLabel(double size, Label label) {
        if (size <= 0.0) {
            return;
        }

        if (label == null) {
            return;
        }

        String title = this.createFigureTitle();
        if (title != null) {
            int lenTitle = Math.min(title.length(), FIGURE_FONT_MAX_LEN2);

            if (lenTitle <= FIGURE_FONT_MAX_LEN1) {
                label.setStyle("-fx-font-size: " + (size * FIGURE_FONT_SIZE1));

            } else {
                double rate = (FIGURE_FONT_SIZE2 - FIGURE_FONT_SIZE1)
                        / ((double) (FIGURE_FONT_MAX_LEN2 - FIGURE_FONT_MAX_LEN1));
                double delta = (double) (lenTitle - FIGURE_FONT_MAX_LEN1);
                double fontSize = FIGURE_FONT_SIZE1 + rate * delta;
                label.setStyle("-fx-font-size: " + (size * fontSize));
            }

            label.setText(title);
        }
    }

    private String createFigureTitle() {
        String title = this.captionProperty().get();
        if (title != null) {
            title = title.trim();
        }

        if (title != null) {
            String title2 = title;
            int index = title2.indexOf('?');
            if (index > -1) {
                title2 = title2.substring(0, index);
            }

            String[] subTitles = title2.split("/+");
            if (subTitles != null) {
                for (int i = subTitles.length - 1; i >= 0; i--) {
                    String subTitle = subTitles[i];
                    subTitle = subTitle == null ? null : subTitle.trim();
                    if (subTitle != null && (!subTitle.isEmpty()) && (!"index.html".equalsIgnoreCase(subTitle))) {
                        title = subTitle;
                        break;
                    }
                }
            }
        }

        if (title != null && title.length() > FIGURE_FONT_MAX_LEN2) {
            title = title.substring(0, FIGURE_FONT_MAX_LEN2) + System.lineSeparator() + "...";
        }

        return title;
    }

    @Override
    protected String initCaption() {
        String caption = null;

        String title = this.content.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            caption = title;
        }

        if (caption == null) {
            if (this.htmlFile != null) {
                caption = this.htmlFile.getName();
            }
        }

        if (caption == null) {
            String location = this.content.getLocation();
            if (location == null || location.trim().isEmpty()) {
                location = this.initialURL;
            }

            int index = location.indexOf(':');
            if (index > -1) {
                location = location.substring(index);
                while (!location.isEmpty()) {
                    char head = location.charAt(0);
                    if (head != ':' && head != '/') {
                        break;
                    }
                    if (location.length() < 2) {
                        location = "";
                    } else {
                        location = location.substring(1);
                    }
                }
            }

            caption = location;
        }

        return caption;
    }

    @Override
    protected String initSubCaption() {
        String caption = null;
        if (this.htmlFile != null) {
            String path = this.htmlFile.getPath();
            if (path != null && (!path.isEmpty())) {
                caption = this.getFileDetail(path);
            }
        }

        String title = this.content.getTitle();
        if (title != null && (!title.trim().isEmpty())) {
            if (caption != null) {
                caption = caption + System.lineSeparator();
            } else {
                caption = "";
            }
            caption = caption + "Title: " + title;
        }

        String location = this.content.getLocation();
        if (location != null && (!location.trim().isEmpty())) {
            if (caption != null) {
                caption = caption + System.lineSeparator();
            } else {
                caption = "";
            }
            caption = caption + "URL: " + location;
        }

        return caption;
    }
}
