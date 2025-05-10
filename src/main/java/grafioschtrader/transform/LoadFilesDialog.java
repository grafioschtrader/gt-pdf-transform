package grafioschtrader.transform;


import grafioschtrader.transform.shared.LimitedTextField;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.ResourceBundle;

public class LoadFilesDialog {

  private final CheckBox cbSetOwner = new CheckBox();
  private final ResourceBundle bundle;
  private Stage stage;

  public LoadFilesDialog(ResourceBundle bundle) {
    this.bundle = bundle;
  }

  public Optional<ImportSelectionModel> createDialog(Stage primaryState, int fontsize) {
    Dialog<ImportSelectionModel> dialog = new Dialog<>();
    dialog.initOwner(primaryState);
    dialog.setTitle(bundle.getString("NEW_IMPORT"));
    dialog.setHeaderText(bundle.getString("NEW_IMPORT_EXACT"));

    // Set the button types.
    ButtonType bChooseFile = new ButtonType(bundle.getString("CHOOSE_DIR"), ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(bChooseFile, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(20, 0, 10, 10));

    CheckBox cbImportDir = new CheckBox(bundle.getString("IMPORT_DIRECTORY"));

    Label lbExcludeRegEx = new Label(bundle.getString("EXCLUDE_FILE_PATTERN"));
    LimitedTextField tfExcludeRegEx = new LimitedTextField();
    tfExcludeRegEx.setMaxLength(128);
    tfExcludeRegEx.setPrefWidth(400);
    CheckBox cbIncludeSubDir = new CheckBox(bundle.getString("IMPORT_INCLUDE_SUB_DIR"));

    HBox hBoxRegex = new HBox(10.0);
    hBoxRegex.setPadding(new Insets(10, 10, 10, 0));
    hBoxRegex.getChildren().addAll(lbExcludeRegEx, tfExcludeRegEx);
    vbox.getChildren().addAll(cbImportDir, hBoxRegex,  cbIncludeSubDir);

    cbImportDir.setOnAction((event) -> {
      if (cbImportDir.isSelected()) {
        ((Button) dialog.getDialogPane().lookupButton(bChooseFile)).setText(bundle.getString("CHOOSE_DIR"));
        vbox.getChildren().addAll(hBoxRegex, cbIncludeSubDir);
      } else {
        cbIncludeSubDir.setSelected(false);
        ((Button) dialog.getDialogPane().lookupButton(bChooseFile)).setText(bundle.getString("CHOOSE_FILE"));
        vbox.getChildren().remove(cbIncludeSubDir);
        vbox.getChildren().remove(hBoxRegex);
      }
      dialog.getDialogPane().getScene().getWindow().sizeToScene();
    });

    cbImportDir.setSelected(true);

    dialog.getDialogPane().setContent(vbox);
    dialog.getDialogPane().setStyle("-fx-font-size: " + fontsize + " px;");
    Platform.runLater(() -> cbImportDir.requestFocus());

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == bChooseFile) {
        return new ImportSelectionModel(cbImportDir.isSelected(), cbIncludeSubDir.isSelected(), tfExcludeRegEx.getText());
      }
      return null;
    });

    Optional<ImportSelectionModel> result = dialog.showAndWait();
    return result;
  }
}
