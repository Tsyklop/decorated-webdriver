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

package ru.stqa.selenium.decorated.events;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;

import static org.mockito.Mockito.*;

class EventFiringWebDriverTest {

  private static class Fixture {
    WebDriver mockedDriver;
    WebDriverListener listener;
    WebDriverListener listener2;
    EventFiringWebDriver decoratedDriver;
    WebDriver driver;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new EventFiringWebDriver(mockedDriver);

      listener = spy(new WebDriverListener() {});
      decoratedDriver.addListener(listener);

      driver = decoratedDriver.getActivated();

      listener2 = spy(new WebDriverListener() {});
      decoratedDriver.addListener(listener2);
    }
  }

  @Test
  void shouldNotFireToRemovedListener() {
    Fixture fixture = new Fixture();
    fixture.decoratedDriver.removeListener(fixture.listener);

    fixture.driver.get("http://localhost/");

    verify(fixture.mockedDriver, times(1)).get("http://localhost/");
    verifyZeroInteractions(fixture.listener);
  }

  @Test
  void canRemoveASingleListener() {
    Fixture fixture = new Fixture();
    fixture.decoratedDriver.removeListener(fixture.listener);

    fixture.driver.get("http://localhost/");

    verify(fixture.mockedDriver, times(1)).get("http://localhost/");
    verifyZeroInteractions(fixture.listener);
    verify(fixture.listener2, times(1)).beforeGet(fixture.mockedDriver, "http://localhost/");
    verify(fixture.listener2, times(1)).afterGet(fixture.mockedDriver, "http://localhost/");
  }

  @Test
  void canRemoveAllListeners() {
    Fixture fixture = new Fixture();
    fixture.decoratedDriver.removeAllListeners();

    fixture.driver.get("http://localhost/");

    verify(fixture.mockedDriver, times(1)).get("http://localhost/");
    verifyZeroInteractions(fixture.listener);
    verifyZeroInteractions(fixture.listener2);
  }

}
