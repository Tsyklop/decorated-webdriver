/*
 * Copyright 2013 Alexei Barantsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.stqa.selenium.wrapper;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnhandledAlertHandlingWrapperTest extends TestBase {

  public static class SimpleUnhandledAlertHandler implements UnhandledAlertHandler {
    public String alertText;

    @Override
    public void handleUnhandledAlert(WebDriver driver, UnhandledAlertException ex) {
      alertText = ex.getAlertText();
    }
  }

  @Test
  public void testAlertIsIgnored() {
    WebDriver original = new FirefoxDriver();
    SimpleUnhandledAlertHandler handler = new SimpleUnhandledAlertHandler();
    UnhandledAlertHandlingWrapper wrapper = new UnhandledAlertHandlingWrapper(original);
    wrapper.registerAlertHandler(handler);
    WebDriver driver = wrapper.getDriver();

    driver.get(testServer.page("/alert.html"));
    driver.findElement(By.tagName("a")).click();

    String elementText = driver.findElement(By.tagName("a")).getText();
    assertThat(elementText, is("Click me to get an alert"));

    assertThat(handler.alertText, is("An alert"));

    driver.quit();
  }

}
