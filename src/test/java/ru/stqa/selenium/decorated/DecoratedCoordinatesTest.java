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
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedCoordinatesTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Coordinates mockedCoords;
    DecoratedCoordinates decoratedCoords;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedCoords = mock(Coordinates.class);
      decoratedCoords = new DecoratedCoordinates(decoratedDriver, mockedCoords);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedCoords, sameInstance(fixture.decoratedCoords.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedCoords.getTopmostDecorated()));
  }

  @Test
  public void testOnScreen() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedCoords.onScreen()).thenReturn(p);

    assertThat(fixture.decoratedCoords.onScreen(), equalTo(p));
    verify(fixture.mockedCoords, times(1)).onScreen();
  }

  @Test
  public void testInViewPort() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedCoords.inViewPort()).thenReturn(p);

    assertThat(fixture.decoratedCoords.inViewPort(), equalTo(p));
    verify(fixture.mockedCoords, times(1)).inViewPort();
  }

  @Test
  public void testOnPage() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedCoords.onPage()).thenReturn(p);

    assertThat(fixture.decoratedCoords.onPage(), equalTo(p));
    verify(fixture.mockedCoords, times(1)).onPage();
  }

  @Test
  public void testGetAuxiliary() {
    Fixture fixture = new Fixture();
    WebElement mockedElement = mock(WebElement.class);
    when(fixture.mockedCoords.getAuxiliary()).thenReturn(mockedElement);

    WebElement proxy = (WebElement) fixture.decoratedCoords.getAuxiliary();
    assertThat(mockedElement, not(sameInstance(proxy)));
    verify(fixture.mockedCoords, times(1)).getAuxiliary();

    proxy.isDisplayed();
    verify(mockedElement, times(1)).isDisplayed();
  }

}
