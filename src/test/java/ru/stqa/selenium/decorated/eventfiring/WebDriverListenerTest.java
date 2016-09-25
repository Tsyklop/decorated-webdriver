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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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
    verify(fixture.listener, times(1)).beforeGet(fixture.mockedDriver, "http://localhost/");
    verify(fixture.listener, times(1)).afterGet(fixture.mockedDriver, "http://localhost/");
  }

  @Test
  public void canFireEventForGetCurrentUrl() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getCurrentUrl()).thenReturn("http://localhost/");

    assertEquals(fixture.driver.getCurrentUrl(), "http://localhost/");

    verify(fixture.mockedDriver, times(1)).getCurrentUrl();
    verify(fixture.listener, times(1)).beforeGetCurrentUrl(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetCurrentUrl("http://localhost/", fixture.mockedDriver);
  }

  @Test
  public void canFireEventForGetTitle() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getTitle()).thenReturn("Home page");

    assertEquals(fixture.driver.getTitle(), "Home page");

    verify(fixture.mockedDriver, times(1)).getTitle();
    verify(fixture.listener, times(1)).beforeGetTitle(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetTitle("Home page", fixture.mockedDriver);
  }

  @Test
  public void canFireEventForFindElement() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);
    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    WebElement result = fixture.driver.findElement(By.id("id"));

    assertEquals(result, mockedElement);

    verify(fixture.mockedDriver, times(1)).findElement(By.id("id"));
    verify(fixture.listener, times(1)).beforeFindElement(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElement(mockedElement, fixture.mockedDriver, By.id("id"));
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
    verify(fixture.listener, times(1)).beforeFindElements(fixture.mockedDriver, By.id("id"));
    verify(fixture.listener, times(1)).afterFindElements(list, fixture.mockedDriver, By.id("id"));
  }

  @Test
  public void canFireEventForGetPageSource() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getPageSource()).thenReturn("<html></html>");

    assertEquals(fixture.driver.getPageSource(), "<html></html>");

    verify(fixture.mockedDriver, times(1)).getPageSource();
    verify(fixture.listener, times(1)).beforeGetPageSource(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetPageSource("<html></html>", fixture.mockedDriver);
  }

  @Test
  public void canFireEventForClose() {
    Fixture fixture = new Fixture();

    fixture.driver.close();

    verify(fixture.mockedDriver, times(1)).close();
    verify(fixture.listener, times(1)).beforeClose(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterClose(fixture.mockedDriver);
  }

  @Test
  public void canFireEventForQuit() {
    Fixture fixture = new Fixture();

    fixture.driver.quit();

    verify(fixture.mockedDriver, times(1)).quit();
    verify(fixture.listener, times(1)).beforeQuit(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterQuit(fixture.mockedDriver);
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
    verify(fixture.listener, times(1)).beforeGetWindowHandles(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetWindowHandles(handles, fixture.mockedDriver);
  }

  @Test
  public void canFireEventForGetWindowHandle() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.getWindowHandle()).thenReturn("window1");

    assertEquals(fixture.driver.getWindowHandle(), "window1");

    verify(fixture.mockedDriver, times(1)).getWindowHandle();
    verify(fixture.listener, times(1)).beforeGetWindowHandle(fixture.mockedDriver);
    verify(fixture.listener, times(1)).afterGetWindowHandle("window1", fixture.mockedDriver);
  }

  @Test
  public void canFireEventForExecuteScript() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.executeScript("return arguments[0]", "test")).thenReturn("result");

    Object result = ((JavascriptExecutor) fixture.driver).executeScript("return arguments[0]", "test");

    assertEquals(result, "result");

    verify(fixture.mockedDriver, times(1)).executeScript("return arguments[0]", "test");
    verify(fixture.listener, times(1)).beforeExecuteScript(fixture.mockedDriver, "return arguments[0]", "test");
    verify(fixture.listener, times(1)).afterExecuteScript("result", fixture.mockedDriver, "return arguments[0]", "test");
  }

  @Test
  public void canFireEventForExecuteAsyncScript() {
    Fixture fixture = new Fixture();

    when(fixture.mockedDriver.executeAsyncScript("return arguments[0]", "test")).thenReturn("result");

    Object result = ((JavascriptExecutor) fixture.driver).executeAsyncScript("return arguments[0]", "test");

    assertEquals(result, "result");

    verify(fixture.mockedDriver, times(1)).executeAsyncScript("return arguments[0]", "test");
    verify(fixture.listener, times(1)).beforeExecuteAsyncScript(fixture.mockedDriver, "return arguments[0]", "test");
    verify(fixture.listener, times(1)).afterExecuteAsyncScript("result", fixture.mockedDriver, "return arguments[0]", "test");
  }

  @Test
  public void canFireEventForWebElementClick() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).click();

    verify(mockedElement, times(1)).click();
    verify(fixture.listener, times(1)).beforeClick(mockedElement);
    verify(fixture.listener, times(1)).afterClick(mockedElement);
  }

  @Test
  public void canFireEventForWebElementSubmit() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).submit();

    verify(mockedElement, times(1)).submit();
    verify(fixture.listener, times(1)).beforeSubmit(mockedElement);
    verify(fixture.listener, times(1)).afterSubmit(mockedElement);
  }

  @Test
  public void canFireEventForWebElementSendKeys() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).sendKeys("test");

    verify(mockedElement, times(1)).sendKeys("test");
    verify(fixture.listener, times(1)).beforeSendKeys(mockedElement, "test");
    verify(fixture.listener, times(1)).afterSendKeys(mockedElement, "test");
  }

  @Test
  public void canFireEventForWebElementClear() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    fixture.driver.findElement(By.id("id")).clear();

    verify(mockedElement, times(1)).clear();
    verify(fixture.listener, times(1)).beforeClear(mockedElement);
    verify(fixture.listener, times(1)).afterClear(mockedElement);
  }

  @Test
  public void canFireEventForWebElementGetTagName() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getTagName()).thenReturn("input");

    assertEquals(fixture.driver.findElement(By.id("id")).getTagName(), "input");

    verify(mockedElement, times(1)).getTagName();
    verify(fixture.listener, times(1)).beforeGetTagName(mockedElement);
    verify(fixture.listener, times(1)).afterGetTagName("input", mockedElement);
  }

  @Test
  public void canFireEventForWebElementGetAttribute() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getAttribute("name")).thenReturn("test");

    assertEquals(fixture.driver.findElement(By.id("id")).getAttribute("name"), "test");

    verify(mockedElement, times(1)).getAttribute("name");
    verify(fixture.listener, times(1)).beforeGetAttribute(mockedElement, "name");
    verify(fixture.listener, times(1)).afterGetAttribute("test", mockedElement, "name");
  }

  @Test
  public void canFireEventForWebElementIsSelected() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isSelected(), true);

    verify(mockedElement, times(1)).isSelected();
    verify(fixture.listener, times(1)).beforeIsSelected(mockedElement);
    verify(fixture.listener, times(1)).afterIsSelected(true, mockedElement);
  }

  @Test
  public void canFireEventForWebElementIsEnabled() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isEnabled(), true);

    verify(mockedElement, times(1)).isEnabled();
    verify(fixture.listener, times(1)).beforeIsEnabled(mockedElement);
    verify(fixture.listener, times(1)).afterIsEnabled(true, mockedElement);
  }

  @Test
  public void canFireEventForWebElementGetText() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn("test");

    assertEquals(fixture.driver.findElement(By.id("id")).getText(), "test");

    verify(mockedElement, times(1)).getText();
    verify(fixture.listener, times(1)).beforeGetText(mockedElement);
    verify(fixture.listener, times(1)).afterGetText("test", mockedElement);
  }

  @Test
  public void canFireEventForWebElementIsDisplayed() {
    Fixture fixture = new Fixture();

    final WebElement mockedElement = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(true);

    assertEquals(fixture.driver.findElement(By.id("id")).isDisplayed(), true);

    verify(mockedElement, times(1)).isDisplayed();
    verify(fixture.listener, times(1)).beforeIsDisplayed(mockedElement);
    verify(fixture.listener, times(1)).afterIsDisplayed(true, mockedElement);
  }

/*  @Test
  public void canFireEventForWebElementGetText() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn("text");

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    String result = driver.findElement(By.id("id")).getText();

    assertEquals(result, "text");

    verify(mockedElement, times(1)).getText();
    verify(mockedListener, times(1)).beforeGetText(mockedElement);
    verify(mockedListener, times(1)).afterGetText(mockedElement, "text");
  }

  @Test
  public void canFireEventForWebElementGetAttribute() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getAttribute("value")).thenReturn("text");

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    String result = driver.findElement(By.id("id")).getAttribute("value");

    assertEquals(result, "text");

    verify(mockedElement, times(1)).getAttribute("value");
    verify(mockedListener, times(1)).beforeGetAttribute(mockedElement, "value");
    verify(mockedListener, times(1)).afterGetAttribute(mockedElement, "text", "value");
  }

  @Test
  public void canFireEventForRefresh() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.Navigation mockedNavigation = mock(WebDriver.Navigation.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.navigate()).thenReturn(mockedNavigation);

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.navigate().refresh();

    verify(mockedNavigation, times(1)).refresh();
    verify(mockedListener, times(1)).beforeRefresh(mockedNavigation);
    verify(mockedListener, times(1)).afterRefresh(mockedNavigation);
  }

  @Test
  public void canFireEventForFindElementInElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElement(By.id("id2"))).thenReturn(mockedElement2);

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    WebElement result = driver.findElement(By.id("id1")).findElement(By.id("id2"));

    assertEquals(result, mockedElement2);

    verify(mockedElement, times(1)).findElement(By.id("id2"));
    verify(mockedListener, times(1)).beforeFindElement(mockedDriver, By.id("id1"));
    verify(mockedListener, times(1)).afterFindElement(mockedDriver, mockedElement, By.id("id1"));
    verify(mockedListener, times(1)).beforeFindElement(mockedElement, By.id("id2"));
    verify(mockedListener, times(1)).afterFindElement(mockedElement, mockedElement2, By.id("id2"));
  }

  @Test
  public void canFireEventForFindElementsInElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> list = new ArrayList<WebElement>();
    list.add(mockedElement2);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElements(By.id("id2"))).thenReturn(list);

    EventFiringWebDriver wrapper = new EventFiringWebDriver(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    List<WebElement> result = driver.findElement(By.id("id1")).findElements(By.id("id2"));

    assertEquals(result, list);

    verify(mockedElement, times(1)).findElements(By.id("id2"));
    verify(mockedListener, times(1)).beforeFindElement(mockedDriver, By.id("id1"));
    verify(mockedListener, times(1)).afterFindElement(mockedDriver, mockedElement, By.id("id1"));
    verify(mockedListener, times(1)).beforeFindElements(mockedElement, By.id("id2"));
    verify(mockedListener, times(1)).afterFindElements(mockedElement, list, By.id("id2"));
  }

  @Test
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
