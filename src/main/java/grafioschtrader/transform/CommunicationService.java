package grafioschtrader.transform;

import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class CommunicationService {

  private final static String RemoveEmptyLinePattern = "(?m)^\\s*$[\n\r]{1,}";
  // private String pdfAsText;
  private final ObservableList<ReplacementTableViewModel> replacementTableViewModelList;
  private TextArea taPdfBefore;
  private TextArea taPdfAfter;

  public CommunicationService(ObservableList<ReplacementTableViewModel> replacementTableViewModelList) {
    this.replacementTableViewModelList = replacementTableViewModelList;
  }

  /**
   * Selection in FileTableView has changed
   *
   * @param fileTableViewModel
   */
  public void fileViewSelectionChanged(FileTableViewModel fileTableViewModel) {
    String pdfAsTxt = getPdfAsTextFromFilePath(fileTableViewModel.getAbsolutePath());
    taPdfBefore.setText(pdfAsTxt);
    setTransformTxt();
  }

  public void reTransformTxt() {
    if (taPdfBefore.getText() != null) {
      taPdfBefore.setText(taPdfBefore.getText());
      setTransformTxt();
    }
  }

  private String getPdfAsTextFromFilePath(Path path) {
    String pdfAsText = null;
    try (InputStream is = Files.newInputStream(path);
         RandomAccessReadBuffer randomAccessRead = new RandomAccessReadBuffer(is)) {
      PDDocument document = Loader.loadPDF(randomAccessRead);
      PDFTextStripper textStripper = new PDFTextStripper();
      textStripper.setSortByPosition(true);
      pdfAsText = removeEmptyLinesAndReduceSpaces(textStripper.getText(document));
      document.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return pdfAsText;
  }

  public void setTransformTxt() {
    String transformedPdfAsTxt = reduceAndTransformPdfAsTxt(removeEmptyLinesAndReduceSpaces(taPdfBefore.getText()));
    taPdfAfter.setText(transformedPdfAsTxt);
  }

  private String reduceAndTransformPdfAsTxt(String pdfAsTxt) {
    String transformedPdfAsTxt = pdfAsTxt;

    for (ReplacementTableViewModel replacementTableViewModel : replacementTableViewModelList) {
      String sourceText = replacementTableViewModel.getSourceText().replaceAll("\r\n|\r|\n", System.lineSeparator());
      if(!replacementTableViewModel.isUseRegex()) {
        sourceText = sourceText = Pattern.quote(sourceText);
      }
      Pattern pattern = Pattern.compile(sourceText);
      transformedPdfAsTxt = pattern.matcher(transformedPdfAsTxt).replaceFirst(replacementTableViewModel.isRemoveText() ? ""
              : replacementTableViewModel.getTargetText());
      transformedPdfAsTxt = removeEmptyLinesAndReduceSpaces(transformedPdfAsTxt);
    }
    return transformedPdfAsTxt;
  }

  private String removeEmptyLinesAndReduceSpaces(String rowPdfAsText) {
    return rowPdfAsText.replaceAll(RemoveEmptyLinePattern, "").replaceAll("\r\n|\r|\n", System.lineSeparator())
            .replaceAll(" +", " ").trim();
  }

  public void transformAndExportPdfAsTxt(ObservableList<FileTableViewModel> fileList, File targetFile) {
    int fileCounter = 1;
    try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)) {
      for (FileTableViewModel fileTableViewModel : fileList) {
        if (fileTableViewModel.getSelected()) {
          String fileHeader = String.format("%s[%d|%s]%s", (fileCounter == 1) ? "" : System.lineSeparator(), fileCounter++,
                  fileTableViewModel.getAbsolutePath(), System.lineSeparator());
          osw.write(fileHeader);
          String pdfAsTxt = getPdfAsTextFromFilePath(fileTableViewModel.getAbsolutePath());
          String transformedPdfAsTxt = reduceAndTransformPdfAsTxt(pdfAsTxt);
          osw.write(transformedPdfAsTxt);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setTaPdfBefore(TextArea taPdfBefore) {
    this.taPdfBefore = taPdfBefore;
  }

  public void setTaPdfAfter(TextArea taPdfAfter) {
    this.taPdfAfter = taPdfAfter;
  }
}
