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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedTimeoutsTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.Timeouts mocked;
    DecoratedTimeouts decorated;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mocked = mock(WebDriver.Timeouts.class);
      decorated = new DecoratedTimeouts(mocked, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();
    assertThat(fixture.mocked, sameInstance(fixture.decorated.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decorated.getTopmostDecorated()));
  }

  private void verifyFunction(Consumer<WebDriver.Timeouts> f) {
    Fixture fixture = new Fixture();
    f.accept(fixture.decorated);
    f.accept(verify(fixture.mocked, times(1)));
    verifyNoMoreInteractions(fixture.mocked);
  }

  @Test
  public void testImplicitlyWait() {
    verifyFunction($ -> $.implicitlyWait(10, TimeUnit.SECONDS));
  }

  @Test
  public void testSetScriptTimeout() {
    verifyFunction($ -> $.setScriptTimeout(10, TimeUnit.SECONDS));
  }

  @Test
  public void testPageLoadTimeout() {
    verifyFunction($ -> $.pageLoadTimeout(10, TimeUnit.SECONDS));
  }

}
