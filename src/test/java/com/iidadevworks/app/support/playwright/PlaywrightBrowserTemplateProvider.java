package com.iidadevworks.app.support.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

public class PlaywrightBrowserTemplateProvider implements TestTemplateInvocationContextProvider {

  private static final Namespace NAMESPACE =
      Namespace.create(PlaywrightBrowserTemplateProvider.class);

  private record PlaywrightResource(Playwright playwright) implements AutoCloseable {
    @Override
    public void close() {
      playwright.close();
    }
  }

  private record BrowserResource(Browser browser) implements AutoCloseable {
    @Override
    public void close() {
      browser.close();
    }
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    Store store = context.getRoot().getStore(NAMESPACE);
    String playwrightResourceKey = PlaywrightResource.class.getName();
    PlaywrightResource playwrightResource =
        store.computeIfAbsent(
            playwrightResourceKey,
            k -> new PlaywrightResource(Playwright.create()),
            PlaywrightResource.class);
    return Stream.<BrowserType>of(
            playwrightResource.playwright().chromium(),
            playwrightResource.playwright().firefox(),
            playwrightResource.playwright().webkit())
        .map(
            browserType -> {
              Browser browser =
                  store
                      .computeIfAbsent(
                          browserType.name(),
                          k ->
                              new BrowserResource(
                                  browserType.launch(
                                      new BrowserType.LaunchOptions().setHeadless(true))),
                          BrowserResource.class)
                      .browser();

              return new TestTemplateInvocationContext() {
                @Override
                public String getDisplayName(int invocationIndex) {
                  return browserType.name();
                }

                @Override
                public List<Extension> getAdditionalExtensions() {
                  return List.of(new PlaywrightDynamicPageResolver(browser));
                }
              };
            });
  }
}
