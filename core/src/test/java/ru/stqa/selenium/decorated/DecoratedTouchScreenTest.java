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

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedTouchScreenTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    TouchScreen mocked;
    DecoratedTouchScreen decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(TouchScreen.class);
      decorated = new DecoratedTouchScreen(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<TouchScreen> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  @Test
  void testSingleTap() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.singleTap(coords));
  }

  @Test
  void testDoubleTap() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.doubleTap(coords));
  }

  @Test
  void testLongPress() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.longPress(coords));
  }

  @Test
  void testDown() {
    verifyFunction($ -> $.down(10, 20));
  }

  @Test
  void testUp() {
    verifyFunction($ -> $.up(10, 20));
  }

  @Test
  void testMove() {
    verifyFunction($ -> $.move(10, 20));
  }

  @Test
  void testScroll() {
    verifyFunction($ -> $.scroll(10, 20));
  }

  @Test
  void testScrollRelative() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.scroll(coords, 10, 20));
  }

  @Test
  void testFlick() {
    verifyFunction($ -> $.flick(10, 20));
  }

  @Test
  void testFlickRelative() {
    final Coordinates coords = mock(Coordinates.class);
    verifyFunction($ -> $.flick(coords, 10, 20, 3));
  }

}
