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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedWebDriverTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;

    public Fixture() {
      mockedDriver = mock(WebDriver.class,
        withSettings().extraInterfaces(HasInputDevices.class, HasTouchScreen.class, JavascriptExecutor.class));
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedDriver, sameInstance(fixture.decoratedDriver.getOriginal()));
    assertThat(fixture.mockedDriver, sameInstance(fixture.decoratedDriver.getWrappedDriver()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedDriver.getTopmostDecorated()));
  }

  // TODO: Implement TakesScreenshot and add the test
  /*@Test
  public void testGetScreenshotAs() {
    Fixture fixture = new Fixture();
    when(fixture.mockedDriver.getScreenshotAs(OutputType.BASE64)).thenReturn("xxx");

    assertThat(fixture.decoratedElement.getScreenshotAs(OutputType.BASE64), equalTo("xxx"));
    verify(fixture.mockedElement, times(1)).getScreenshotAs(OutputType.BASE64);
  }*/

  @Test
  public void testGet() {
    Fixture fixture = new Fixture();

    fixture.decoratedDriver.get("test");
    verify(fixture.mockedDriver, times(1)).get("test");
  }

  @Test
  public void testGetCurrentUrl() {
    Fixture fixture = new Fixture();
    when(fixture.mockedDriver.getCurrentUrl()).thenReturn("test");

    assertThat(fixture.decoratedDriver.getCurrentUrl(), equalTo("test"));
    verify(fixture.mockedDriver, times(1)).getCurrentUrl();
  }

  @Test
  public void testGetTitle() {
    Fixture fixture = new Fixture();
    when(fixture.mockedDriver.getTitle()).thenReturn("test");

    assertThat(fixture.decoratedDriver.getTitle(), equalTo("test"));
    verify(fixture.mockedDriver, times(1)).getTitle();
  }

  @Test
  public void testGetPageSource() {
    Fixture fixture = new Fixture();
    when(fixture.mockedDriver.getPageSource()).thenReturn("test");

    assertThat(fixture.decoratedDriver.getPageSource(), equalTo("test"));
    verify(fixture.mockedDriver, times(1)).getPageSource();
  }

  @Test
  public void testFindElement() {
    Fixture fixture = new Fixture();
    WebElement found = mock(WebElement.class);
    when(fixture.mockedDriver.findElement(By.id("test"))).thenReturn(found);

    WebElement proxy = fixture.decoratedDriver.findElement(By.id("test"));
    assertThat(found, not(sameInstance(proxy)));
    verify(fixture.mockedDriver, times(1)).findElement(By.id("test"));

    proxy.isDisplayed();
    verify(found, times(1)).isDisplayed();
  }

  @Test
  public void testFindElements() {
    Fixture fixture = new Fixture();
    WebElement found = mock(WebElement.class);
    List<WebElement> list = new ArrayList<WebElement>();
    list.add(found);
    when(fixture.mockedDriver.findElements(By.id("test"))).thenReturn(list);

    List<WebElement> proxyList = fixture.decoratedDriver.findElements(By.id("test"));
    // TODO: Why same instance?????
    assertThat(list, sameInstance(proxyList));
    assertThat(found, not(sameInstance(proxyList.get(0))));
    verify(fixture.mockedDriver, times(1)).findElements(By.id("test"));

    proxyList.get(0).isDisplayed();
    verify(found, times(1)).isDisplayed();
  }

  @Test
  public void testClose() {
    Fixture fixture = new Fixture();

    fixture.decoratedDriver.close();
    verify(fixture.mockedDriver, times(1)).close();
  }

  @Test
  public void testQuit() {
    Fixture fixture = new Fixture();

    fixture.decoratedDriver.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testGetWindowHandle() {
    Fixture fixture = new Fixture();
    when(fixture.mockedDriver.getWindowHandle()).thenReturn("test");

    assertThat(fixture.decoratedDriver.getWindowHandle(), equalTo("test"));
    verify(fixture.mockedDriver, times(1)).getWindowHandle();
  }

  @Test
  public void testGetWindowHandles() {
    Fixture fixture = new Fixture();
    Set<String> handles = new HashSet<String>();
    handles.add("test");
    when(fixture.mockedDriver.getWindowHandles()).thenReturn(handles);

    assertThat(fixture.decoratedDriver.getWindowHandles(), equalTo(handles));
    verify(fixture.mockedDriver, times(1)).getWindowHandles();
  }

  @Test
  public void testSwitchTo() {
    Fixture fixture = new Fixture();
    WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    WebElement mockedElement = mock(WebElement.class);
    when(mockedElement.isDisplayed()).thenReturn(true);
    when(target.activeElement()).thenReturn(mockedElement);
    when(fixture.mockedDriver.switchTo()).thenReturn(target);

    WebDriver.TargetLocator proxy = fixture.decoratedDriver.switchTo();
    assertThat(target, not(sameInstance(proxy)));
    verify(fixture.mockedDriver, times(1)).switchTo();

    WebElement elementProxy = proxy.activeElement();
    assertThat(mockedElement, not(sameInstance(elementProxy)));
    verify(target, times(1)).activeElement();

    assertThat(elementProxy.isDisplayed(), equalTo(true));
    verify(mockedElement, times(1)).isDisplayed();
  }

  @Test
  public void testNavigate() {
    Fixture fixture = new Fixture();
    WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigation);

    WebDriver.Navigation proxy = fixture.decoratedDriver.navigate();
    assertThat(navigation, not(sameInstance(proxy)));
    verify(fixture.mockedDriver, times(1)).navigate();

    proxy.refresh();
    verify(navigation, times(1)).refresh();
  }

  @Test
  public void testManage() {
    Fixture fixture = new Fixture();
    WebDriver.Options options = mock(WebDriver.Options.class);
    when(fixture.mockedDriver.manage()).thenReturn(options);

    WebDriver.Options proxy = fixture.decoratedDriver.manage();
    assertThat(options, not(sameInstance(proxy)));
    verify(fixture.mockedDriver, times(1)).manage();

    proxy.deleteAllCookies();
    verify(options, times(1)).deleteAllCookies();
  }

  @Test
  public void testGetKeyboard() {
    Fixture fixture = new Fixture();
    Keyboard keyboard = mock(Keyboard.class);
    when(((HasInputDevices) fixture.mockedDriver).getKeyboard()).thenReturn(keyboard);

    Keyboard proxy = fixture.decoratedDriver.getKeyboard();
    assertThat(keyboard, not(sameInstance(proxy)));
    verify((HasInputDevices) fixture.mockedDriver, times(1)).getKeyboard();

    proxy.sendKeys("test");
    verify(keyboard, times(1)).sendKeys("test");
  }

  @Test
  public void testGetMouse() {
    Fixture fixture = new Fixture();
    Mouse  mouse = mock(Mouse.class);
    when(((HasInputDevices) fixture.mockedDriver).getMouse()).thenReturn(mouse);

    Mouse proxy = fixture.decoratedDriver.getMouse();
    assertThat(mouse, not(sameInstance(proxy)));
    verify((HasInputDevices) fixture.mockedDriver, times(1)).getMouse();

    Coordinates coords = mock(Coordinates.class);
    proxy.click(coords);
    verify(mouse, times(1)).click(coords);
  }

  @Test
  public void testGetTouchScreen() {
    Fixture fixture = new Fixture();
    TouchScreen touch = mock(TouchScreen.class);
    when(((HasTouchScreen) fixture.mockedDriver).getTouch()).thenReturn(touch);

    TouchScreen proxy = fixture.decoratedDriver.getTouch();
    assertThat(touch, not(sameInstance(proxy)));
    verify((HasTouchScreen) fixture.mockedDriver, times(1)).getTouch();

    Coordinates coords = mock(Coordinates.class);
    proxy.singleTap(coords);
    verify(touch, times(1)).singleTap(coords);
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
    Fixture fixture = new Fixture();
    when(((JavascriptExecutor) fixture.mockedDriver).executeScript("...")).thenReturn(1);

    fixture.decoratedDriver.executeScript("...");
    assertThat(1, equalTo(fixture.decoratedDriver.executeScript("...")));
  }

  @Test
  public void testExecuteScriptThatReturnsAnElement() {
    Fixture fixture = new Fixture();
    WebElement element = mock(WebElement.class);
    when(((JavascriptExecutor) fixture.mockedDriver).executeScript("...")).thenReturn(element);

    WebElement proxy = (WebElement) fixture.decoratedDriver.executeScript("...");
    assertThat(element, not(sameInstance(proxy)));
    verify((JavascriptExecutor) fixture.mockedDriver, times(1)).executeScript("...");

    proxy.isDisplayed();
    verify(element, times(1)).isDisplayed();
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
    Fixture fixture = new Fixture();
    when(((JavascriptExecutor) fixture.mockedDriver).executeAsyncScript("...")).thenReturn(1);

    fixture.decoratedDriver.executeAsyncScript("...");
    assertThat(1, equalTo(fixture.decoratedDriver.executeAsyncScript("...")));
  }

  @Test
  public void testExecuteAsyncScriptThatReturnsAnElement() {
    Fixture fixture = new Fixture();
    WebElement element = mock(WebElement.class);
    when(((JavascriptExecutor) fixture.mockedDriver).executeAsyncScript("...")).thenReturn(element);

    WebElement proxy = (WebElement) fixture.decoratedDriver.executeAsyncScript("...");
    assertThat(element, not(sameInstance(proxy)));
    verify((JavascriptExecutor) fixture.mockedDriver, times(1)).executeAsyncScript("...");

    proxy.isDisplayed();
    verify(element, times(1)).isDisplayed();
  }

}
