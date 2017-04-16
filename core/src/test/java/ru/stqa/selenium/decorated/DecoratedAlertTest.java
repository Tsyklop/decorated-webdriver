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

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.security.Credentials;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedAlertTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Alert mocked;
    DecoratedAlert decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(Alert.class);
      decorated = new DecoratedAlert(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<Alert> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyFunction(Function<Alert, R> f, R result) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);
    assertThat(f.apply(fixture.decorated), equalTo(result));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  @Test
  void testSendKeys() {
    verifyFunction($ -> $.sendKeys("test"));
  }

  @Test
  void testAccept() {
    verifyFunction(Alert::accept);
  }

  @Test
  void testDismiss() {
    verifyFunction(Alert::dismiss);
  }

  @Test
  void testGetText() {
    verifyFunction(Alert::getText, "test");
  }

  @Test
  void testAuthenticateUsing() {
    final Credentials creds = mock(Credentials.class);
    verifyFunction($ -> $.authenticateUsing(creds));
  }

  @Test
  void testSetCredentials() {
    final Credentials creds = mock(Credentials.class);
    verifyFunction($ -> $.setCredentials(creds));
  }
}
