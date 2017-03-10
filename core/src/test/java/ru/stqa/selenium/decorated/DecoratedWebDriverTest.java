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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedWebDriverTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

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
  public void testConstructor() {
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
  public void testGet() {
    verifyFunction($ -> $.get("http://selenium2.ru/"));
  }

  @Test
  public void testGetCurrentUrl() {
    verifyFunction(WebDriver::getCurrentUrl, "http://selenium2.ru/");
  }

  @Test
  public void testGetTitle() {
    verifyFunction(WebDriver::getTitle, "test");
  }

  @Test
  public void testGetPageSource() {
    verifyFunction(WebDriver::getPageSource, "test");
  }

  @Test
  public void testFindElement() {
    final WebElement found = mock(WebElement.class);
    verifyDecoratingFunction($ -> $.findElement(By.id("test")), found, WebElement::click);
  }

  @Test
  public void testFindElements() {
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
  public void testClose() {
    verifyFunction(WebDriver::close);
  }

  @Test
  public void testQuit() {
    verifyFunction(WebDriver::quit);
  }

  @Test
  public void testGetWindowHandle() {
    verifyFunction(WebDriver::getWindowHandle, "test");
  }

  @Test
  public void testGetWindowHandles() {
    Set<String> handles = new HashSet<>();
    handles.add("test");
    verifyFunction(WebDriver::getWindowHandles, handles);
  }

  @Test
  public void testSwitchTo() {
    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    verifyDecoratingFunction(WebDriver::switchTo, target, WebDriver.TargetLocator::defaultContent);
  }

  @Test
  public void testNavigate() {
    final WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
    verifyDecoratingFunction(WebDriver::navigate, navigation, WebDriver.Navigation::refresh);
  }

  @Test
  public void testManage() {
    final WebDriver.Options options = mock(WebDriver.Options.class);
    verifyDecoratingFunction(WebDriver::manage, options, WebDriver.Options::deleteAllCookies);
  }

  @Test
  public void testGetKeyboard() {
    final Keyboard keyboard = mock(Keyboard.class);
    verifyDecoratingFunction($ -> ((HasInputDevices) $).getKeyboard(), keyboard, k -> k.sendKeys("test"));
  }

  @Test
  public void testGetMouse() {
    final Mouse  mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);
    verifyDecoratingFunction($ -> ((HasInputDevices) $).getMouse(), mouse, m -> m.click(coords));
  }

  @Test
  public void testGetTouchScreen() {
    TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);
    verifyDecoratingFunction($ -> ((HasTouchScreen) $).getTouch(), touch, t -> t.singleTap(coords));
  }

  @Test
  public void testExecuteScriptNotSupported() {
    WebDriver mockedDriver = mock(WebDriver.class);
    DecoratedWebDriver decoratedDriver = new DecoratedWebDriver(mockedDriver);

    expectedEx.expect(WebDriverException.class);
    decoratedDriver.executeScript("...");
  }

  @Test
  public void testExecuteScriptThatReturnsAPrimitive() {
    verifyFunction($ -> ((JavascriptExecutor) $).executeScript("..."), 1);
  }

  @Test
  public void testExecuteScriptThatReturnsAnElement() {
    WebElement element = mock(WebElement.class);
    verifyDecoratingFunction($ -> (WebElement) ((JavascriptExecutor) $).executeScript("..."), element, WebElement::click);
  }

  @Test
  public void testExecuteAsyncScriptNotSupported() {
    WebDriver mockedDriver = mock(WebDriver.class);
    DecoratedWebDriver decoratedDriver = new DecoratedWebDriver(mockedDriver);

    expectedEx.expect(WebDriverException.class);
    decoratedDriver.executeAsyncScript("...");
  }

  @Test
  public void testExecuteAsyncScriptThatReturnsAPrimitive() {
    verifyFunction($ -> ((JavascriptExecutor) $).executeAsyncScript("..."), 1);
  }

  @Test
  public void testExecuteAsyncScriptThatReturnsAnElement() {
    WebElement element = mock(WebElement.class);
    verifyDecoratingFunction($ -> (WebElement) ((JavascriptExecutor) $).executeAsyncScript("..."), element, WebElement::click);
  }

}
