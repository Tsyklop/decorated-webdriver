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
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedWebElementTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebElement mocked;
    DecoratedWebElement decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(WebElement.class, withSettings().extraInterfaces(Locatable.class));
      decorated = new DecoratedWebElement(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getWrappedElement()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<WebElement> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyFunction(Function<WebElement, R> f, R result) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);
    assertThat(f.apply(fixture.decorated), equalTo(result));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyDecoratingFunction(Function<WebElement, R> f, R result, Consumer<R> p) {
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
  void testSendKeys() {
    verifyFunction($ -> $.sendKeys("test"));
  }

  @Test
  void testClick() {
    verifyFunction(WebElement::click);
  }

  @Test
  void testSubmit() {
    verifyFunction(WebElement::submit);
  }

  @Test
  void testClear() {
    verifyFunction(WebElement::clear);
  }

  @Test
  void testGetText() {
    verifyFunction(WebElement::getText, "test");
  }

  @Test
  void testGetTagName() {
    verifyFunction(WebElement::getTagName, "input");
  }

  @Test
  void testGetAttribute() {
    verifyFunction($ -> $.getAttribute("value"), "test");
  }

  @Test
  void testIsSelected() {
    verifyFunction(WebElement::isSelected, true);
  }

  @Test
  void testIsEnabled() {
    verifyFunction(WebElement::isEnabled, true);
  }

  @Test
  void testIsDisplayed() {
    verifyFunction(WebElement::isDisplayed, true);
  }

  @Test
  void testGetLocation() {
    verifyFunction(WebElement::getLocation, new Point(10, 20));
  }

  @Test
  void testGetSize() {
    verifyFunction(WebElement::getSize, new Dimension(100, 200));
  }

  @Test
  void testGetRect() {
    verifyFunction(WebElement::getRect, new Rectangle(new Point(10, 20), new Dimension(100, 200)));
  }

  @Test
  void testGetCssValue() {
    verifyFunction($ -> $.getCssValue("color"), "red");
  }

  @Test
  void testGetCoordinates() {
    final Coordinates coords = mock(Coordinates.class);
    verifyDecoratingFunction($ -> ((Locatable) $).getCoordinates(), coords, Coordinates::onScreen);
  }

  @Test
  void testGetScreenshotAs() {
    verifyFunction($ -> $.getScreenshotAs(OutputType.BASE64), "xxx");
  }

  @Test
  void testFindElement() {
    final WebElement found = mock(WebElement.class);
    verifyDecoratingFunction($ -> $.findElement(By.id("test")), found, WebElement::click);
  }

  @Test
  void testFindElements() {
    Fixture fixture = new Fixture();
    WebElement found = mock(WebElement.class);
    List<WebElement> list = new ArrayList<>();
    list.add(found);
    when(fixture.mocked.findElements(By.id("test"))).thenReturn(list);

    List<WebElement> proxyList = fixture.decorated.findElements(By.id("test"));
    // TODO: Why same instance?????
    assertThat(list, sameInstance(proxyList));
    assertThat(found, not(sameInstance(proxyList.get(0))));
    verify(fixture.mocked, times(1)).findElements(By.id("test"));

    proxyList.get(0).isDisplayed();
    verify(found, times(1)).isDisplayed();
  }

}
