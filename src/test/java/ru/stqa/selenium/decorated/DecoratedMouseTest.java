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
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedMouseTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Mouse mocked;
    DecoratedMouse decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(Mouse.class);
      decorated = new DecoratedMouse(mocked, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<Mouse> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  @Test
  public void testClick() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.click(coords));
  }

  @Test
  public void testDoubleClick() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.doubleClick(coords));
  }

  @Test
  public void testContextClick() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.contextClick(coords));
  }

  @Test
  public void testMouseDown() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.mouseDown(coords));
  }

  @Test
  public void testMouseUp() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.mouseUp(coords));
  }

  @Test
  public void testMouseMove() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.mouseMove(coords));
  }

  @Test
  public void testMouseMoveWithShift() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.mouseMove(coords, 10, 20));
  }
}
