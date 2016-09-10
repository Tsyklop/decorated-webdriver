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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedMouseTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Mouse mockedMouse;
    DecoratedMouse decoratedMouse;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedMouse = mock(Mouse.class);
      decoratedMouse = new DecoratedMouse(decoratedDriver, mockedMouse);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedMouse, sameInstance(fixture.decoratedMouse.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedMouse.getTopmostDecorated()));
  }

  @Test
  public void testClick() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.click(coords);
    verify(fixture.mockedMouse, times(1)).click(coords);
  }

  @Test
  public void testDoubleClick() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.doubleClick(coords);
    verify(fixture.mockedMouse, times(1)).doubleClick(coords);
  }

  @Test
  public void testContextClick() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.contextClick(coords);
    verify(fixture.mockedMouse, times(1)).contextClick(coords);
  }

  @Test
  public void testMouseDown() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.mouseDown(coords);
    verify(fixture.mockedMouse, times(1)).mouseDown(coords);
  }

  @Test
  public void testMouseUp() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.mouseUp(coords);
    verify(fixture.mockedMouse, times(1)).mouseUp(coords);
  }

  @Test
  public void testMouseMove() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.mouseMove(coords);
    verify(fixture.mockedMouse, times(1)).mouseMove(coords);
  }

  @Test
  public void testMouseMoveWithShift() {
    Fixture fixture = new Fixture();
    Coordinates coords = mock(Coordinates.class);

    fixture.decoratedMouse.mouseMove(coords, 10, 20);
    verify(fixture.mockedMouse, times(1)).mouseMove(coords, 10, 20);
  }

}
