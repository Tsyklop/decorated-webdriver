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

package ru.stqa.selenium.wrapper;

import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.logging.Logs;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedOptionsTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Options mockedOptions;
    DecoratedOptions decoratedOptions;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedOptions = mock(WebDriver.Options.class);
      decoratedOptions = new DecoratedOptions(decoratedDriver, mockedOptions);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedOptions, sameInstance(fixture.decoratedOptions.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedOptions.getTopmostDecorated()));
  }

  @Test
  public void testAddCookie() {
    Fixture fixture = new Fixture();
    Cookie mockedCookie = mock(Cookie.class);

    fixture.decoratedOptions.addCookie(mockedCookie);
    verify(fixture.mockedOptions, times(1)).addCookie(mockedCookie);
  }

  @Test
  public void testDeleteCookieNamed() {
    Fixture fixture = new Fixture();

    fixture.decoratedOptions.deleteCookieNamed("test");
    verify(fixture.mockedOptions, times(1)).deleteCookieNamed("test");
  }

  @Test
  public void testDeleteCookie() {
    Fixture fixture = new Fixture();
    Cookie mockedCookie = mock(Cookie.class);

    fixture.decoratedOptions.deleteCookie(mockedCookie);
    verify(fixture.mockedOptions, times(1)).deleteCookie(mockedCookie);
  }

  @Test
  public void testDeleteAllCookies() {
    Fixture fixture = new Fixture();

    fixture.decoratedOptions.deleteAllCookies();
    verify(fixture.mockedOptions, times(1)).deleteAllCookies();
  }

  @Test
  public void testGetCookies() {
    Fixture fixture = new Fixture();
    Set<Cookie> cookies = new HashSet<Cookie>();
    cookies.add(mock(Cookie.class));
    when(fixture.mockedOptions.getCookies()).thenReturn(cookies);

    assertThat(fixture.decoratedOptions.getCookies(), sameInstance(cookies));
    verify(fixture.mockedOptions, times(1)).getCookies();
  }

  @Test
  public void testGetCookieNamed() {
    Fixture fixture = new Fixture();
    Cookie cookie = mock(Cookie.class);
    when(fixture.mockedOptions.getCookieNamed("test")).thenReturn(cookie);

    assertThat(fixture.decoratedOptions.getCookieNamed("test"), sameInstance(cookie));
    verify(fixture.mockedOptions, times(1)).getCookieNamed("test");
  }

  @Test
  public void testTimeouts() {
    Fixture fixture = new Fixture();
    WebDriver.Timeouts mockedTimeouts = mock(WebDriver.Timeouts.class);
    when(fixture.mockedOptions.timeouts()).thenReturn(mockedTimeouts);

    WebDriver.Timeouts proxy = fixture.decoratedOptions.timeouts();
    assertThat(mockedTimeouts, not(sameInstance(proxy)));
    verify(fixture.mockedOptions, times(1)).timeouts();

    proxy.implicitlyWait(10, TimeUnit.SECONDS);
    verify(mockedTimeouts, times(1)).implicitlyWait(10, TimeUnit.SECONDS);
  }

  @Test
  public void testImeNotDecorated() {
    Fixture fixture = new Fixture();
    WebDriver.ImeHandler mockedIme = mock(WebDriver.ImeHandler.class);
    when(fixture.mockedOptions.ime()).thenReturn(mockedIme);

    assertThat(fixture.decoratedOptions.ime(), sameInstance(mockedIme));
    verify(fixture.mockedOptions, times(1)).ime();
  }

  @Test
  public void testWindow() {
    Fixture fixture = new Fixture();
    WebDriver.Window mockedWindow = mock(WebDriver.Window.class);
    when(fixture.mockedOptions.window()).thenReturn(mockedWindow);

    WebDriver.Window proxy = fixture.decoratedOptions.window();
    assertThat(mockedWindow, not(sameInstance(proxy)));
    verify(fixture.mockedOptions, times(1)).window();

    proxy.maximize();
    verify(mockedWindow, times(1)).maximize();
  }

  @Test
  public void testLogsNotDecorated() {
    Fixture fixture = new Fixture();
    Logs mockedLogs = mock(Logs.class);
    when(fixture.mockedOptions.logs()).thenReturn(mockedLogs);

    assertThat(fixture.decoratedOptions.logs(), sameInstance(mockedLogs));
    verify(fixture.mockedOptions, times(1)).logs();
  }

}
