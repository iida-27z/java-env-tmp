package com.iidadevworks.app.e2e;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class PlaywrightDynamicPageResolver implements ParameterResolver, AfterEachCallback {

  private final BrowserContext browserContext;

  public PlaywrightDynamicPageResolver(BrowserContext browserContext) {
    this.browserContext = browserContext;
  }

  @Override
  public void afterEach(ExtensionContext context) {
    if (browserContext != null) {
      browserContext.close();
    }
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Page.class;
  }

  @Override
  public @Nullable Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return browserContext.newPage();
  }
}
