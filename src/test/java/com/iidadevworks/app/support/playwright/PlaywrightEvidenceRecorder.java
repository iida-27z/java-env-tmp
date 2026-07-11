package com.iidadevworks.app.support.playwright;

import com.microsoft.playwright.Page;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.TestInfo;

public class PlaywrightEvidenceRecorder {

  private static final Path EVIDENCE_DIRECTORY = Paths.get("build/reports/playwright");
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
  private final String className;
  private final String methodName;

  public PlaywrightEvidenceRecorder(TestInfo testInfo) {
    this.className = testInfo.getTestClass().map(c -> c.getSimpleName()).orElse("UnknownClass");
    this.methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("UnknownMethod");
  }

  public void captureScreenshot(Page page, String screenshotName) {
    String browserName = page.context().browser().browserType().name();
    Path screenshotDirectory =
        EVIDENCE_DIRECTORY.resolve(
            Paths.get(FilenameUtils.getName(className), FilenameUtils.getName(methodName)));
    String fileName =
        String.format(
            "%s_%s_%s.png",
            screenshotName, browserName, TIMESTAMP_FORMATTER.format(LocalDateTime.now()));
    Path outputPath = screenshotDirectory.resolve(FilenameUtils.getName(fileName));
    page.screenshot(new Page.ScreenshotOptions().setPath(outputPath).setFullPage(true));
  }
}
