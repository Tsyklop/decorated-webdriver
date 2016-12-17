/*
 * Copyright 2016 Alexei Barantsev
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

package ru.stqa.selenium.decorated.alerts;

import org.junit.Test;
import org.openqa.selenium.*;

import static com.googlecode.catchexception.throwable.CatchThrowable.catchThrowable;
import static com.googlecode.catchexception.throwable.CatchThrowable.caughtThrowable;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnhandledAlertHandlingWebDriverTest {

  private static class Fixture {
    WebDriver mockedDriver;
    UnhandledAlertHandlingWebDriver alertHandlingDriver;
    WebDriver driver;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      alertHandlingDriver = new UnhandledAlertHandlingWebDriver(mockedDriver);
      driver = alertHandlingDriver.getActivated();
    }
  }

  public static class SimpleUnhandledAlertHandler implements UnhandledAlertHandler {
    public String alertText;

    @Override
    public void handleUnhandledAlert(WebDriver driver, UnhandledAlertException ex) {
      alertText = ex.getAlertText();
    }
  }

  @Test
  public void testAlertIsIgnored() {
    Fixture fixture = new Fixture();
    SimpleUnhandledAlertHandler handler = new SimpleUnhandledAlertHandler();
    fixture.alertHandlingDriver.registerAlertHandler(handler);

    when(fixture.mockedDriver.getTitle())
      .thenThrow(new UnhandledAlertException("Unhandled alert", "Alert text"))
      .thenReturn("Page title");

    String title = fixture.driver.getTitle();
    assertThat(title, is("Page title"));
    assertThat(handler.alertText, is("Alert text"));
  }

  @Test
  public void testSecondAlertIsNotIgnored() {
    Fixture fixture = new Fixture();
    SimpleUnhandledAlertHandler handler = new SimpleUnhandledAlertHandler();
    fixture.alertHandlingDriver.registerAlertHandler(handler);

    when(fixture.mockedDriver.getTitle())
      .thenThrow(new UnhandledAlertException("Unhandled alert", "Alert text 1"))
      .thenThrow(new UnhandledAlertException("Unhandled alert", "Alert text 2"));

    catchThrowable(() -> fixture.driver.getTitle());
    assertThat(caughtThrowable(), instanceOf(UnhandledAlertException.class));
    assertThat(((UnhandledAlertException) caughtThrowable()).getAlertText(), is("Alert text 2"));
    assertThat(handler.alertText, is("Alert text 1"));
  }

  @Test
  public void testOtherExceptionsAreNotIgnored() {
    Fixture fixture = new Fixture();
    SimpleUnhandledAlertHandler handler = new SimpleUnhandledAlertHandler();
    fixture.alertHandlingDriver.registerAlertHandler(handler);

    when(fixture.mockedDriver.getTitle()).thenThrow(new WebDriverException());

    catchThrowable(() -> fixture.driver.getTitle());
    assertThat(caughtThrowable(), instanceOf(WebDriverException.class));
  }

}
