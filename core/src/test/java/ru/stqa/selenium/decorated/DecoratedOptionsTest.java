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
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedOptionsTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Options mocked;
    DecoratedOptions decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(WebDriver.Options.class);
      decorated = new DecoratedOptions(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<WebDriver.Options> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyFunction(Function<WebDriver.Options, R> f, R result) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);
    assertThat(f.apply(fixture.decorated), equalTo(result));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyDecoratingFunction(Function<WebDriver.Options, R> f, R result, Consumer<R> p) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);

    R proxy = f.apply(fixture.decorated);
    assertThat(result, not(sameInstance(proxy)));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);

    p.accept(proxy);
    p.accept(verify(result, times(1)));
    verifyNoMoreInteractions(result);
  }

  @Test
  void testAddCookie() {
    verifyFunction($ -> $.addCookie(new Cookie("name", "value")));
  }

  @Test
  void testDeleteCookieNamed() {
    verifyFunction($ -> $.deleteCookieNamed("test"));
  }

  @Test
  void testDeleteCookie() {
    verifyFunction($ -> $.deleteCookie(new Cookie("name", "value")));
  }

  @Test
  void testDeleteAllCookies() {
    verifyFunction(WebDriver.Options::deleteAllCookies);
  }

  @Test
  void testGetCookies() {
    Set<Cookie> cookies = new HashSet<>();
    cookies.add(new Cookie("name", "value"));
    verifyFunction(WebDriver.Options::getCookies, cookies);
  }

  @Test
  void testGetCookieNamed() {
    verifyFunction($ -> $.getCookieNamed("test"), new Cookie("name", "value"));
  }

  @Test
  void testTimeouts() {
    WebDriver.Timeouts timeouts = mock(WebDriver.Timeouts.class);
    verifyDecoratingFunction(WebDriver.Options::timeouts, timeouts, t -> t.implicitlyWait(10, TimeUnit.SECONDS));
  }

  @Test
  void testImeNotDecorated() {
    final WebDriver.ImeHandler ime = mock(WebDriver.ImeHandler.class);
    verifyFunction(WebDriver.Options::ime, ime);
  }

  @Test
  void testWindow() {
    final WebDriver.Window window = mock(WebDriver.Window.class);
    verifyDecoratingFunction(WebDriver.Options::window, window, WebDriver.Window::maximize);
  }

  @Test
  void testLogsNotDecorated() {
    final Logs logs = mock(Logs.class);
    verifyFunction(WebDriver.Options::logs, logs);
  }

}
