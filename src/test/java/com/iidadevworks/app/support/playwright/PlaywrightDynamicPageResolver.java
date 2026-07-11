package com.iidadevworks.app.support.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class PlaywrightDynamicPageResolver
    implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

  private final Browser browser;
  private BrowserContext browserContext;

  public PlaywrightDynamicPageResolver(Browser browser) {
    this.browser = browser;
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    this.browserContext = browser.newContext();
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
