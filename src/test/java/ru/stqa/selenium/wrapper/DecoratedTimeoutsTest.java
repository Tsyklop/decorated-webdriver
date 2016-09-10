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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedTimeoutsTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Timeouts mockedTimeouts;
    DecoratedTimeouts decoratedTimeouts;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedTimeouts = mock(WebDriver.Timeouts.class);
      decoratedTimeouts = new DecoratedTimeouts(decoratedDriver, mockedTimeouts);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedTimeouts, sameInstance(fixture.decoratedTimeouts.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedTimeouts.getTopmostDecorated()));
  }

  @Test
  public void testImplicitlyWait() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedTimeouts.implicitlyWait(10, TimeUnit.SECONDS)).thenReturn(fixture.mockedTimeouts);

    assertThat(fixture.decoratedTimeouts, equalTo(fixture.decoratedTimeouts.implicitlyWait(10, TimeUnit.SECONDS)));
    verify(fixture.mockedTimeouts, times(1)).implicitlyWait(10, TimeUnit.SECONDS);
  }

  @Test
  public void testSetScriptTimeout() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedTimeouts.setScriptTimeout(10, TimeUnit.SECONDS)).thenReturn(fixture.mockedTimeouts);

    assertThat(fixture.decoratedTimeouts, equalTo(fixture.decoratedTimeouts.setScriptTimeout(10, TimeUnit.SECONDS)));
    verify(fixture.mockedTimeouts, times(1)).setScriptTimeout(10, TimeUnit.SECONDS);
  }

  @Test
  public void testPageLoadTimeout() {
    Fixture fixture = new Fixture();
    Point p = new Point(10, 20);
    when(fixture.mockedTimeouts.pageLoadTimeout(10, TimeUnit.SECONDS)).thenReturn(fixture.mockedTimeouts);

    assertThat(fixture.decoratedTimeouts, equalTo(fixture.decoratedTimeouts.pageLoadTimeout(10, TimeUnit.SECONDS)));
    verify(fixture.mockedTimeouts, times(1)).pageLoadTimeout(10, TimeUnit.SECONDS);
  }

}
