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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedWindowTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Window mockedWindow;
    DecoratedWindow decoratedWindow;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedWindow = mock(WebDriver.Window.class);
      decoratedWindow = new DecoratedWindow(mockedWindow, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedWindow, sameInstance(fixture.decoratedWindow.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedWindow.getTopmostDecorated()));
  }

  @Test
  public void testSetSize() {
    Fixture fixture = new Fixture();
    Dimension dim = mock(Dimension.class);

    fixture.decoratedWindow.setSize(dim);
    verify(fixture.mockedWindow, times(1)).setSize(dim);
  }

  @Test
  public void testSetPosition() {
    Fixture fixture = new Fixture();
    Point pos = mock(Point.class);

    fixture.decoratedWindow.setPosition(pos);
    verify(fixture.mockedWindow, times(1)).setPosition(pos);
  }

  @Test
  public void testGetSize() {
    Fixture fixture = new Fixture();
    Dimension dim = mock(Dimension.class);
    when(fixture.mockedWindow.getSize()).thenReturn(dim);

    assertThat(fixture.decoratedWindow.getSize(), sameInstance(dim));
    verify(fixture.mockedWindow, times(1)).getSize();
  }

  @Test
  public void testGetPosition() {
    Fixture fixture = new Fixture();
    Point pos = mock(Point.class);
    when(fixture.mockedWindow.getPosition()).thenReturn(pos);

    assertThat(fixture.decoratedWindow.getPosition(), sameInstance(pos));
    verify(fixture.mockedWindow, times(1)).getPosition();
  }

  @Test
  public void testMaximize() {
    Fixture fixture = new Fixture();

    fixture.decoratedWindow.maximize();
    verify(fixture.mockedWindow, times(1)).maximize();
  }

  @Test
  public void testFullscreen() {
    Fixture fixture = new Fixture();

    fixture.decoratedWindow.fullscreen();
    verify(fixture.mockedWindow, times(1)).fullscreen();
  }

}
