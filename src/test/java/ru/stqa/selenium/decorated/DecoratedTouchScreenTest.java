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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedTouchScreenTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    TouchScreen mockedTouchScreen;
    DecoratedTouchScreen decoratedTouchScreen;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedTouchScreen = mock(TouchScreen.class);
      decoratedTouchScreen = new DecoratedTouchScreen(decoratedDriver, mockedTouchScreen);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedTouchScreen, sameInstance(fixture.decoratedTouchScreen.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedTouchScreen.getTopmostDecorated()));
  }

  @Test
  public void testSingleTap() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedTouchScreen.singleTap(coords);
    verify(fixture.mockedTouchScreen, times(1)).singleTap(coords);
  }

  @Test
  public void testDoubleTap() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedTouchScreen.doubleTap(coords);
    verify(fixture.mockedTouchScreen, times(1)).doubleTap(coords);
  }

  @Test
  public void testLongPress() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedTouchScreen.longPress(coords);
    verify(fixture.mockedTouchScreen, times(1)).longPress(coords);
  }

  @Test
  public void testDown() {
    Fixture fixture = new Fixture();

    fixture.decoratedTouchScreen.down(10, 20);
    verify(fixture.mockedTouchScreen, times(1)).down(10, 20);
  }

  @Test
  public void testUp() {
    Fixture fixture = new Fixture();

    fixture.decoratedTouchScreen.up(10, 20);
    verify(fixture.mockedTouchScreen, times(1)).up(10, 20);
  }

  @Test
  public void testMove() {
    Fixture fixture = new Fixture();

    fixture.decoratedTouchScreen.move(10, 20);
    verify(fixture.mockedTouchScreen, times(1)).move(10, 20);
  }

  @Test
  public void testScroll() {
    Fixture fixture = new Fixture();

    fixture.decoratedTouchScreen.scroll(10, 20);
    verify(fixture.mockedTouchScreen, times(1)).scroll(10, 20);
  }

  @Test
  public void testScrollRelative() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedTouchScreen.scroll(coords, 10, 20);
    verify(fixture.mockedTouchScreen, times(1)).scroll(coords, 10, 20);
  }

  @Test
  public void testFlick() {
    Fixture fixture = new Fixture();

    fixture.decoratedTouchScreen.flick(2, 3);
    verify(fixture.mockedTouchScreen, times(1)).flick(2, 3);
  }

  @Test
  public void testFlickRelative() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedTouchScreen.flick(coords, 10, 20, 3);
    verify(fixture.mockedTouchScreen, times(1)).flick(coords, 10, 20, 3);
  }

}
