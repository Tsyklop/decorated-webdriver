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
import org.openqa.selenium.WebElement;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedTargetLocatorTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.TargetLocator mocked;
    DecoratedTargetLocator decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(WebDriver.TargetLocator.class);
      decorated = new DecoratedTargetLocator(mocked, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private <R> void verifyDecoratingFunction(Function<WebDriver.TargetLocator, WebDriver> f) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(fixture.mockedDriver);

    WebDriver proxy = f.apply(fixture.decorated);
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
    verifyNoMoreInteractions(fixture.mockedDriver);
  }

  private <R> void verifyDecoratingFunction(Function<WebDriver.TargetLocator, R> f, R result, Consumer<R> p) {
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
  public void testWindow() {
    verifyDecoratingFunction($ -> $.window("test"));
  }

  @Test
  public void testFrameByIndex() {
    verifyDecoratingFunction($ -> $.frame(3));
  }

  @Test
  public void testFrameByString() {
    verifyDecoratingFunction($ -> $.frame("test"));
  }

  @Test
  public void testFrameByReference() {
    final WebElement frame = mock(WebElement.class);
    verifyDecoratingFunction($ -> $.frame(frame));
  }

  @Test
  public void testParentFrame() {
    verifyDecoratingFunction(WebDriver.TargetLocator::parentFrame);
  }

  @Test
  public void testDefaultContent() {
    verifyDecoratingFunction(WebDriver.TargetLocator::defaultContent);
  }

  @Test
  public void testActiveElement() {
    WebElement active = mock(WebElement.class);
    verifyDecoratingFunction(WebDriver.TargetLocator::activeElement, active, WebElement::click);
  }

  @Test
  public void testAlert() {
    Alert alert = mock(Alert.class);
    verifyDecoratingFunction(WebDriver.TargetLocator::alert, alert, Alert::dismiss);
  }

}
