/**
 * @file DockFX.java
 * @brief Driver demonstrating basic dock layout with prototypes. Maintained in a separate package
 * to ensure the encapsulation of org.dockfx private package members.
 * @section License
 * <p>
 * This file is a part of the DockFX Library. Copyright (C) 2015 Robert B. Colton
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 **/

package grafioschtrader.transform;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class GrafioschtraderTransformApp extends Application {
  public static File outputFile;
  public int fontsize = 15;
  private Stage primaryStage;
  private FileListTableView fileListTableView;
  private ReplacementTableView transformTableView;
  private BorderPane root;

  public static void main(String[] args) {
    launch(args);
  }

  public static String transFormPDFToTxt(InputStream is) throws IOException {
    String text = null;
    try (RandomAccessReadBuffer randomAccessRead = new RandomAccessReadBuffer(is)) {
      PDDocument document = Loader.loadPDF(randomAccessRead);
      PDFTextStripper textStripper = new PDFTextStripper();
      textStripper.setSortByPosition(true);
      text = textStripper.getText(document);
      document.close();
    }

    return text;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    Locale defaultLocale = Locale.getDefault();
    ResourceBundle bundle = ResourceBundle.getBundle("messages/messages", defaultLocale);
    primaryStage.setTitle("GT-PDF-Transform " + getAppVersion());
    primaryStage.getIcons().add(new Image(GrafioschtraderTransformApp.class.getResource("gt.png").toExternalForm()));
    root = new BorderPane();

    // create a dock pane that will manage our dock nodes and handle the layout
    DockPane dockPane = new DockPane();

    transformTableView = new ReplacementTableView(bundle);
    CommunicationService communicationService = new CommunicationService(transformTableView.getTransformTableViewModelList());
    transformTableView.setCommunicationService(communicationService);
    fileListTableView = new FileListTableView(communicationService, bundle);

    MenuBar menuBar = addGlobalMenu(bundle, communicationService);
    dockPane.getChildren().add(menuBar);

    // load an image to caption the dock nodes
    Image dockImage = new Image(GrafioschtraderTransformApp.class.getResource("docknode.png").toExternalForm());

    // create and dock some prototype dock nodes to the middle of the dock pane
    // the preferred sizes are used to specify the relative size of the node
    // to the other nodes

    // we can use this to give our central content a larger area where
    // the top and bottom nodes have a preferred width of 300 which means that
    // when a node is docked relative to them such as the left or right dock below
    // they will have 300 / 100 + 300 (400) or 75% of their previous width
    // after both the left and right node's are docked the center docks end up with 50% of the width
    SplitPane spFileTableView = new SplitPane();
    spFileTableView.setOrientation(Orientation.VERTICAL);

    VBox vbFilesTableView = new VBox(new Label(bundle.getString("FILES")), fileListTableView.getFilesTableView());
    VBox vbChangesTableView = new VBox(new Label(bundle.getString("REPLACEMENT")), transformTableView.getTransformTableView());

    spFileTableView.getItems().addAll(vbFilesTableView, vbChangesTableView);

    DockNode mainDock = new DockNode(spFileTableView, bundle.getString("FILES"), new ImageView(dockImage));
    mainDock.setPrefSize(300, 100);
    mainDock.setClosable(false);
    mainDock.dock(dockPane, DockPos.TOP);
    addPDFView(communicationService, bundle, dockPane);
    root.setTop(menuBar);
    root.setCenter(dockPane);

    Scene scene = new Scene(root, 1200, 800);
    scene.focusOwnerProperty().addListener((prop, oldNode, newNode) -> {
      // TODO can be used to set the menus
      System.out.println(newNode);
    });

    // scene.getStylesheets().add(GrafioschtraderTransformApp.class.getResource("application.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();

    primaryStage.show();
    // primaryStage.setFullScreen(true);

    // test the look and feel with both Caspian and Modena
    Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
    // initialize the default styles for the dock pane and undocked nodes using the DockFX
    // library's internal Default.css stylesheet
    // unlike other custom control libraries this allows the user to override them globally
    // using the style manager just as they can with internal JavaFX controls
    // this must be called after the primary stage is shown
    // https://bugs.openjdk.java.net/browse/JDK-8132900
    DockPane.initializeDefaultUserAgentStylesheet(scene);

    setFontSize(fontsize);

    // TODO: after this feel free to apply your own global stylesheet using the StyleManager class
  }

  private void addPDFView(CommunicationService communicationService, ResourceBundle bundle, DockPane dockPane) {
    SplitPane spPdfTextAreas = new SplitPane();
    spPdfTextAreas.setOrientation(Orientation.HORIZONTAL);
    TextArea taPdfBefore = new TextArea();
    communicationService.setTaPdfBefore(taPdfBefore);
    taPdfBefore.setPrefHeight(4000);

    TextArea taPdfAfter = new TextArea();
    communicationService.setTaPdfAfter(taPdfAfter);
    taPdfAfter.setPrefHeight(4000);
    VBox vbPdfBefore = new VBox(new Label(bundle.getString("PDF_BEFORE")), taPdfBefore);

    VBox vbPdfAfter = new VBox(new Label(bundle.getString("PDF_AFTER")), taPdfAfter);
    spPdfTextAreas.getItems().addAll(vbPdfBefore, vbPdfAfter);
    ContextMenu contextMenu = new ContextMenu();
    MenuItem miRemove = new MenuItem(bundle.getString("REMOVE_TEXT"));
    contextMenu.getItems().add(miRemove);
    miRemove.setOnAction(event -> {
      transformTableView.addTransformation(taPdfBefore.getSelectedText(), null, true);
      communicationService.setTransformTxt();
    });

    taPdfBefore.setContextMenu(contextMenu);
    taPdfBefore.setOnContextMenuRequested(event -> {
      miRemove.setDisable(taPdfBefore.getSelectedText() == null || taPdfBefore.getSelectedText().isEmpty());
    });
    DockNode detailDock = new DockNode(spPdfTextAreas, "PDF");
    detailDock.setPrefSize(300, 100);
    detailDock.setClosable(false);
    detailDock.dock(dockPane, DockPos.BOTTOM);
  }

  public MenuBar addGlobalMenu(ResourceBundle bundle, CommunicationService communicationService) {
    Menu miFile = new Menu(bundle.getString("FILE"));
    Menu miFontSize = new Menu(bundle.getString("FONT_SIZE"));
    MenuItem miImportPdf = new MenuItem(bundle.getString("NEW_IMPORT"));
    miImportPdf.setOnAction((ActionEvent actionEvent) -> {
      Optional<ImportSelectionModel> importSelectionModel = new LoadFilesDialog(bundle).createDialog(this.primaryStage, fontsize);
      if (importSelectionModel.isPresent()) {
        FileHelper.chooseFileDir(this.primaryStage, importSelectionModel.get(), this.fileListTableView, bundle);
      }
    });

    MenuItem miExportPdfAsTxt = new MenuItem(bundle.getString("EXPORT_PDF_AS_TEXT"));
    miExportPdfAsTxt.setOnAction(event -> {
      File targetFile = FileHelper.getTransformTargetFile(this.primaryStage);
      if (targetFile != null) {
        communicationService.transformAndExportPdfAsTxt(fileListTableView.getFileList(), targetFile);
      }
    });

    MenuItem miSaveTransformation = new MenuItem(bundle.getString("SAVE_REPLACEMENT"));
    miSaveTransformation.setOnAction(event ->
            FileHelper.readSaveReplacement(false, this.primaryStage, transformTableView.getTransformTableViewModelList(), bundle));

    MenuItem miReadTransformation = new MenuItem(bundle.getString("LOAD_REPLACEMENT"));
    miReadTransformation.setOnAction(event -> {
      FileHelper.readSaveReplacement(true, this.primaryStage, transformTableView.getTransformTableViewModelList(), bundle);
      communicationService.setTransformTxt();
    });

    MenuItem miExit = new MenuItem(bundle.getString("EXIT"));
    miExit.setOnAction(event -> Platform.exit());
    miFile.getItems().addAll(miImportPdf, miExportPdfAsTxt, new SeparatorMenuItem(), miSaveTransformation, miReadTransformation, new SeparatorMenuItem(), miExit);
    miFontSize.getItems().addAll(addSlider());
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(miFile, miFontSize);
    return menuBar;
  }

  private MenuItem addSlider() {
    Slider slider = new Slider(5, 40, fontsize);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(5);
    MenuItem item = new MenuItem();
    item.setGraphic(slider);
    slider.valueProperty().addListener(
            (observableValue, oldValue, newValue) -> {
              fontsize = newValue.intValue();
              setFontSize(fontsize);
            }
    );
    return item;
  }

  private void setFontSize(int value) {
    DoubleProperty fontSize = new SimpleDoubleProperty(value); // font size in pt
    root.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpx;", fontSize));
  }

  private String getAppVersion() {
    final Properties properties = new Properties();
    try {
      properties.load(GrafioschtraderTransformApp.class.getClassLoader().getResourceAsStream("gtpdftransform.properties"));
      return "V" + properties.getProperty("gt.version");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }
}
