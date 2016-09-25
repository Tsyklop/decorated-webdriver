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

package ru.stqa.selenium.decorated.eventfiring;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class WebDriverListenerTest {

  interface WebDriverWithJS extends WebDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen {}

  private static class Fixture {
    WebDriverWithJS mockedDriver;
    WebDriverListener listener;
    EventFiringWebDriver decoratedDriver;
    WebDriverWithJS driver;

    public Fixture() {
      mockedDriver = mock(WebDriverWithJS.class);
      listener = spy(new WebDriverListener() {});
      decoratedDriver = new EventFiringWebDriver(mockedDriver);
      decoratedDriver.addListener(listener);
      driver = (WebDriverWithJS) decoratedDriver.getActivated();
    }
  }

  @Test
  public void canFireEventForGet() {
    Fixture fixture = new Fixture();

    fixture.driver.get("http://localhost/");

    verify(fixture.mockedDriver, times(1)).get("http://localhost/");
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGet(fixture.mockedDriver, "http://localhost/");
    verify(fixture.listener, times(1)).afterGet(fixture.mockedDriver, "http://localhost/");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetCurrentUrl() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getCurrentUrl()).thenReturn("http://localhost/");

    assertEquals(fixture.driver.getCurrentUrl(), "http://localhost/");

    verify(fixture.mockedDriver, times(1)).getCurrentUrl();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGetCurrentUrl(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetCurrentUrl("http://localhost/", fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetTitle() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getTitle()).thenReturn("Home page");

    assertEquals(fixture.driver.getTitle(), "Home page");

    verify(fixture.mockedDriver, times(1)).getTitle();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGetTitle(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetTitle("Home page", fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForFindElement() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    WebElement result = fixture.driver.findElement(By.id("id"));

    assertEquals(result, mockedElement);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForFindElements() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    final List<WebElement> list = new ArrayList<>();
    list.add(mockedElement);
    when(fixture.mockedDriver.findElements(By.id("id"))).thenReturn(list);

    List<WebElement> result = fixture.driver.findElements(By.id("id"));

    assertEquals(result, list);

    verify(fixture.mockedDriver, times(1)).findElements(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElements(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElements(list, fixture.mockedDriver, By.id("id"));
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetPageSource() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getPageSource()).thenReturn("<html></html>");

    assertEquals(fixture.driver.getPageSource(), "<html></html>");

    verify(fixture.mockedDriver, times(1)).getPageSource();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGetPageSource(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetPageSource("<html></html>", fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForClose() {
    Fixture fixture = new Fixture();

    fixture.driver.close();

    verify(fixture.mockedDriver, times(1)).close();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeClose(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterClose(fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForQuit() {
    Fixture fixture = new Fixture();

    fixture.driver.quit();

    verify(fixture.mockedDriver, times(1)).quit();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeQuit(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterQuit(fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetWindowHandles() {
    Fixture fixture = new Fixture();

    final Set<String> handles = new HashSet<>();
    handles.add("window1");
    when(fixture.mockedDriver.getWindowHandles()).thenReturn(handles);

    Set<String> result = fixture.driver.getWindowHandles();

    assertEquals(result, handles);

    verify(fixture.mockedDriver, times(1)).getWindowHandles();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGetWindowHandles(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetWindowHandles(handles, fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetWindowHandle() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getWindowHandle()).thenReturn("window1");

    assertEquals(fixture.driver.getWindowHandle(), "window1");

    verify(fixture.mockedDriver, times(1)).getWindowHandle();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeGetWindowHandle(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetWindowHandle("window1", fixture.mockedDriver);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForExecuteScript() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.executeScript("return arguments[0]", "test")).thenReturn("result");

    Object result = fixture.driver.executeScript("return arguments[0]", "test");

    assertEquals(result, "result");

    verify(fixture.mockedDriver, times(1)).executeScript("return arguments[0]", "test");
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeExecuteScript(fixture.mockedDriver, "return arguments[0]", "test");
    verify(fixture.listener, times(1)).afterExecuteScript("result", fixture.mockedDriver, "return arguments[0]", "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForExecuteAsyncScript() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.executeAsyncScript("return arguments[0]", "test")).thenReturn("result");

    Object result = fixture.driver.executeAsyncScript("return arguments[0]", "test");

    assertEquals(result, "result");

    verify(fixture.mockedDriver, times(1)).executeAsyncScript("return arguments[0]", "test");
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeExecuteAsyncScript(fixture.mockedDriver, "return arguments[0]", "test");
    verify(fixture.listener, times(1)).afterExecuteAsyncScript("result", fixture.mockedDriver, "return arguments[0]", "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementClick() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).click();

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).click();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeClick(mockedElement);
    verify(fixture.listener, times(1)).afterClick(mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementSubmit() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).submit();

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).submit();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeSubmit(mockedElement);
    verify(fixture.listener, times(1)).afterSubmit(mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementSendKeys() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).sendKeys("test");

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).sendKeys("test");
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeSendKeys(mockedElement, "test");
    verify(fixture.listener, times(1)).afterSendKeys(mockedElement, "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementClear() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).clear();

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).clear();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeClear(mockedElement);
    verify(fixture.listener, times(1)).afterClear(mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetTagName() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getTagName()).thenReturn("input");

    assertEquals(fixture.driver.findElement(By.id("id")).getTagName(), "input");

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getTagName();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetTagName(mockedElement);
    verify(fixture.listener, times(1)).afterGetTagName("input", mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetAttribute() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getAttribute("name")).thenReturn("test");

    assertEquals(fixture.driver.findElement(By.id("id")).getAttribute("name"), "test");

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getAttribute("name");
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetAttribute(mockedElement, "name");
    verify(fixture.listener, times(1)).afterGetAttribute("test", mockedElement, "name");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementIsSelected() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isSelected(), true);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).isSelected();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeIsSelected(mockedElement);
    verify(fixture.listener, times(1)).afterIsSelected(true, mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementIsEnabled() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isEnabled(), true);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).isEnabled();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeIsEnabled(mockedElement);
    verify(fixture.listener, times(1)).afterIsEnabled(true, mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetText() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn("test");

    assertEquals(fixture.driver.findElement(By.id("id")).getText(), "test");

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getText();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetText(mockedElement);
    verify(fixture.listener, times(1)).afterGetText("test", mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementIsDisplayed() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isDisplayed(), true);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).isDisplayed();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeIsDisplayed(mockedElement);
    verify(fixture.listener, times(1)).afterIsDisplayed(true, mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForRelativeFindElement() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    when(fixture.mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElement(By.id("id2"))).thenReturn(mockedElement2);

    WebElement result = fixture.driver.findElement(By.id("id1")).findElement(By.id("id2"));

    assertEquals(result, mockedElement2);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id1"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id1"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id1"));
    verify(mockedElement, times(1)).findElement(By.id("id2"));
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeFindElement(mockedElement, By.id("id2"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement2, mockedElement, By.id("id2"));
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForRelativeFindElements() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> list = new ArrayList<>();
    list.add(mockedElement2);
    when(fixture.mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElements(By.id("id2"))).thenReturn(list);

    List<WebElement> result = fixture.driver.findElement(By.id("id1")).findElements(By.id("id2"));

    assertEquals(result, list);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id1"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id1"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id1"));
    verify(mockedElement, times(1)).findElements(By.id("id2"));
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeFindElements(mockedElement, By.id("id2"));
    verify(fixture.listener, times(1)).afterFindElements(list, mockedElement, By.id("id2"));
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetLocation() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    final Point location = new Point(10, 20);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getLocation()).thenReturn(location);

    assertSame(fixture.driver.findElement(By.id("id")).getLocation(), location);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getLocation();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetLocation(mockedElement);
    verify(fixture.listener, times(1)).afterGetLocation(location, mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetSize() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    final Dimension dimension = new Dimension(10, 20);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getSize()).thenReturn(dimension);

    assertSame(fixture.driver.findElement(By.id("id")).getSize(), dimension);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getSize();
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetSize(mockedElement);
    verify(fixture.listener, times(1)).afterGetSize(dimension, mockedElement);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWebElementGetCssValue() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getCssValue("color")).thenReturn("red");

    assertEquals(fixture.driver.findElement(By.id("id")).getCssValue("color"), "red");

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
    verify(mockedElement, times(1)).getCssValue("color");
    verifyNoMoreInteractions(mockedElement);
    verify(fixture.listener, times(1)).beforeGetCssValue(mockedElement, "color");
    verify(fixture.listener, times(1)).afterGetCssValue("red", mockedElement, "color");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForNavigateTo() {
    Fixture fixture = new Fixture();

    WebDriver.Navigation navigate = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigate);

    fixture.driver.navigate().to("http://localhost/");

    verify(fixture.mockedDriver, times(1)).navigate();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(navigate, times(1)).to("http://localhost/");
    verifyNoMoreInteractions(navigate);
    verify(fixture.listener, times(1)).beforeTo(navigate, "http://localhost/");
    verify(fixture.listener, times(1)).afterTo(navigate, "http://localhost/");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForNavigateToUrl() throws MalformedURLException {
    Fixture fixture = new Fixture();

    WebDriver.Navigation navigate = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigate);

    URL localhost = new URL("http://localhost/");

    fixture.driver.navigate().to(localhost);

    verify(fixture.mockedDriver, times(1)).navigate();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(navigate, times(1)).to(localhost);
    verifyNoMoreInteractions(navigate);
    verify(fixture.listener, times(1)).beforeTo(navigate, localhost);
    verify(fixture.listener, times(1)).afterTo(navigate, localhost);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForNavigateBack() {
    Fixture fixture = new Fixture();

    WebDriver.Navigation navigate = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigate);

    fixture.driver.navigate().back();

    verify(fixture.mockedDriver, times(1)).navigate();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(navigate, times(1)).back();
    verifyNoMoreInteractions(navigate);
    verify(fixture.listener, times(1)).beforeBack(navigate);
    verify(fixture.listener, times(1)).afterBack(navigate);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForNavigateForward() {
    Fixture fixture = new Fixture();

    WebDriver.Navigation navigate = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigate);

    fixture.driver.navigate().forward();

    verify(fixture.mockedDriver, times(1)).navigate();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(navigate, times(1)).forward();
    verifyNoMoreInteractions(navigate);
    verify(fixture.listener, times(1)).beforeForward(navigate);
    verify(fixture.listener, times(1)).afterForward(navigate);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForRefresh() {
    Fixture fixture = new Fixture();

    WebDriver.Navigation navigate = mock(WebDriver.Navigation.class);
    when(fixture.mockedDriver.navigate()).thenReturn(navigate);

    fixture.driver.navigate().refresh();

    verify(fixture.mockedDriver, times(1)).navigate();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(navigate, times(1)).refresh();
    verifyNoMoreInteractions(navigate);
    verify(fixture.listener, times(1)).beforeRefresh(navigate);
    verify(fixture.listener, times(1)).afterRefresh(navigate);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForAlertAccept() {
    Fixture fixture = new Fixture();

    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    final Alert alert = mock(Alert.class);

    when(fixture.mockedDriver.switchTo()).thenReturn(target);
    when(target.alert()).thenReturn(alert);

    fixture.driver.switchTo().alert().accept();

    verify(fixture.mockedDriver, times(1)).switchTo();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(target, times(1)).alert();
    verifyNoMoreInteractions(target);
    verify(alert, times(1)).accept();
    verifyNoMoreInteractions(alert);
    verify(fixture.listener, times(1)).beforeAccept(alert);
    verify(fixture.listener, times(1)).afterAccept(alert);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForAlertDismiss() {
    Fixture fixture = new Fixture();

    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    final Alert alert = mock(Alert.class);

    when(fixture.mockedDriver.switchTo()).thenReturn(target);
    when(target.alert()).thenReturn(alert);

    fixture.driver.switchTo().alert().dismiss();

    verify(fixture.mockedDriver, times(1)).switchTo();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(target, times(1)).alert();
    verifyNoMoreInteractions(target);
    verify(alert, times(1)).dismiss();
    verifyNoMoreInteractions(alert);
    verify(fixture.listener, times(1)).beforeDismiss(alert);
    verify(fixture.listener, times(1)).afterDismiss(alert);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForAlertGetText() {
    Fixture fixture = new Fixture();

    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    final Alert alert = mock(Alert.class);

    when(fixture.mockedDriver.switchTo()).thenReturn(target);
    when(target.alert()).thenReturn(alert);
    when(alert.getText()).thenReturn("test");

    assertEquals(fixture.driver.switchTo().alert().getText(), "test");

    verify(fixture.mockedDriver, times(1)).switchTo();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(target, times(1)).alert();
    verifyNoMoreInteractions(target);
    verify(alert, times(1)).getText();
    verifyNoMoreInteractions(alert);
    verify(fixture.listener, times(1)).beforeGetText(alert);
    verify(fixture.listener, times(1)).afterGetText("test", alert);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForAlertSendKeys() {
    Fixture fixture = new Fixture();

    final WebDriver.TargetLocator target = mock(WebDriver.TargetLocator.class);
    final Alert alert = mock(Alert.class);

    when(fixture.mockedDriver.switchTo()).thenReturn(target);
    when(target.alert()).thenReturn(alert);

    fixture.driver.switchTo().alert().sendKeys("test");

    verify(fixture.mockedDriver, times(1)).switchTo();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(target, times(1)).alert();
    verifyNoMoreInteractions(target);
    verify(alert, times(1)).sendKeys("test");
    verifyNoMoreInteractions(alert);
    verify(fixture.listener, times(1)).beforeSendKeys(alert, "test");
    verify(fixture.listener, times(1)).afterSendKeys(alert, "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForAddCookie() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    Cookie cookie = new Cookie("name", "value");

    when(fixture.mockedDriver.manage()).thenReturn(options);

    fixture.driver.manage().addCookie(cookie);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).addCookie(cookie);
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeAddCookie(options, cookie);
    verify(fixture.listener, times(1)).afterAddCookie(options, cookie);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForDeleteCookieNamed() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);

    fixture.driver.manage().deleteCookieNamed("name");

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).deleteCookieNamed("name");
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeDeleteCookieNamed(options, "name");
    verify(fixture.listener, times(1)).afterDeleteCookieNamed(options, "name");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForDeleteCookie() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    Cookie cookie = new Cookie("name", "value");

    when(fixture.mockedDriver.manage()).thenReturn(options);

    fixture.driver.manage().deleteCookie(cookie);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).deleteCookie(cookie);
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeDeleteCookie(options, cookie);
    verify(fixture.listener, times(1)).afterDeleteCookie(options, cookie);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForDeleteAllCookies() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);

    fixture.driver.manage().deleteAllCookies();

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).deleteAllCookies();
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeDeleteAllCookies(options);
    verify(fixture.listener, times(1)).afterDeleteAllCookies(options);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetCookies() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    Cookie cookie = new Cookie("name", "value");
    Set<Cookie> cookies = new HashSet<>();
    cookies.add(cookie);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.getCookies()).thenReturn(cookies);

    assertEquals(fixture.driver.manage().getCookies(), cookies);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).getCookies();
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeGetCookies(options);
    verify(fixture.listener, times(1)).afterGetCookies(cookies, options);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForGetCookieNamed() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    Cookie cookie = new Cookie("name", "value");

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.getCookieNamed("test")).thenReturn(cookie);

    assertEquals(fixture.driver.manage().getCookieNamed("test"), cookie);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).getCookieNamed("test");
    verifyNoMoreInteractions(options);
    verify(fixture.listener, times(1)).beforeGetCookieNamed(options, "test");
    verify(fixture.listener, times(1)).afterGetCookieNamed(cookie, options, "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForImplicitlyWait() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Timeouts timeouts = mock(WebDriver.Timeouts.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.timeouts()).thenReturn(timeouts);

    fixture.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).timeouts();
    verifyNoMoreInteractions(options);
    verify(timeouts, times(1)).implicitlyWait(10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(timeouts);
    verify(fixture.listener, times(1)).beforeImplicitlyWait(timeouts, 10, TimeUnit.SECONDS);
    verify(fixture.listener, times(1)).afterImplicitlyWait(timeouts, 10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForSetScriptTimeout() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Timeouts timeouts = mock(WebDriver.Timeouts.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.timeouts()).thenReturn(timeouts);

    fixture.driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).timeouts();
    verifyNoMoreInteractions(options);
    verify(timeouts, times(1)).setScriptTimeout(10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(timeouts);
    verify(fixture.listener, times(1)).beforeSetScriptTimeout(timeouts, 10, TimeUnit.SECONDS);
    verify(fixture.listener, times(1)).afterSetScriptTimeout(timeouts, 10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForPageLoadTimeout() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Timeouts timeouts = mock(WebDriver.Timeouts.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.timeouts()).thenReturn(timeouts);

    fixture.driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).timeouts();
    verifyNoMoreInteractions(options);
    verify(timeouts, times(1)).pageLoadTimeout(10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(timeouts);
    verify(fixture.listener, times(1)).beforePageLoadTimeout(timeouts, 10, TimeUnit.SECONDS);
    verify(fixture.listener, times(1)).afterPageLoadTimeout(timeouts, 10, TimeUnit.SECONDS);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowGetSize() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);
    final Dimension dimension = new Dimension(10, 20);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);
    when(window.getSize()).thenReturn(dimension);

    assertEquals(fixture.driver.manage().window().getSize(), dimension);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).getSize();
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeGetSize(window);
    verify(fixture.listener, times(1)).afterGetSize(dimension, window);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowSetSize() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);
    final Dimension dimension = new Dimension(10, 20);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);

    fixture.driver.manage().window().setSize(dimension);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).setSize(dimension);
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeSetSize(window, dimension);
    verify(fixture.listener, times(1)).afterSetSize(window, dimension);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowGetPosition() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);
    final Point position = new Point(10, 20);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);
    when(window.getPosition()).thenReturn(position);

    assertEquals(fixture.driver.manage().window().getPosition(), position);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).getPosition();
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeGetPosition(window);
    verify(fixture.listener, times(1)).afterGetPosition(position, window);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowSetPosition() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);
    final Point position = new Point(10, 20);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);

    fixture.driver.manage().window().setPosition(position);

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).setPosition(position);
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeSetPosition(window, position);
    verify(fixture.listener, times(1)).afterSetPosition(window, position);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowMaximize() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);

    fixture.driver.manage().window().maximize();

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).maximize();
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeMaximize(window);
    verify(fixture.listener, times(1)).afterMaximize(window);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForWindowFullscreen() {
    Fixture fixture = new Fixture();

    final WebDriver.Options options = mock(WebDriver.Options.class);
    final WebDriver.Window window = mock(WebDriver.Window.class);

    when(fixture.mockedDriver.manage()).thenReturn(options);
    when(options.window()).thenReturn(window);

    fixture.driver.manage().window().fullscreen();

    verify(fixture.mockedDriver, times(1)).manage();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(options, times(1)).window();
    verifyNoMoreInteractions(options);
    verify(window, times(1)).fullscreen();
    verifyNoMoreInteractions(window);
    verify(fixture.listener, times(1)).beforeFullscreen(window);
    verify(fixture.listener, times(1)).afterFullscreen(window);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForKeyboardSendKeys() {
    Fixture fixture = new Fixture();

    final Keyboard keyboard = mock(Keyboard.class);

    when(fixture.mockedDriver.getKeyboard()).thenReturn(keyboard);

    fixture.driver.getKeyboard().sendKeys("test");

    verify(fixture.mockedDriver, times(1)).getKeyboard();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(keyboard, times(1)).sendKeys("test");
    verifyNoMoreInteractions(keyboard);
    verify(fixture.listener, times(1)).beforeSendKeys(keyboard, "test");
    verify(fixture.listener, times(1)).afterSendKeys(keyboard, "test");
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForKeyboardPressKey() {
    Fixture fixture = new Fixture();

    final Keyboard keyboard = mock(Keyboard.class);

    when(fixture.mockedDriver.getKeyboard()).thenReturn(keyboard);

    fixture.driver.getKeyboard().pressKey(Keys.CONTROL);

    verify(fixture.mockedDriver, times(1)).getKeyboard();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(keyboard, times(1)).pressKey(Keys.CONTROL);
    verifyNoMoreInteractions(keyboard);
    verify(fixture.listener, times(1)).beforePressKey(keyboard, Keys.CONTROL);
    verify(fixture.listener, times(1)).afterPressKey(keyboard, Keys.CONTROL);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForKeyboardReleaseKey() {
    Fixture fixture = new Fixture();

    final Keyboard keyboard = mock(Keyboard.class);

    when(fixture.mockedDriver.getKeyboard()).thenReturn(keyboard);

    fixture.driver.getKeyboard().releaseKey(Keys.CONTROL);

    verify(fixture.mockedDriver, times(1)).getKeyboard();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(keyboard, times(1)).releaseKey(Keys.CONTROL);
    verifyNoMoreInteractions(keyboard);
    verify(fixture.listener, times(1)).beforeReleaseKey(keyboard, Keys.CONTROL);
    verify(fixture.listener, times(1)).afterReleaseKey(keyboard, Keys.CONTROL);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseClick() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().click(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).click(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeClick(mouse, coords);
    verify(fixture.listener, times(1)).afterClick(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseDoubleClick() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().doubleClick(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).doubleClick(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeDoubleClick(mouse, coords);
    verify(fixture.listener, times(1)).afterDoubleClick(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseContextClick() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().contextClick(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).contextClick(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeContextClick(mouse, coords);
    verify(fixture.listener, times(1)).afterContextClick(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseDown() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().mouseDown(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).mouseDown(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeMouseDown(mouse, coords);
    verify(fixture.listener, times(1)).afterMouseDown(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseUp() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().mouseUp(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).mouseUp(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeMouseUp(mouse, coords);
    verify(fixture.listener, times(1)).afterMouseUp(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseMove() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().mouseMove(coords);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).mouseMove(coords);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeMouseMove(mouse, coords);
    verify(fixture.listener, times(1)).afterMouseMove(mouse, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForMouseMoveRelative() {
    Fixture fixture = new Fixture();

    final Mouse mouse = mock(Mouse.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getMouse()).thenReturn(mouse);

    fixture.driver.getMouse().mouseMove(coords, 10, 20);

    verify(fixture.mockedDriver, times(1)).getMouse();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(mouse, times(1)).mouseMove(coords, 10, 20);
    verifyNoMoreInteractions(mouse);
    verify(fixture.listener, times(1)).beforeMouseMove(mouse, coords, 10, 20);
    verify(fixture.listener, times(1)).afterMouseMove(mouse, coords, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForSingleTap() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().singleTap(coords);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).singleTap(coords);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeSingleTap(touch, coords);
    verify(fixture.listener, times(1)).afterSingleTap(touch, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForDoubleTap() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().doubleTap(coords);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).doubleTap(coords);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeDoubleTap(touch, coords);
    verify(fixture.listener, times(1)).afterDoubleTap(touch, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForLongPress() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().longPress(coords);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).longPress(coords);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeLongPress(touch, coords);
    verify(fixture.listener, times(1)).afterLongPress(touch, coords);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForTouchDown() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().down(10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).down(10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeDown(touch, 10, 20);
    verify(fixture.listener, times(1)).afterDown(touch, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForTouchUp() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().up(10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).up(10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeUp(touch, 10, 20);
    verify(fixture.listener, times(1)).afterUp(touch, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForTouchMove() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().move(10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).move(10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeMove(touch, 10, 20);
    verify(fixture.listener, times(1)).afterMove(touch, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForScroll() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().scroll(10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).scroll(10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeScroll(touch, 10, 20);
    verify(fixture.listener, times(1)).afterScroll(touch, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForScrollRelative() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().scroll(coords, 10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).scroll(coords, 10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeScroll(touch, coords, 10, 20);
    verify(fixture.listener, times(1)).afterScroll(touch, coords, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForFlick() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().flick(10, 20);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).flick(10, 20);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeFlick(touch, 10, 20);
    verify(fixture.listener, times(1)).afterFlick(touch, 10, 20);
    verifyNoMoreInteractions(fixture.listener);
  }

  @Test
  public void canFireEventForFlickRelative() {
    Fixture fixture = new Fixture();

    final TouchScreen touch = mock(TouchScreen.class);
    final Coordinates coords = mock(Coordinates.class);

    when(fixture.mockedDriver.getTouch()).thenReturn(touch);

    fixture.driver.getTouch().flick(coords, 10, 20, 5);

    verify(fixture.mockedDriver, times(1)).getTouch();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verify(touch, times(1)).flick(coords, 10, 20, 5);
    verifyNoMoreInteractions(touch);
    verify(fixture.listener, times(1)).beforeFlick(touch, coords, 10, 20, 5);
    verify(fixture.listener, times(1)).afterFlick(touch, coords, 10, 20, 5);
    verifyNoMoreInteractions(fixture.listener);
  }

}
