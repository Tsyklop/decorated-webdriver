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
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Keyboard;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedKeyboardTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    Keyboard mockedKeyboard;
    DecoratedKeyboard decoratedKeyboard;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedKeyboard = mock(Keyboard.class);
      decoratedKeyboard = new DecoratedKeyboard(decoratedDriver, mockedKeyboard);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedKeyboard, sameInstance(fixture.decoratedKeyboard.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedKeyboard.getTopmostDecorated()));
  }

  @Test
  public void testSendKeys() {
    Fixture fixture = new Fixture();

    fixture.decoratedKeyboard.sendKeys("test");
    verify(fixture.mockedKeyboard, times(1)).sendKeys("test");
  }

  @Test
  public void testPressKey() {
    Fixture fixture = new Fixture();

    fixture.decoratedKeyboard.pressKey("t");
    verify(fixture.mockedKeyboard, times(1)).pressKey("t");
  }

  @Test
  public void testReleaseKey() {
    Fixture fixture = new Fixture();

    fixture.decoratedKeyboard.releaseKey("t");
    verify(fixture.mockedKeyboard, times(1)).releaseKey("t");
  }

}
