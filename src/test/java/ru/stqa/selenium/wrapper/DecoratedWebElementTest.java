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
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedWebElementTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebElement mockedElement;
    DecoratedWebElement decoratedElement;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedElement = mock(WebElement.class, withSettings().extraInterfaces(Locatable.class));
      decoratedElement = new DecoratedWebElement(decoratedDriver, mockedElement);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedElement, sameInstance(fixture.decoratedElement.getOriginal()));
    assertThat(fixture.mockedElement, sameInstance(fixture.decoratedElement.getWrappedElement()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedElement.getTopmostDecorated()));
  }

  @Test
  public void testSendKeys() {
    Fixture fixture = new Fixture();

    fixture.decoratedElement.sendKeys("test");
    verify(fixture.mockedElement, times(1)).sendKeys("test");
  }

  @Test
  public void testClick() {
    Fixture fixture = new Fixture();

    fixture.decoratedElement.click();
    verify(fixture.mockedElement, times(1)).click();
  }

  @Test
  public void testSubmit() {
    Fixture fixture = new Fixture();

    fixture.decoratedElement.submit();
    verify(fixture.mockedElement, times(1)).submit();
  }

  @Test
  public void testClear() {
    Fixture fixture = new Fixture();

    fixture.decoratedElement.clear();
    verify(fixture.mockedElement, times(1)).clear();
  }

  @Test
  public void testGetText() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.getText()).thenReturn("test");

    assertThat(fixture.decoratedElement.getText(), equalTo("test"));
    verify(fixture.mockedElement, times(1)).getText();
  }

  @Test
  public void testGetTagName() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.getTagName()).thenReturn("test");

    assertThat(fixture.decoratedElement.getTagName(), equalTo("test"));
    verify(fixture.mockedElement, times(1)).getTagName();
  }

  @Test
  public void testGetAttribute() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.getAttribute("value")).thenReturn("test");

    assertThat(fixture.decoratedElement.getAttribute("value"), equalTo("test"));
    verify(fixture.mockedElement, times(1)).getAttribute("value");
  }

  @Test
  public void testIsSelected() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.isSelected()).thenReturn(true);

    assertThat(fixture.decoratedElement.isSelected(), equalTo(true));
    verify(fixture.mockedElement, times(1)).isSelected();
  }

  @Test
  public void testIsEnabled() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.isEnabled()).thenReturn(true);

    assertThat(fixture.decoratedElement.isEnabled(), equalTo(true));
    verify(fixture.mockedElement, times(1)).isEnabled();
  }

  @Test
  public void testIsDisplayed() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.isDisplayed()).thenReturn(true);

    assertThat(fixture.decoratedElement.isDisplayed(), equalTo(true));
    verify(fixture.mockedElement, times(1)).isDisplayed();
  }

  @Test
  public void testGetLocation() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedElement.getLocation()).thenReturn(p);

    assertThat(fixture.decoratedElement.getLocation(), equalTo(p));
    verify(fixture.mockedElement, times(1)).getLocation();
  }

  @Test
  public void testGetSize() {
    Fixture fixture = new Fixture();
    Dimension d = new Dimension(100, 200);
    when(fixture.mockedElement.getSize()).thenReturn(d);

    assertThat(fixture.decoratedElement.getSize(), equalTo(d));
    verify(fixture.mockedElement, times(1)).getSize();
  }

  @Test
  public void testGetRect() {
    Fixture fixture = new Fixture();
    Rectangle r = new Rectangle(new Point(10, 20), new Dimension(100, 200));
    when(fixture.mockedElement.getRect()).thenReturn(r);

    assertThat(fixture.decoratedElement.getRect(), equalTo(r));
    verify(fixture.mockedElement, times(1)).getRect();
  }

  @Test
  public void testGetCssValue() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.getCssValue("color")).thenReturn("red");

    assertThat(fixture.decoratedElement.getCssValue("color"), equalTo("red"));
    verify(fixture.mockedElement, times(1)).getCssValue("color");
  }

  @Test
  public void testGetCoordinates() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);
    when(((Locatable) fixture.mockedElement).getCoordinates()).thenReturn(coords);

    Coordinates proxy = fixture.decoratedElement.getCoordinates();
    assertThat(coords, not(sameInstance(proxy)));
    verify((Locatable) fixture.mockedElement, times(1)).getCoordinates();

    proxy.onScreen();
    verify(coords, times(1)).onScreen();
  }

  @Test
  public void testGetScreenshotAs() {
    Fixture fixture = new Fixture();
    when(fixture.mockedElement.getScreenshotAs(OutputType.BASE64)).thenReturn("xxx");

    assertThat(fixture.decoratedElement.getScreenshotAs(OutputType.BASE64), equalTo("xxx"));
    verify(fixture.mockedElement, times(1)).getScreenshotAs(OutputType.BASE64);
  }

  @Test
  public void testFindElement() {
    Fixture fixture = new Fixture();
    WebElement found = mock(WebElement.class);
    when(fixture.mockedElement.findElement(By.id("test"))).thenReturn(found);

    WebElement proxy = fixture.decoratedElement.findElement(By.id("test"));
    assertThat(found, not(sameInstance(proxy)));
    verify(fixture.mockedElement, times(1)).findElement(By.id("test"));

    proxy.isDisplayed();
    verify(found, times(1)).isDisplayed();
  }

  @Test
  public void testFindElements() {
    Fixture fixture = new Fixture();
    WebElement found = mock(WebElement.class);
    List<WebElement> list = new ArrayList<WebElement>();
    list.add(found);
    when(fixture.mockedElement.findElements(By.id("test"))).thenReturn(list);

    List<WebElement> proxyList = fixture.decoratedElement.findElements(By.id("test"));
    // TODO: Why same instance?????
    assertThat(list, sameInstance(proxyList));
    assertThat(found, not(sameInstance(proxyList.get(0))));
    verify(fixture.mockedElement, times(1)).findElements(By.id("test"));

    proxyList.get(0).isDisplayed();
    verify(found, times(1)).isDisplayed();
  }

}
