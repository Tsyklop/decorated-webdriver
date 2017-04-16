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
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class DecoratedCoordinatesTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Coordinates mocked;
    DecoratedCoordinates decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(Coordinates.class);
      decorated = new DecoratedCoordinates(mocked, decoratedDriver);
    }
  }

  @Test
  void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private <R> void verifyFunction(Function<Coordinates, R> f, R result) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);
    assertThat(f.apply(fixture.decorated), equalTo(result));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  private <R> void verifyDecoratingFunction(Function<Coordinates, R> f, R result, Consumer<R> p) {
    Fixture fixture = new Fixture();
    when(f.apply(fixture.mocked)).thenReturn(result);

    R proxy = f.apply(fixture.decorated);
    assertThat(result, not(sameInstance(proxy)));
    f.apply(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);

    p.accept(proxy);
    p.accept(verify(result, times(1)));
    verifyNoMoreInteractions(result);
  }

  @Test
  void testOnScreen() {
    verifyFunction(Coordinates::onScreen, new Point(10, 20));
  }

  @Test
  void testInViewPort() {
    verifyFunction(Coordinates::inViewPort, new Point(10, 20));
  }

  @Test
  void testOnPage() {
    verifyFunction(Coordinates::onPage, new Point(10, 20));
  }

  @Test
  void testGetAuxiliary() {
    final WebElement element = mock(WebElement.class);
    verifyDecoratingFunction($ -> (WebElement) $.getAuxiliary(), element, WebElement::click);
  }

}
