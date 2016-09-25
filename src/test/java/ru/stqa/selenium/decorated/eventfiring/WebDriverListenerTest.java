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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class WebDriverListenerTest {

  interface WebDriverWithJS extends WebDriver, JavascriptExecutor {}

  private static class Fixture {
    WebDriverWithJS mockedDriver;
    WebDriverListener listener;
    EventFiringWebDriver decoratedDriver;
    WebDriver driver;

    public Fixture() {
      mockedDriver = mock(WebDriverWithJS.class);
      listener = spy(new WebDriverListener() {});
      decoratedDriver = new EventFiringWebDriver(mockedDriver);
      decoratedDriver.addListener(listener);
      driver = decoratedDriver.getActivated();
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

    Object result = ((JavascriptExecutor) fixture.driver).executeScript("return arguments[0]", "test");

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

    Object result = ((JavascriptExecutor) fixture.driver).executeAsyncScript("return arguments[0]", "test");

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
    final Point location = mock(Point.class);

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
    final Dimension dimension = mock(Dimension.class);

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

/* @Test
  public void canFireEventForAlertAccept() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.TargetLocator mockedTarget = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.switchTo()).thenReturn(mockedTarget);
    when(mockedTarget.alert()).thenReturn(mockedAlert);

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.switchTo().alert().accept();

    verify(mockedAlert, times(1)).accept();
    verify(mockedListener, times(1)).beforeAccept(mockedAlert);
    verify(mockedListener, times(1)).afterAccept(mockedAlert);
  }
*/
}
