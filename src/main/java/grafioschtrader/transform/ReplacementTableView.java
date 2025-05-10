package grafioschtrader.transform;

import grafioschtrader.transform.shared.TextAreaTableCell;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.ResourceBundle;

public class ReplacementTableView {

  private final TableView<ReplacementTableViewModel> tableView = new TableView<>();
  private final ObservableList<ReplacementTableViewModel> replacementTableViewModelList = FXCollections.observableArrayList();
  private final ResourceBundle bundle;
  private CommunicationService communicationService;

  public ReplacementTableView(ResourceBundle bundle) {
    this.bundle = bundle;
  }

  public void setCommunicationService(CommunicationService communicationService) {
    this.communicationService = communicationService;
  }

  public TableView<ReplacementTableViewModel> getTransformTableView() {
    tableView.setEditable(true);

    TableColumn<ReplacementTableViewModel, String> tcSourceText = new TableColumn<>(bundle.getString("SOURCE_TEXT"));

    tcSourceText.setCellValueFactory(new PropertyValueFactory<>("sourceText"));

    tcSourceText.setCellFactory(TextAreaTableCell.forTableColumn());
    tcSourceText.setOnEditCommit(
            (CellEditEvent<ReplacementTableViewModel, String> t) -> {
              t.getTableView().getItems().get(
                      t.getTablePosition().getRow()).setSourceText(t.getNewValue());
              callCommunicationService(t.getTableView().getItems().get(
                      t.getTablePosition().getRow()));
            });
    tcSourceText.setMinWidth(300);
    tcSourceText.setMaxWidth(2000);

    TableColumn<ReplacementTableViewModel, String> tcTargetText = new TableColumn<>(bundle.getString("TARGET_TEXT"));

    tcTargetText.setCellValueFactory(new PropertyValueFactory<>("targetText"));
    tcTargetText.setCellFactory(TextAreaTableCell.forTableColumn());
    tcTargetText.setOnEditCommit(
            (CellEditEvent<ReplacementTableViewModel, String> t) -> {
              t.getTableView().getItems().get(
                      t.getTablePosition().getRow()).setTargetText(t.getNewValue());
              callCommunicationService(t.getTableView().getItems().get(
                      t.getTablePosition().getRow()));
            });
    tcTargetText.setMinWidth(300);
    tcTargetText.setMaxWidth(2000);

    TableColumn<ReplacementTableViewModel, Boolean> tcRemove = new TableColumn<>(bundle.getString("REMOVE_TEXT"));
    tcRemove.setMinWidth(150);
    tcRemove.setMaxWidth(250);
    tcRemove.setCellValueFactory(param -> param.getValue().isRemoveTextObservable());
    tcRemove.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
      @Override
      public ObservableValue<Boolean> call(Integer param) {
        callCommunicationService(replacementTableViewModelList.get(param));
        return replacementTableViewModelList.get(param).isRemoveTextObservable();
      }
    }));

    TableColumn<ReplacementTableViewModel, Boolean> tcUseRegex = new TableColumn<>(bundle.getString("USE_REGEX"));
    tcUseRegex.setMinWidth(150);
    tcUseRegex.setMaxWidth(250);
    tcUseRegex.setCellValueFactory(param -> param.getValue().isUseRegexObservable());
    tcUseRegex.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
      @Override
      public ObservableValue<Boolean> call(Integer param) {
        callCommunicationService(replacementTableViewModelList.get(param));
        return replacementTableViewModelList.get(param).isUseRegexObservable();
      }
    }));

    tableView.setItems(replacementTableViewModelList);
    tableView.getColumns().addAll(tcSourceText, tcTargetText, tcRemove, tcUseRegex);
    tableView.setPrefHeight(4000);

    MenuItem miDelete = new MenuItem(bundle.getString("DELETE"));
    miDelete.setOnAction(event -> {
      ReplacementTableViewModel selectedReplacementTableViewModel = tableView.getSelectionModel().getSelectedItem();
      if (selectedReplacementTableViewModel != null) {
        replacementTableViewModelList.remove(selectedReplacementTableViewModel);
      }
    });
    tableView.setContextMenu(new ContextMenu(miDelete));
    return tableView;
  }

  private void callCommunicationService(ReplacementTableViewModel replacementTableViewModel) {
    if (replacementTableViewModel.isRemoveText() || !replacementTableViewModel.isRemoveText() &&
            replacementTableViewModel.getTargetText() != null) {
      this.communicationService.reTransformTxt();
    }
  }

  public void addTransformation(String sourceText, String targetText, boolean removeText) {
    replacementTableViewModelList.add(new ReplacementTableViewModel(sourceText, targetText, removeText));
  }

  public ObservableList<ReplacementTableViewModel> getTransformTableViewModelList() {
    return replacementTableViewModelList;
  }
}
