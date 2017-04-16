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
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedNavigationTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Navigation mocked;
    DecoratedNavigation decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(WebDriver.Navigation.class);
      decorated = new DecoratedNavigation(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<WebDriver.Navigation> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  @Test
  void testToAddressAsString() {
    verifyFunction($ -> $.to("test"));
  }

  @Test
  void testToAddressAsUrl() throws MalformedURLException {
    final URL url = new URL("http://www.selenium2.ru/");
    verifyFunction($ -> $.to(url));
  }

  @Test
  void testBack() {
    verifyFunction(WebDriver.Navigation::back);
  }

  @Test
  void testForward() {
    verifyFunction(WebDriver.Navigation::forward);
  }

  @Test
  void testRefresh() {
    verifyFunction(WebDriver.Navigation::refresh);
  }

}
