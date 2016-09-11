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

package ru.stqa.selenium.decorated;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.security.Credentials;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedAlertTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Alert mockedAlert;
    DecoratedAlert decoratedAlert;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedAlert = mock(Alert.class);
      decoratedAlert = new DecoratedAlert(mockedAlert, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedAlert, sameInstance(fixture.decoratedAlert.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedAlert.getTopmostDecorated()));
  }

  @Test
  public void testSendKeys() {
    Fixture fixture = new Fixture();

    fixture.decoratedAlert.sendKeys("test");
    verify(fixture.mockedAlert, times(1)).sendKeys("test");
  }

  @Test
  public void testAccept() {
    Fixture fixture = new Fixture();

    fixture.decoratedAlert.accept();
    verify(fixture.mockedAlert, times(1)).accept();
  }

  @Test
  public void testDismiss() {
    Fixture fixture = new Fixture();

    fixture.decoratedAlert.dismiss();
    verify(fixture.mockedAlert, times(1)).dismiss();
  }

  @Test
  public void testGetText() {
    Fixture fixture = new Fixture();
    when(fixture.mockedAlert.getText()).thenReturn("test");

    assertThat(fixture.decoratedAlert.getText(), equalTo("test"));
    verify(fixture.mockedAlert, times(1)).getText();
  }

  @Test
  public void testAuthenticateUsing() {
    Fixture fixture = new Fixture();
    Credentials creds = mock(Credentials.class);

    fixture.decoratedAlert.authenticateUsing(creds);
    verify(fixture.mockedAlert, times(1)).authenticateUsing(creds);
  }

  @Test
  public void testSetCredentials() {
    Fixture fixture = new Fixture();
    Credentials creds = mock(Credentials.class);

    fixture.decoratedAlert.setCredentials(creds);
    verify(fixture.mockedAlert, times(1)).setCredentials(creds);
  }

}
