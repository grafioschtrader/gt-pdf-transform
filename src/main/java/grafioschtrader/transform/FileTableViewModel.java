package grafioschtrader.transform;

import javafx.beans.property.*;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * File model view
 */
public class FileTableViewModel {

  private final SimpleBooleanProperty selected;
  private final StringProperty pathWithoutName;
  private final StringProperty fileName;
  private final LongProperty size;
  private final ObjectProperty<LocalDateTime> creationTime;
  private final Path absolutePath;

  public FileTableViewModel(boolean selected, Path absolutePath, String path, String fileName, Long size, LocalDateTime creationTime) {
    this.selected = new SimpleBooleanProperty(selected);
    this.absolutePath = absolutePath;
    this.pathWithoutName = new SimpleStringProperty(path);
    this.fileName = new SimpleStringProperty(fileName);
    this.size = new SimpleLongProperty(size);
    this.creationTime = new SimpleObjectProperty<>(creationTime);
  }

  public BooleanProperty selectedProperty() {
    return selected;
  }

  public boolean getSelected() {
    return selected.get();
  }

  public void setSelected(boolean selected) {
    this.selected.set(selected);
  }

  public StringProperty pathWithoutNameProperty() {
    return pathWithoutName;
  }

  public String getPathWithoutName() {
    return pathWithoutName.get();
  }

  public StringProperty fileNameProperty() {
    return fileName;
  }

  public String getFileName() {
    return fileName.get();
  }

  public void setFileName(String fileName) {
    this.fileName.set(fileName);
  }

  public Path getAbsolutePath() {
    return absolutePath;
  }

  public Long getSize() {
    return size.get();
  }

  public void setSize(Integer size) {
    this.size.set(size);
  }

  public LocalDateTime getCreationTime() {
    return creationTime.get();
  }

  public void setCreationTime(LocalDateTime creationTime) {
    this.creationTime.set(creationTime);
  }

  public LongProperty sizeProperty() {
    return size;
  }

  public ObjectProperty<LocalDateTime> creationTimeProperty() {
    return creationTime;
  }
}

