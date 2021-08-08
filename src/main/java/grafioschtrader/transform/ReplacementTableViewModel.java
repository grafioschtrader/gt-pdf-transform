package grafioschtrader.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

public class ReplacementTableViewModel {

  private final SimpleStringProperty sourceText;
  private final SimpleStringProperty targetText;
  private final SimpleBooleanProperty removeText;
  private final SimpleBooleanProperty useRegex;

  public ReplacementTableViewModel() {
    this(null, null, false);
  }

  public ReplacementTableViewModel(String sourceText, String targetText, boolean removeText) {
    this.sourceText = new SimpleStringProperty(sourceText);
    this.targetText = new SimpleStringProperty(targetText);
    this.removeText = new SimpleBooleanProperty(removeText);
    this.useRegex = new SimpleBooleanProperty(removeText);
  }

  public String getSourceText() {
    return sourceText.get();
  }

  public void setSourceText(String sourceText) {
    this.sourceText.set(sourceText);
  }

  public String getTargetText() {
    return targetText.get();
  }

  public void setTargetText(String targetText) {
    this.targetText.set(targetText);
    if (targetText == null || targetText.isBlank()) {
      setRemoveText(true);
    }
  }

  @JsonIgnore
  public ObservableBooleanValue isRemoveTextObservable() {
    return removeText;
  }

  public boolean isRemoveText() {
    return removeText.get();
  }

  public void setRemoveText(boolean removeText) {
    this.removeText.set(removeText);
  }

  @JsonIgnore
  public ObservableBooleanValue isUseRegexObservable() {
    return useRegex;
  }

  public boolean isUseRegex() {
    return useRegex.get();
  }

  public void setUseRegex(boolean useRegex) {
    this.useRegex.set(useRegex);
  }
}
