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
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DecoratedWebDriverTest {

  private static class Fixture {
    WebDriver mocked;
    DecoratedWebDriver decorated;

    public Fixture() {
      mocked = mock(WebDriver.class,
        withSettings().extraInterfaces(HasInputDevices.class, HasTouchScreen.class, JavascriptExecutor.class));
      decorated = new DecoratedWebDriver(mocked);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getWrappedDriver()));
  }

  // TODO: Implement TakesScreenshot and add the test
  /*@Test
  public void testGetScreenshotAs() {
    Fixture fixture = new Fixture();
    when(fixture.mocked.getScreenshotAs(OutputType.BASE64)).thenReturn("xxx");

    assertThat(fixture.decorated.getScreenshotAs(OutputType.BASE64), equalTo("xxx"));
    verify(fixture.mocked, times(1)).getScreenshotAs(OutputType.BASE64);
  }*/

  private void verifyFunction(Consumer<WebDriver> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyFunction(Function<WebDriver, R> f, R result) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);
    assertThat(f.apply(fixture.decorated), equalTo(result));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyDecoratingFunction(Function<WebDriver, R> f, R result, Consumer<R> p) {
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
  void testGet() {
    verifyFunction($ -> $.get("http://selenium2.ru/"));
  }

  @Test
  void testGetCurrentUrl() {
    verifyFunction(WebDriver::getCurrentUrl, "http://selenium2.ru/");
  }

  @Test
  void testGetTitle() {
    verifyFunction(WebDriver::getTitle, "test");
  }

  @Test
  void testGetPageSource() {
    verifyFunction(WebDriver::getPageSource, "test");
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

  @Test
  void testClose() {
    verifyFunction(WebDriver::close);
  }

  @Test
  void testQuit() {
    verifyFunction(WebDriver::quit);
  }

  @Test
  void testGetWindowHandle() {
    verifyFunction(WebDriver::getWindowHandle, "test");
  }

  @Test
  void testGetWindowHandles() {
    Set<String> handles = new HashSet<>();
    handles.add("test");
    verifyFunction(WebDriver::getWindowHandles, handles);
  }

  @Test
  void testSwitchTo() {
    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    verifyDecoratingFunction(WebDriver::switchTo, target, WebDriver.TargetLocator::defaultContent);
  }

  @Test
  void testNavigate() {
    final WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
    verifyDecoratingFunction(WebDriver::navigate, navigation, WebDriver.Navigation::refresh);
  }

  @Test
  void testManage() {
    final WebDriver.Options options = mock(WebDriver.Options.class);
    verifyDecoratingFunction(WebDriver::manage, options, WebDriver.Options::deleteAllCookies);
  }

  @Test
  void testGetKeyboard() {
    final Keyboard keyboard = mock(Keyboard.class);
    verifyDecoratingFunction($ -> ((HasInputDevices) $).getKeyboard(), keyboard, k -> k.sendKeys("test"));
  }

  @Test
  void testGetMouse() {
    final Mouse  mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);
    verifyDecoratingFunction($ -> ((HasInputDevices) $).getMouse(), mouse, m -> m.click(coords));
  }

  @Test
  void testGetTouchScreen() {
    TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);
    verifyDecoratingFunction($ -> ((HasTouchScreen) $).getTouch(), touch, t -> t.singleTap(coords));
  }

  @Test
  void testExecuteScriptNotSupported() {
    WebDriver mockedDriver = mock(WebDriver.class);
    DecoratedWebDriver decoratedDriver = new DecoratedWebDriver(mockedDriver);

    assertThrows(WebDriverException.class, () -> decoratedDriver.executeScript("..."));
  }

  @Test
  void testExecuteScriptThatReturnsAPrimitive() {
    verifyFunction($ -> ((JavascriptExecutor) $).executeScript("..."), 1);
  }

  @Test
  void testExecuteScriptThatReturnsAnElement() {
    WebElement element = mock(WebElement.class);
    verifyDecoratingFunction($ -> (WebElement) ((JavascriptExecutor) $).executeScript("..."), element, WebElement::click);
  }

  @Test
  void testExecuteAsyncScriptNotSupported() {
    WebDriver mockedDriver = mock(WebDriver.class);
    DecoratedWebDriver decoratedDriver = new DecoratedWebDriver(mockedDriver);

    assertThrows(WebDriverException.class, () -> decoratedDriver.executeAsyncScript("..."));
  }

  @Test
  void testExecuteAsyncScriptThatReturnsAPrimitive() {
    verifyFunction($ -> ((JavascriptExecutor) $).executeAsyncScript("..."), 1);
  }

  @Test
  void testExecuteAsyncScriptThatReturnsAnElement() {
    WebElement element = mock(WebElement.class);
    verifyDecoratingFunction($ -> (WebElement) ((JavascriptExecutor) $).executeAsyncScript("..."), element, WebElement::click);
  }

}
