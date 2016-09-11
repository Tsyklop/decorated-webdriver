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
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedNavigationTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Navigation mockedNavigation;
    DecoratedNavigation decoratedNavigation;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedNavigation = mock(WebDriver.Navigation.class);
      decoratedNavigation = new DecoratedNavigation(mockedNavigation, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedNavigation, sameInstance(fixture.decoratedNavigation.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedNavigation.getTopmostDecorated()));
  }

  @Test
  public void testToAddressAsString() {
    Fixture fixture = new Fixture();

    fixture.decoratedNavigation.to("test");
    verify(fixture.mockedNavigation, times(1)).to("test");
  }

  @Test
  public void testToAddressAsUrl() throws MalformedURLException {
    Fixture fixture = new Fixture();
    URL url = new URL("http://www.selenium2.ru/");

    fixture.decoratedNavigation.to(url);
    verify(fixture.mockedNavigation, times(1)).to(url);
  }

  @Test
  public void testBack() {
    Fixture fixture = new Fixture();

    fixture.decoratedNavigation.back();
    verify(fixture.mockedNavigation, times(1)).back();
  }

  @Test
  public void testForward() {
    Fixture fixture = new Fixture();

    fixture.decoratedNavigation.forward();
    verify(fixture.mockedNavigation, times(1)).forward();
  }

  @Test
  public void testRefresh() {
    Fixture fixture = new Fixture();

    fixture.decoratedNavigation.refresh();
    verify(fixture.mockedNavigation, times(1)).refresh();
  }

}
